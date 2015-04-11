package com.eltiland.bl.impl.authentication;

import com.eltiland.bl.AuthenticationProvider;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;

/**
 * Basic abstract class for Authentication Providers.
 * Base on Jakarta http client, need to generalize
 * authentication on the external resources.
 *
 * @author knorr
 * @version 1.0
 * @since 7/20/12
 */
public abstract class AbstractJakartaAuthenticationProvider implements AuthenticationProvider {

    protected final int LOGON_PORT = 80;

    protected final String LOGON_PROTOCOL = "http";

    protected final String MAIN_DOMAIN = "logicify.lan";

    protected final int DEFAULT_CONNECTION_TIMEOUT = 3000;

    private HttpClient client;


    public AbstractJakartaAuthenticationProvider() {
        client = new HttpClient();
        client.getHostConfiguration().setHost(getLogonSiteDomain(), LOGON_PORT, LOGON_PROTOCOL);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        client.getHttpConnectionManager().getParams().setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
    }

    protected HttpClient getClient() {
        return client;
    }

    protected abstract String getLogonSiteDomain();

}