package com.eltiland.ui.login;

import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.model.user.Confirmation;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.login.panels.ActivationPanel;
import com.eltiland.ui.login.panels.LoginPanel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Login page.
 */
public class LoginPage extends TwoColumnPage {

    public static final String MOUNT_PATH = UrlUtils.LOGIN_PAGE_MOUNT_PATH;

    public static final String FAILED_LOGIN_PARAM = "fl";

    @SpringBean
    private ConfirmationManager confirmationManager;

    /**
     * Construct.
     * Login page, contains login panel, which can work in 3 modes:
     * <p/>
     * Simple login form
     * Confirmation form, for fulfill parent information in first time visit
     * Error message, which notify user that he pass wrong confirmation secret parameter,
     * or confirmation has been expire
     *
     * @param parameters page parameters
     */
    public LoginPage(PageParameters parameters) {
        LoginPanel loginPanel2 = new LoginPanel("loginPanel2");
        ActivationPanel activationPanel = new ActivationPanel("activationPanel");

        String secretCode = parameters.get(UrlUtils.SECRET_CODE_PARAMETER_NAME).toString();
        String failedLogin = parameters.get(FAILED_LOGIN_PARAM).toString();
        if (failedLogin != null) {
            loginPanel2.initErrorMode(failedLogin);
        }

        boolean secret = (secretCode != null);

        if (secret) {
            Confirmation confirmation = confirmationManager.getConfirmationByCode(secretCode);
            if (confirmation == null) {
                activationPanel.initErrorMode(getString("incorrectLinkError"));
            } else if (confirmation.getUser() == null) {
                activationPanel.initErrorMode(getString("noActualError"));
            } else if (confirmation.getEndingDate().before(DateUtils.getCurrentDate())) {
                activationPanel.initErrorMode(getString("dateEndedError"));
            } else {
                activationPanel.initConfirmationMode(confirmation);
            }
        }

        activationPanel.setVisible(secret);
        loginPanel2.setVisible(!secret);

        add(loginPanel2);
        add(activationPanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LOGIN);
    }
}
