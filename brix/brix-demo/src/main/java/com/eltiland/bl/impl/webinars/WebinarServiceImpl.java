package com.eltiland.bl.impl.webinars;

import com.eltiland.bl.utils.http.DeleteMethod;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.thoughtworks.xstream.XStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Webinar service implementation, based on Webinar.ru API.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class WebinarServiceImpl implements WebinarServiceManager {

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    private static final String WEBINAR_RU_API_URL = "http://connect1.webinar.ru/api/1/";

    private static final String AUTHVERIFY_ACTION = "authverify";
    private static final String SESSION_ACTION = "session";
    private static final String INVITE_ACTION = "invitee";
    private static final String INVITEES_ACTION = "invitees";

    private class SimpleReply implements Serializable {
        int id;
        String message;
    }

    private String getAuthString() {
        String authString = eltilandProps.getProperty("webinar.login") +
                ":" + eltilandProps.getProperty("webinar.passw");
        return "Basic " + new String(Base64.encodeBase64(authString.getBytes()));
    }

    private HttpURLConnection openConnection(String action) throws EltilandManagerException {
        String url = WEBINAR_RU_API_URL + eltilandProps.getProperty("webinar.login") + "/" + action;
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            throw new EltilandManagerException("Cannot open connection", e);
        }
        return con;
    }

    private String createJsonParameters(Map<String, String> params) {
        if (params.isEmpty()) {
            return "";
        }

        String result = "input_type=json&rest_data={";

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entryValue = iterator.next();
            result += "\"" + entryValue.getKey() + "\":\"" + entryValue.getValue() + "\"";
            if (iterator.hasNext()) {
                result += ",";
            }
        }
        result += "}";

        return result;
    }

    @Override
    public void authenticate() throws EltilandManagerException {
        HttpURLConnection con = openConnection(AUTHVERIFY_ACTION);
        if (con != null) {
            con.setRequestProperty("Authorization", getAuthString());
            try {
                con.setRequestMethod("GET");
            } catch (ProtocolException e) {
                throw new EltilandManagerException("Cannot set request method", e);
            }
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                rd.close();
            } catch (IOException e) {
                throw new EltilandManagerException("Cannot get answer from server. May be API access is closed.", e);
            }
        }
    }

    @Override
    public boolean createWebinar(Webinar webinar) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            PutMethod method = new PutMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + SESSION_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate = dateFormat.format(webinar.getStartDate());

            HashMap<String, String> params = new HashMap<>();
            params.put("topic", webinar.getName());
            params.put("duration", String.valueOf(webinar.getDuration()));
            params.put("start_time", startDate);
            params.put("timezone", "UTC+2");
            params.put("password", webinar.getPassword());

            method.setRequestEntity(new StringRequestEntity(createJsonParameters(params), "text/html", "UTF-8"));

            int statusCode = client.executeMethod(method);
            if (statusCode != 201) {
                return false;
            }

            XStream xstream = new XStream();
            xstream.alias("xml", SimpleReply.class);
            SimpleReply reply = (SimpleReply) xstream.fromXML(method.getResponseBodyAsString());
            webinar.setWebinarid((long) reply.id);

        } catch (IOException e) {
            throw new EltilandManagerException("Cannot add user to webinar event - most likely some error in parameters.", e);
        }
        return true;
    }


    @Override
    public boolean removeWebinar(Webinar webinar) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            DeleteMethod method = new DeleteMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + SESSION_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            NameValuePair[] body = {new NameValuePair("id", webinar.getWebinarid().toString())};
            method.setRequestBody(body);

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return false;
            }
        } catch (IOException e) {
            throw new EltilandManagerException("Cannot delete webinar event - most likely some error in parameters.", e);
        }
        return true;
    }

    @Override
    public boolean updateWebinar(Webinar webinar) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + SESSION_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDate = dateFormat.format(webinar.getStartDate());

            HashMap<String, String> params = new HashMap<>();
            params.put("id", webinar.getWebinarid().toString());
            params.put("topic", webinar.getName());
            params.put("duration", String.valueOf(webinar.getDuration()));
            params.put("start_time", startDate);
            params.put("timezone", "UTC+2");

            method.setRequestEntity(new StringRequestEntity(createJsonParameters(params), "text/html", "UTF-8"));

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return false;
            }
        } catch (IOException e) {
            throw new EltilandManagerException("Cannot delete webinar event - most likely some error in parameters.", e);
        }
        return true;
    }

    @Override
    public boolean addUser(WebinarUserPayment user) throws EltilandManagerException {
     //   try {
         /*   HttpClient client = new HttpClient();
            PutMethod method = new PutMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + INVITE_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            Webinar webinar = user.getWebinar();
            HashMap<String, String> params = new HashMap<>();
            params.put("session_id", webinar.getWebinarid().toString());
            params.put("email", user.getUserEmail());
            params.put("first_name", user.getUserName());
            params.put("last_name", user.getUserSurname());

            String role = "2";
            if (user.getRole().equals(WebinarUserPayment.Role.MODERATOR)) {
                role = "1";
            } else if (user.getRole().equals(WebinarUserPayment.Role.OBSERVER)) {
                role = "3";
            }
            params.put("role", role);

            method.setRequestEntity(new StringRequestEntity(createJsonParameters(params), "text/html", "UTF-8"));

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return false;
            }

            XStream xstream = new XStream();
            xstream.alias("xml", SimpleReply.class);
            SimpleReply reply = (SimpleReply) xstream.fromXML(method.getResponseBodyAsString());
            user.setUserid((long) reply.id);*/

     /*   } catch (IOException e) {
            throw new EltilandManagerException("Cannot add user to webinar event - most likely some error in parameters.", e);
        }*/
        return true;
    }

    @Override
    public boolean removeUser(WebinarUserPayment user) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            DeleteMethod method = new DeleteMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + INVITE_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            Webinar webinar = user.getWebinar();

            HashMap<String, String> params = new HashMap<>();
            params.put("session_id", webinar.getWebinarid().toString());
            params.put("user_id", user.getUserid().toString());

            method.setRequestEntity(new StringRequestEntity(createJsonParameters(params), "text/html", "UTF-8"));

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return false;
            }
        } catch (IOException e) {
            throw new EltilandManagerException("Cannot delete webinar event - most likely some error in parameters.", e);
        }
        return true;
    }

    @Override
    public boolean updateUser(WebinarUserPayment user) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(WEBINAR_RU_API_URL
                    + eltilandProps.getProperty("webinar.login") + "/" + INVITE_ACTION);
            method.addRequestHeader("Authorization", getAuthString());

            Webinar webinar = user.getWebinar();
            HashMap<String, String> params = new HashMap<>();
            params.put("session_id", webinar.getWebinarid().toString());
            params.put("user_id", user.getUserid().toString());
            String role = "2";
            if (user.getRole().equals(WebinarUserPayment.Role.MODERATOR)) {
                role = "1";
            } else if (user.getRole().equals(WebinarUserPayment.Role.OBSERVER)) {
                role = "3";
            }
            params.put("role", role);

            method.setRequestEntity(new StringRequestEntity(createJsonParameters(params), "text/html", "UTF-8"));

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return false;
            }
        } catch (IOException e) {
            throw new EltilandManagerException(
                    "Cannot update user in webinar event - most likely some error in parameters.", e);
        }
        return true;
    }

    @Override
    public Map<String, String> getUsersData(Webinar webinar) throws EltilandManagerException {
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(WEBINAR_RU_API_URL + eltilandProps.getProperty("webinar.login") + "/"
                    + INVITEES_ACTION + "/id/" + webinar.getWebinarid().toString());
            method.addRequestHeader("Authorization", getAuthString());

            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
                return null;
            }

            Pattern generalPattern = Pattern.compile("<email>[\\s\\S]*?<\\/personal_session_link>");
            Pattern emailPattern = Pattern.compile("<email>[\\s\\S]*?<\\/email>");
            Pattern linkPattern = Pattern.compile("<personal_session_link>[\\s\\S]*?<\\/personal_session_link>");

            Matcher m = generalPattern.matcher(method.getResponseBodyAsString());
            List<String> data = new ArrayList<>();
            while (m.find()) {
                data.add(m.group());
            }

            Map<String, String> result = new HashMap<>();
            for (String item : data) {
                Matcher email_match = emailPattern.matcher(item);
                Matcher link_match = linkPattern.matcher(item);
                email_match.find();
                link_match.find();

                String email = email_match.group().replace("<email>", "").replace("</email>", "");
                String link = link_match.group().replace("<personal_session_link>", "")
                        .replace("</personal_session_link>", "");

                result.put(email, link);
            }
            return result;
        } catch (IOException e) {
            throw new EltilandManagerException(
                    "Cannot get webinar users - most likely some error in parameters.", e);
        }
    }
}
