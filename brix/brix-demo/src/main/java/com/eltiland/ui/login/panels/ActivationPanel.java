package com.eltiland.ui.login.panels;

import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.login.LoginPage;
import com.eltiland.ui.worktop.BaseWorktopPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidationError;

/**
 * Login panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ActivationPanel extends BaseEltilandPanel {

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private ConfirmationManager confirmationManager;

    private TextField<String> loginField = new TextField<>("login", new Model<String>());
    private PasswordTextField passField = new PasswordTextField("pass", new Model<String>());
    private PasswordTextField rePassField = new PasswordTextField("rePass", new Model<String>());
    private WebMarkupContainer error = new WebMarkupContainer("error");
    private WebMarkupContainer main = new WebMarkupContainer("main");
    private Label errorMessage = new Label("errorMessage", new Model<String>());

    private IModel<Confirmation> confirmationIModel = new GenericDBModel<>(Confirmation.class);

    /**
     * Panel constructor.
     *
     * @param id panel's id
     */
    public ActivationPanel(String id) {
        super(id);
        add(main.setOutputMarkupPlaceholderTag(true));

        Form form = new Form("form");
        main.add(form);

        form.add(loginField);
        form.add(passField);
        form.add(rePassField);

        form.add(new EltiAjaxSubmitLink("loginButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                if (confirmationIModel.getObject() != null) {
                    User user = confirmationIModel.getObject().getUser();
                    user.setPassword(HashesUtils.getSHA1inHex(passField.getModelObject()));
                    user.setConfirmationDate(DateUtils.getCurrentDate());
                    try {
                        userManager.updateUser(user);
                        confirmationManager.removeConfirmation(confirmationIModel.getObject());

                        EltiStaticAlerts.registerOKPopupModal(getString("successMessage"));

                        EltilandSession session = EltilandSession.get();
                        session.bind();
                        if (session.signIn(user.getEmail(), passField.getModelObject())) {
                            setResponsePage(BaseWorktopPage.class);
                        } else {
                            PageParameters pp = new PageParameters();
                            pp.add(LoginPage.FAILED_LOGIN_PARAM, user.getEmail());
                            throw new RestartResponseException(LoginPage.class, pp);
                        }
                    } catch (UserException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                ELTAlerts.renderErrorPopup(getString("passwordEqualsError"), target);
            }
        });
        form.add(new ConfirmFormValidator());

        error.add(errorMessage);
        add(error.setVisible(false).setOutputMarkupPlaceholderTag(true));
    }

    public void initErrorMode(String errorText) {
        loginField.setModelObject(errorText);
        error.setVisible(true);
        main.setVisible(false);
        errorMessage.setDefaultModelObject(errorText);
    }

    public void initConfirmationMode(Confirmation confirmation) {
        confirmationIModel.setObject(confirmation);
        loginField.setModelObject(confirmation.getUser().getEmail());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LOGIN);
    }

    private class ConfirmFormValidator extends AbstractFormValidator {

        private IValidationError passwError = new IValidationError() {
            @Override
            public String getErrorMessage(IErrorMessageSource messageSource) {
                return getString("passwordEqualsError");
            }
        };

        @Override
        public FormComponent[] getDependentFormComponents() {
            return new FormComponent[]{passField, rePassField};
        }

        @Override
        public void validate(Form components) {
            if (!(passField.getConvertedInput().equals(rePassField.getConvertedInput()))) {
                passField.error(passwError);
                rePassField.error(passwError);
            }
        }
    }
}
