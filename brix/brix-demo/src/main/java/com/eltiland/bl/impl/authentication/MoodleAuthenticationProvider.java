package com.eltiland.bl.impl.authentication;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author knorr
 * @version 1.0
 * @since 7/31/12
 */
public class MoodleAuthenticationProvider extends AbstractJakartaAuthenticationProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(MoodleAuthenticationProvider.class);

    private final String LOGON_SITE = "moodle.logicify.lan";

    private final String PATH_DOMAIN = "/login/index.php";

    @Override
    protected String getLogonSiteDomain() {
        return LOGON_SITE;
    }

    @Override
    public Collection<Cookie> authenticate(String login, String password) {

        PostMethod authpost = new PostMethod(PATH_DOMAIN);
        NameValuePair userName = new NameValuePair("username", login);
        NameValuePair userPassword = new NameValuePair("password", password);
        authpost.setRequestBody(new NameValuePair[]{userName, userPassword});

        try {
            getClient().executeMethod(authpost);
        } catch (IOException e) {
            LOGGER.error("error", e);
        } finally {
            authpost.releaseConnection();
        }
        LOGGER.info("Login form post: " + authpost.getStatusLine().toString());

        //Get cookies from moodle.
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(MAIN_DOMAIN, LOGON_PORT, "/", false, getClient().getState().getCookies());

        LOGGER.info("Logon cookies:");
        if (logoncookies.length == 0) {
            LOGGER.info("None cookies has been received");
        } else {
            for (int i = 0; i < logoncookies.length; i++) {
                LOGGER.info(logoncookies[i].toString());
            }
        }

        //moodle returns response with verification link. Check it!
        int statuscode = authpost.getStatusCode();
        if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (statuscode == HttpStatus.SC_SEE_OTHER) ||
                (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {

            Header header = authpost.getResponseHeader("location");
            if (header != null) {
                String newuri = header.getValue();
                if ((newuri == null) || (newuri.equals(""))) {
                    newuri = "/";
                }
                LOGGER.info("Redirect target: " + newuri);

                GetMethod redirect = new GetMethod(newuri);
                try {
                    getClient().executeMethod(redirect);
                } catch (IOException e) {
                    LOGGER.error("Cannot execute get method, for testing redirect link", e);
                } finally {
                    redirect.releaseConnection();
                }
                LOGGER.info("Redirect: " + redirect.getStatusLine().toString());
            }
        }
        return Arrays.asList(logoncookies);
    }

    @Override
    public void setConnectionTimeOut(int timeOut) {
        getClient().getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
    }
}
