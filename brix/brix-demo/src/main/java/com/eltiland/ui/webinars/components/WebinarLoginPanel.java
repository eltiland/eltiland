package com.eltiland.ui.webinars.components;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.textfield.ELTPasswordField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.WebinarsPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Panel for loggin in and registering to the webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarLoginPanel extends BaseEltilandPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarLoginPanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private WebMarkupContainer loginContainer = new WebMarkupContainer("loginContainer");
    private WebMarkupContainer registerContainer = new WebMarkupContainer("registerContainer");

    private ELTTextField loginField = new ELTTextField<>(
            "loginField", new ResourceModel("email"), new Model<String>(), String.class, true);
    private ELTPasswordField passwordField = new ELTPasswordField(
            "passwordField", new ResourceModel("password"), new Model<String>(), true);

    private ELTTextField nameField = new ELTTextField<>(
            "nameField", new ResourceModel("name"), new Model<String>(), String.class);
    private ELTTextField surnameField = new ELTTextField<>(
            "surnameField", new ResourceModel("surname"), new Model<String>(), String.class);
    private ELTTextField patronymicField = new ELTTextField<>(
            "patronymicField", new ResourceModel("patronymic"), new Model<String>(), String.class);

    private EltiAjaxSubmitLink webinarButton = new EltiAjaxSubmitLink("webinarButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            User currentUser = EltilandSession.get().getCurrentUser();
            WebinarUserPayment user = new WebinarUserPayment();
            user.setRole(WebinarUserPayment.Role.MEMBER);
            user.setWebinar(webinarIModel.getObject());
            user.setPrice(webinarIModel.getObject().getPrice());
            user.setRegistrationDate(DateUtils.getCurrentDate());
            user.setUserName((String) nameField.getModelObject());
            user.setPatronymic((String) patronymicField.getModelObject());
            user.setUserSurname((String) surnameField.getModelObject());
            user.setUserProfile(currentUser);
            user.setUserEmail(currentUser.getEmail());
            try {
                try {
                    webinarUserPaymentManager.createUser(user);
                } catch (WebinarException e) {
                    e.printStackTrace();
                }

                boolean isFree = (user.getPrice() == null) || (user.getPrice().equals(BigDecimal.valueOf(0)));

                EltiStaticAlerts.registerOKPopup(getString(isFree ? "signupFreeMessage" : "signupPaidMessage"));
                setResponsePage(WebinarsPage.class);
            } catch (EltilandManagerException e) {
                LOGGER.error("Got exception when creating webinar user", e);
                throw new WicketRuntimeException("Got exception when creating webinar user", e);
            } catch (EmailException e) {
                LOGGER.error("Got exception when sending mail", e);
                throw new WicketRuntimeException("Got exception when sending mail", e);
            }
        }
    };

    private enum MODE {
        LOGIN, REGISTER
    }

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    public WebinarLoginPanel(String id) {
        super(id);

        Form form = new Form("form");
        add(form);

        form.add(loginContainer.setOutputMarkupPlaceholderTag(true));
        form.add(registerContainer.setOutputMarkupPlaceholderTag(true));

        loginContainer.add(loginField);
        loginField.add(new AbstractValidator() {
            @Override
            protected void onValidate(IValidatable validatable) {
                String email = (String) validatable.getValue();

                if (webinarUserPaymentManager.hasAlreadyRegistered(webinarIModel.getObject(), email)) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("errorRegistered");
                        }
                    });
                }
            }
        });

        loginContainer.add(passwordField);
        loginContainer.add(new EltiAjaxSubmitLink("loginButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                EltilandSession session = EltilandSession.get();
                session.bind();
                if (session.signIn((String) loginField.getModelObject(), passwordField.getModelObject())) {
                    User currentUser = EltilandSession.get().getCurrentUser();
                    String name = currentUser.getName();
                    String[] nameParts = name.split(" ");
                    int count = nameParts.length;

                    if (count > 0) {
                        surnameField.setModelObject(nameParts[0]);
                    }
                    if (count > 1) {
                        nameField.setModelObject(nameParts[1]);
                    }
                    if (count > 2) {
                        patronymicField.setModelObject(nameParts[2]);
                    }

                    changeMode(target, MODE.REGISTER);
                } else {
                    ELTAlerts.renderErrorPopup(getString("errorLogin"), target);
                }
            }
        });

        registerContainer.add(nameField);
        registerContainer.add(surnameField);
        registerContainer.add(patronymicField);
        registerContainer.add(webinarButton);

        changeMode(null, MODE.LOGIN);
    }

    public void initWebinarData(IModel<Webinar> webinarIModel) {
        this.webinarIModel = webinarIModel;
    }

    private void changeMode(AjaxRequestTarget target, MODE mode) {
        boolean login = mode.equals(MODE.LOGIN);

        loginContainer.setVisible(login);
        registerContainer.setVisible(!login);

        nameField.setValueRequired(!login);
        surnameField.setValueRequired(!login);
        patronymicField.setValueRequired(!login);

        if (target != null) {
            target.add(loginContainer);
            target.add(registerContainer);
        }
    }
}
