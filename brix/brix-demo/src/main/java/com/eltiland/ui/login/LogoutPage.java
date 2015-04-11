package com.eltiland.ui.login;

import com.eltiland.session.EltilandSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;

/**
 * Logout page.
 */
public class LogoutPage extends WebPage {

    public static final String MOUNT_PATH = "/logout";

    public LogoutPage() {
        EltilandSession.get().signOut();
        throw new RestartResponseException(getApplication().getHomePage());
    }
}
