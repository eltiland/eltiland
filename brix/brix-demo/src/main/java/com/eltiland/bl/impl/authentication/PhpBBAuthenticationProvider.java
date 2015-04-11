package com.eltiland.bl.impl.authentication;

import org.apache.commons.httpclient.Cookie;
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
 * Authentication provider to login into eltiland forum. Based on PhpBB3 forum engine.
 * <p/>
 * Description of the PhpBB3 authentication process :
 * <p>
 * Entry point of the PhpBB3 engine is the script named 'ucp'.
 * Parameter - 'mode' is used  to determine what page must be showed to the user.
 * So first step is provide mode = 'login' to the 'ucp' script to start required script.
 * Usually PhpBB3 send to user 3 cookies: 'u', 'k', 'sid', seems like 'u' is the user id
 * 'sid' is user session id, and finally 'k' - is UFO :)
 * </p>
 * <p/>
 * <p>
 * PhpBB3 authentication script require sid as a request parameter
 * so we need to do  'GET' request of the forum home page, to retrieve sid cookie,
 * and then pass this cookie value as parameter to the 'POST' request.
 * Also auth script require 'username' and 'password' params! And one more additional
 * parameter that called 'login' and we need to pass string - 'Login' there - WTF ;)
 * </p>
 *
 * @author knorr
 * @version 1.0
 * @since 7/31/12
 */
public class PhpBBAuthenticationProvider extends AbstractJakartaAuthenticationProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(PhpBBAuthenticationProvider.class);

    private final String LOGON_SITE = "forum.logicify.lan";

    private final String PATH_DOMAIN = "/ucp.php";

    private final String SID_COOKIE_NAME = "phpbb3_2633v_sid";

    @Override
    protected String getLogonSiteDomain() {
        return LOGON_SITE;
    }

    @Override
    public Collection<Cookie> authenticate(String login, String password) {
        /*
        First we need to 'GET' PhpBB3 home page, to retrieve required cookies.
        */
        GetMethod authget = new GetMethod(PATH_DOMAIN);
        try {
            getClient().executeMethod(authget);
        } catch (IOException e) {
            LOGGER.error(String.format("Get en exception, while trying to 'GET' %s%s", LOGON_SITE, PATH_DOMAIN), e);
        } finally {
            authget.releaseConnection();
        }

        /*
        Authentication in PhpBB3 require sid parameter,
        */
        String sid = null;
        for (Cookie cookie : getClient().getState().getCookies()) {
            if (SID_COOKIE_NAME.equals(cookie.getName().toLowerCase())) {
                sid = cookie.getValue();
            }
        }

        /*
        Construct needed 'POST' request.
        */
        PostMethod authpost = new PostMethod(PATH_DOMAIN);
        NameValuePair userName = new NameValuePair("username", login);
        NameValuePair userPassword = new NameValuePair("password", password);
        NameValuePair loginMode = new NameValuePair("login", "Login");
        NameValuePair userSid = new NameValuePair("sid", sid);
        NameValuePair mode = new NameValuePair("mode", "login");
        authpost.setRequestBody(new NameValuePair[]{userName, userPassword, loginMode, userSid, mode});
        try {
            getClient().executeMethod(authpost);
        } catch (IOException e) {
            LOGGER.error(String.format("Get en exception, while trying to 'POST' on %s%s", LOGON_SITE, PATH_DOMAIN), e);
        } finally {
            authpost.releaseConnection();
        }
        LOGGER.info("Login form post: " + authpost.getStatusLine().toString());

        /*
        Get 'POST' cookies from response headers.
        */
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(MAIN_DOMAIN, LOGON_PORT, "/", false, getClient().getState().getCookies());
        if (logoncookies.length == 0) {
            LOGGER.error("None cookies has been received");
        } else {
            for (Cookie logoncooky : logoncookies) {
                LOGGER.info(logoncooky.toString());
            }
        }
        return Arrays.asList(logoncookies);
    }

    @Override
    public void setConnectionTimeOut(int timeOut) {
        getClient().getHttpConnectionManager().getParams().setConnectionTimeout(timeOut);
    }


}
