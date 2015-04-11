package com.eltiland.ui.common.components.user_selector;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for registration new user for webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class RegisterUserPanel extends ELTDialogPanel implements IDialogNewCallback<User> {

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private UserManager userManager;

    private IDialogActionProcessor<User> callback;

    ELTTextField nameField = new ELTTextField(
            "nameField", new ResourceModel("fioField"), new Model<String>(), String.class, true) {
    };
    ELTTextEmailField emailField = new ELTTextEmailField(
            "emailField", new ResourceModel("emailField"), new Model<String>(), true);

    public RegisterUserPanel(String id) {
        super(id);

        form.add(nameField);
        form.add(emailField);
        emailField.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                if (userManager.getUserByEmail(validatable.getValue()) != null) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("emailExistsError");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected String getHeader() {
        return getString("registerHeaderLabel");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Register));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Register)) {
            User user = new User();
            user.setName((String) nameField.getModelObject());
            user.setEmail(emailField.getModelObject());
            user.setPassword(RandomStringUtils.randomAlphanumeric(10));
            user.setAvatar(fileManager.getStandardIconFile(UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT));
            callback.process(new GenericDBModel<>(User.class, user), target);
        }
    }

    @Override
    protected boolean showCaptcha() {
        return true;
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<User> callback) {
        this.callback = callback;
    }
}
