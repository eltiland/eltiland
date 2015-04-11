package com.eltiland.bl;

import org.apache.commons.httpclient.Cookie;

import java.util.Collection;

/**
 * @author knorr
 * @version 1.0
 * @since 7/31/12
 */
public interface AuthenticationProvider {

    Collection<Cookie> authenticate(String login, String password);

    void setConnectionTimeOut(int timeOut);

}
