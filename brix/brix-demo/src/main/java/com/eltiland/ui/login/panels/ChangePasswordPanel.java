package com.eltiland.ui.login.panels;

import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTPasswordField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidationError;

import java.util.Arrays;
import java.util.List;

/**
 * Change password panel.
 *
 * @author Aleksey Plotnikov
 */
public class ChangePasswordPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<String> {

    private IDialogActionProcessor<String> newCallback;

    private ELTPasswordField passField = new ELTPasswordField(
            "passField", new ResourceModel("newPasswordLabel"), new Model<String>(), true);
    private ELTPasswordField passConfirmField = new ELTPasswordField(
            "passConfirmField", new ResourceModel("confirmPasswordLabel"), new Model<String>(), true);


    public ChangePasswordPanel(String id) {
        super(id);

        form.add(passField);
        form.add(passConfirmField);
        form.add(new ChangePassFormValidator());
    }

    @Override
    protected String getHeader() {
        return getString("changePasswordTitle");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Apply);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Apply)) {
            ELTAlerts.renderOKPopup(getString("passwordChangedMessage"), target);
            newCallback.process(new Model<>(passField.getModelObject()), target);
        }
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<String> callback) {
        this.newCallback = callback;
    }

    private class ChangePassFormValidator extends AbstractFormValidator {

        private IValidationError passwError = new IValidationError() {
            @Override
            public String getErrorMessage(IErrorMessageSource messageSource) {
                return getString("passwordEqualsError");
            }
        };

        @Override
        public FormComponent[] getDependentFormComponents() {
            return new FormComponent[]{passField, passConfirmField};
        }

        @Override
        public void validate(Form components) {
            if (!(passField.getConvertedInput().equals(passConfirmField.getConvertedInput()))) {
                passField.error(passwError);
                passConfirmField.error(passwError);
            }
        }
    }
}
