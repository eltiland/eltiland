package com.eltiland.bl.impl.webinars;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Webinar service implementation, based on Webinar.ru v3 API
 */
@Component
@Service("webinarServiceV3Impl")
public class WebinarServiceV3Impl implements WebinarServiceManager {

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;
    @Autowired
    private WebinarUserPaymentManager userPaymentManager;
    @Autowired
    private GenericManager genericManager;

    private static final String WEBINAR_RU_API_URL = "https://userapi.webinar.ru/v3/";
    private static final String CREATE_EVENT_ACTION = "events";
    private static final String REGISTER_USER_ACTION = "register";

    @Override
    public void authenticate() throws EltilandManagerException {
    }

    private static String getContentFromInputStream(InputStream is) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private static List<BasicNameValuePair> getStartDateString(Date date) {
        List<BasicNameValuePair> params = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        params.add(new BasicNameValuePair("startsAt[date][year]", String.valueOf(calendar.get(Calendar.YEAR))));
        params.add(new BasicNameValuePair("startsAt[date][month]", String.valueOf(calendar.get(Calendar.MONTH)+1)));
        params.add(new BasicNameValuePair("startsAt[date][day]", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))));
        params.add(new BasicNameValuePair("startsAt[time][hour]", String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))));
        params.add(new BasicNameValuePair("startsAt[time][minute]", String.valueOf(calendar.get(Calendar.MINUTE))));

        return params;
    }

    private static String getDurationString(int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        return String.format("PT%dH%dM0S", hours, minutes);
    }

    @Override
    public Long createEvent(Webinar event) throws WebinarException {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(WEBINAR_RU_API_URL + CREATE_EVENT_ACTION);

        httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

        httppost.setHeader("x-auth-token", eltilandProps.getProperty("webinar.apikey"));

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", event.getName()));
        params.add(new BasicNameValuePair("access", "4"));
        for (BasicNameValuePair param : getStartDateString(event.getStartDate())) {
            params.add(param);
        }
        params.add(new BasicNameValuePair("duration", getDurationString(event.getDuration())));

        try {

            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 400) {
                throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE_PARAMS);
            }
            if (statusCode == 401 || statusCode == 403) {
                throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE_AUTH);
            }

            String result = getContentFromInputStream(response.getEntity().getContent());

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(result);
            JSONObject jsonObject = (JSONObject) obj;
            return (Long) jsonObject.get("eventId");

        } catch (IOException | ParseException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE, e);
        }
    }

    @Override
    public boolean createWebinar(Webinar webinar) throws EltilandManagerException {
        return false;
    }

    @Override
    public boolean removeWebinar(Webinar webinar) throws EltilandManagerException {
        return false;
    }

    @Override
    public boolean updateWebinar(Webinar webinar) throws EltilandManagerException {
        return false;
    }

    @Override
    public boolean addUser(WebinarUserPayment user) throws WebinarException {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        genericManager.initialize(user, user.getWebinar());
        Long eventId = user.getWebinar().getEventId();
        HttpPost httppost = new HttpPost(
                WEBINAR_RU_API_URL + CREATE_EVENT_ACTION + "/" + eventId + "/" + REGISTER_USER_ACTION);

        httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

        httppost.setHeader("x-auth-token", eltilandProps.getProperty("webinar.apikey"));

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("email", user.getUserEmail()));
        params.add(new BasicNameValuePair("name", user.getName()));
        params.add(new BasicNameValuePair("secondName", user.getUserSurname()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 400) {
                throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE_PARAMS);
            }
            if (statusCode == 401 || statusCode == 403) {
                throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE_AUTH);
            }

            String result = getContentFromInputStream(response.getEntity().getContent());

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(result);
            JSONObject jsonObject = (JSONObject) obj;
            Long userId = (Long) jsonObject.get("participationId");
            String link = (String) jsonObject.get("link");

            user.setUserid(userId);
            user.setWebinarlink(link);
            userPaymentManager.update(user);

            return true;
        } catch (IOException | ParseException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE, e);
        } catch (EltilandManagerException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE, e);
        }
    }

    @Override
    public boolean removeUser(WebinarUserPayment user) throws EltilandManagerException {
        return false;
    }

    @Override
    public boolean updateUser(WebinarUserPayment user) throws EltilandManagerException {
        return false;
    }

    @Override
    public Map<String, String> getUsersData(Webinar webinar) throws EltilandManagerException {
        return null;
    }
}
