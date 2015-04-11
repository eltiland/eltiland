package com.eltiland.ui.webinars.components;

import com.eltiland.bl.*;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.captcha.CaptchaPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTPasswordField;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Panel for new user registration on webinar.
 *
 * @author Aleksey Plotnikov
 */
public abstract class WebinarNewUserPanel extends BaseEltilandPanel<WebinarUserPayment>
        implements IDialogNewCallback<WebinarUserPayment> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarNewUserPanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private SubscriberManager subscriberManager;


    private IDialogNewCallback.IDialogActionProcessor<WebinarUserPayment> newCallback;

    private ELTTextField nameField = new ELTTextField<>(
            "nameField", new ResourceModel("nameLabel"), new Model<String>(), String.class, true);
    private ELTTextEmailField emailField = new ELTTextEmailField(
            "emailField", new ResourceModel("emailLabel"), new Model<String>(), true);
    private ELTPasswordField passwordField = new ELTPasswordField(
            "passwordField", new ResourceModel("passwordLabel"), new Model<String>());
    private ELTPasswordField passwordSecondField = new ELTPasswordField(
            "passwordSecondField", new ResourceModel("passwordSecondLabel"), new Model<String>());
    private final CheckBox subscribeCheckBox = new CheckBox("subscribeCheckBox", new Model<Boolean>());

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private String nameValue, surnameValue, patronymicValue;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public WebinarNewUserPanel(String id) {
        super(id);

        final boolean loggedUser = currentUserModel.getObject() != null;

        add(new EltiAjaxLink("enterButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onLogin(target, webinarIModel);
            }

            @Override
            public boolean isVisible() {
                currentUserModel.detach();
                return !loggedUser;
            }
        });

        Form form = new Form("form");
        add(form);
        form.add(nameField);
        form.add(emailField);
        form.add(passwordField);
        form.add(passwordSecondField);
        form.add(subscribeCheckBox);
        subscribeCheckBox.setModelObject(true);
        passwordField.setVisible(!loggedUser);
        passwordField.setValueRequired(!loggedUser);
        passwordSecondField.setValueRequired(!loggedUser);

        if (loggedUser) {
            nameField.setModelObject(currentUserModel.getObject().getName());
            emailField.setModelObject(currentUserModel.getObject().getEmail());
        }

        form.add(new CaptchaPanel("captchaPanel"));
        form.add(new FormRequired("required"));


        emailField.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                String email = validatable.getValue();

                if (webinarUserPaymentManager.hasAlreadyRegistered(webinarIModel.getObject(), email)) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("errorEmailRegistered");
                        }
                    });
                } else {
                    if (!loggedUser && userManager.getUserByEmail(email) != null) {
                        validatable.error(new IValidationError() {
                            @Override
                            public String getErrorMessage(IErrorMessageSource messageSource) {
                                return getString("errorRegisteredUser");
                            }
                        });
                    }
                }
            }
        });

        nameField.add(new AbstractValidator<String>() {
            @Override
            protected void onValidate(IValidatable<String> validatable) {
                String name = validatable.getValue();
                String[] nameParts = name.split(" ");
                if (nameParts.length != 3) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("errorFioFormat");
                        }
                    });
                } else {
                    surnameValue = nameParts[0];
                    nameValue = nameParts[1];
                    patronymicValue = nameParts[2];
                }
            }
        });


        form.add(new EltiAjaxSubmitLink("approveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                if (!loggedUser) { // create new User
                    User user = new User();

                    user.setName((String) nameField.getModelObject());
                    user.setEmail(emailField.getModelObject());
                    user.setPassword(HashesUtils.getSHA1inHex(passwordField.getModelObject()));
                    user.setAvatar(fileManager.getStandardIconFile(UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT));

                    try {
                        userManager.createUser(user);

                        EltilandSession session = EltilandSession.get();
                        session.bind();
                        session.signIn(user.getEmail(), passwordField.getModelObject());
                        emailMessageManager.sendEmailToUserRegistered(user);
                    } catch (EltilandManagerException | UserException e) {
                        LOGGER.error("Got exception when creating user", e);
                        throw new WicketRuntimeException("Got exception when creating user", e);
                    } catch (EmailException e) {
                        LOGGER.error("\"Got exception when sending email", e);
                        throw new WicketRuntimeException("Got exception when sending email", e);
                    }

                    currentUserModel.detach();

                    if( subscribeCheckBox.getModelObject() ) {
                        Subscriber subscriber = new Subscriber();
                        subscriber.setEmail(emailField.getModelObject());
                        try {
                            subscriberManager.createSubscriber(subscriber);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }
                    }

                }

                // add new webinar user
                WebinarUserPayment user = new WebinarUserPayment();
                user.setRole(WebinarUserPayment.Role.MEMBER);
                user.setWebinar(webinarIModel.getObject());
                user.setPrice(webinarIModel.getObject().getPrice());
                user.setRegistrationDate(DateUtils.getCurrentDate());
                user.setUserName(nameValue);
                user.setPatronymic(patronymicValue);
                user.setUserSurname(surnameValue);
                user.setUserProfile(currentUserModel.getObject());
                user.setUserEmail(emailField.getModelObject());

                if (newCallback != null) {
                    newCallback.process(new GenericDBModel<>(WebinarUserPayment.class, user), target);
                }
            }
        });

        form.add(new WebinarFormValidator());
        form.setMultiPart(true);
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<WebinarUserPayment> callback) {
        this.newCallback = callback;
    }

    public void initWebinarData(Webinar currentWebinar) {
        webinarIModel.setObject(currentWebinar);
    }

    public abstract void onLogin(AjaxRequestTarget target, IModel<Webinar> webinarIModel);

    private class WebinarFormValidator extends AbstractFormValidator {

        private IValidationError passwError = new IValidationError() {
            @Override
            public String getErrorMessage(IErrorMessageSource messageSource) {
                return getString("passwordEqualsError");
            }
        };

        @Override
        public FormComponent<?>[] getDependentFormComponents() {
            return new FormComponent[]{passwordField, passwordSecondField};
        }

        @Override
        public void validate(Form<?> form) {
            if (!(passwordField.getConvertedInput().equals(passwordSecondField.getConvertedInput()))) {
                passwordField.error(passwError);
                passwordSecondField.error(passwError);
            }
        }
    }
}
