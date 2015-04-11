package com.eltiland.ui.worktop.simple.panel;

import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.bl.validators.UserValidator;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.login.panels.ChangePasswordPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Profile panel - edit information.
 *
 * @author Aleksey PLotnikov
 */
public abstract class ProfileEditPanel extends BaseEltilandPanel<User> {

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private UserValidator userValidator;

    private Dialog<ChangePasswordPanel> changePasswordPanelDialog = new Dialog<ChangePasswordPanel>(
            "changePassDialog", 335) {
        @Override
        public ChangePasswordPanel createDialogPanel(String id) {
            return new ChangePasswordPanel(id);
        }

        @Override
        public void registerCallback(ChangePasswordPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    close(target);
                    User user = ProfileEditPanel.this.getModelObject();
                    user.setPassword(HashesUtils.getSHA1inHex(model.getObject()));
                    try {
                        userManager.updateUser(user);
                    } catch (UserException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    ELTAlerts.renderOKPopup(getString("changePassMessage"), target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id        markup id.
     * @param userModel user model.
     */
    public ProfileEditPanel(String id, final IModel<User> userModel) {
        super(id, userModel);

        final User user = userModel.getObject();

        setOutputMarkupId(true);

        Form form = new Form("form");

        final ELTTextField<String> nameField = new ELTTextField<>("nameField",
                new ResourceModel("nameHeader"), new Model<String>(), String.class, true);
        final ELTTextEmailField emailField = new ELTTextEmailField("emailField", new ResourceModel("emailHeader"),
                new Model<>(user.getEmail()), true);
        final ELTTextField<String> addressField = new ELTTextField<>("addressField", new ResourceModel("addressHeader"),
                new Model<>(user.getAddress()), String.class);
        final ELTTextField<String> phoneField = new ELTTextField<>("phoneField", new ResourceModel("phoneHeader"),
                new Model<>(user.getPhone()), String.class);
        final ELTTextField<String> skypeField = new ELTTextField<>("skypeField", new ResourceModel("skypeHeader"),
                new Model<>(user.getSkype()), String.class);
        final ELTTextField<String> companyField = new ELTTextField<>("companyField", new ResourceModel("companyHeader"),
                new Model<>(user.getOrganization()), String.class);
        final ELTTextField<String> jobField = new ELTTextField<>("jobField", new ResourceModel("jobHeader"),
                new Model<>(user.getAppointment()), String.class);
        final ELTTextField<Integer> expField = new ELTTextField<>("expField", new ResourceModel("expHeader"),
                new Model<>(user.getExperience()), Integer.class);

        final ELTTextArea aboutField = new ELTTextArea("aboutField", new ResourceModel("aboutHeader"),
                new Model<>(user.getInformation())) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }

            @Override
            protected int getInitialHeight() {
                return 210;
            }
        };

        final ELTTextArea achieveField = new ELTTextArea("achieveField", new ResourceModel("achieveHeader"),
                new Model<>(user.getAchievements())) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }

            @Override
            protected int getInitialHeight() {
                return 210;
            }
        };

        nameField.setModelObject(user.getName());

        add(form);

        form.add(nameField);
        form.add(emailField);
        form.add(addressField);
        form.add(phoneField);
        form.add(skypeField);
        form.add(aboutField);
        form.add(companyField);
        form.add(jobField);
        form.add(expField);
        form.add(achieveField);

        emailField.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                User user = ProfileEditPanel.this.getModelObject();
                Long id = (user != null) ? user.getId() : null;

                if (!userValidator.validateUniqueEmail(validatable.getValue(), id)) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("validateEmailUnique");
                        }
                    });
                }
            }
        });

        phoneField.add(UIConstants.phoneNumberValidator);
        skypeField.add(UIConstants.skypeValidator);

        skypeField.addMaxLengthValidator(32);
        addressField.addMaxLengthValidator(1024);
        aboutField.addMaxLengthValidator(2048);
        achieveField.addMaxLengthValidator(2048);

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                showFeedbackLabels(target, form);
                User user = ProfileEditPanel.this.getModelObject();
                user.setName(nameField.getModelObject());
                user.setEmail(emailField.getModelObject());
                user.setAddress(addressField.getModelObject());
                user.setPhone(phoneField.getModelObject());
                user.setSkype(skypeField.getModelObject());
                user.setInformation(aboutField.getModelObject());
                user.setAchievements(achieveField.getModelObject());
                user.setOrganization(companyField.getModelObject());
                user.setAppointment(jobField.getModelObject());
                user.setExperience(expField.getModelObject());
                saveAvatar(user, target);

                try {
                    userManager.updateUser(user);
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                } catch (UserException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        });

        form.add(new EltiAjaxLink("changePassButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                changePasswordPanelDialog.show(target);
            }
        });

        add(changePasswordPanelDialog);
    }

    protected abstract void saveAvatar(User user, AjaxRequestTarget target);
}
