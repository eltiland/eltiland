package com.eltiland.ui.login;

import com.eltiland.bl.user.ResetPassManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.ResetCode;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.login.panels.ChangePasswordPanel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;

/**
 * Reset password page.
 *
 * @author Aleksey Plotnikov
 */
public class ResetPasswordPage extends TwoColumnPage {

    public static final String MOUNT_PATH = UrlUtils.RESET_PAGE_MOUNT_PATH;

    @SpringBean
    private ResetPassManager resetPassManager;

    @SpringBean
    private UserManager userManager;

    private String resetCode;

    private IModel<ResetCode> resetCodeModel = new LoadableDetachableModel<ResetCode>() {
        @Override
        protected ResetCode load() {
            return resetPassManager.getResetInfoByCode(resetCode);
        }
    };

    /**
     * Page constructor.
     *
     * @param parameters page parameters
     */
    public ResetPasswordPage(PageParameters parameters) {
        super(parameters);
        resetCode = parameters.get(UrlUtils.RESET_CODE_PARAMETER_NAME).toString();

        if (resetCode != null) {
            resetCodeModel.detach();
            if (resetCodeModel.getObject() == null ||
                    resetCodeModel.getObject().getEndingDate().before(DateUtils.getCurrentDate())) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        ChangePasswordPanel changePasswordPanel = new ChangePasswordPanel("panel");
        add(changePasswordPanel);
        changePasswordPanel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
            @Override
            public void process(IModel<String> stringIModel, AjaxRequestTarget target) {
                User user = resetCodeModel.getObject().getUser();
                user.setPassword(HashesUtils.getSHA1inHex(stringIModel.getObject()));
                try {
                    userManager.updateUser(user);
                    resetPassManager.removeResetCode(resetCodeModel.getObject());
                    EltiStaticAlerts.registerOKPopup(getString("passwordChanged"));
                    setResponsePage(Application.get().getHomePage());
                } catch (UserException e) {
                    EltiStaticAlerts.registerErrorPopup(e.getMessage());
                }
            }
        });
    }
}
