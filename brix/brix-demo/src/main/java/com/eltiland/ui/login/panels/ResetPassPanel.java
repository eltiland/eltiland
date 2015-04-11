package com.eltiland.ui.login.panels;

import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

import java.util.Arrays;
import java.util.List;

/**
 * Reset password panel.
 *
 * @author Aleksey Plotnikov
 */
public class ResetPassPanel extends ELTDialogPanel implements IDialogSelectCallback<User>{

    @SpringBean
    private UserManager userManager;

    private IDialogActionProcessor<User> selectCallback;

    private ELTTextEmailField emailField = new ELTTextEmailField(
            "emailField", new ResourceModel("emailLabel"), new Model<String>(), true);

    public ResetPassPanel(String id) {
        super(id);
        form.add(emailField);
        emailField.add(new AbstractValidator() {
            @Override
            protected void onValidate(IValidatable validatable) {
                User user = userManager.getUserByEmail((String) validatable.getValue());
                if (user == null) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("notFoundError");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected String getHeader() {
        return getString("panelLabel");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Send);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Send)) {
            User user = userManager.getUserByEmail(emailField.getModelObject());
            ELTAlerts.renderOKPopup(getString("sendedMessage"), target);
            selectCallback.process(new GenericDBModel<>(User.class, user), target);
        }
    }

    @Override
    protected boolean showCaptcha() {
        return true;
    }

    @Override
    public void setSelectCallback(IDialogActionProcessor<User> callback) {
        this.selectCallback = callback;
    }
}
