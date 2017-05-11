package com.eltiland.ui.login.panels;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.SubscriberManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.general.ConfirmationPage;
import com.eltiland.ui.course.CourseNewPage;
import com.eltiland.utils.StringUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;

/**
 * Registration panel.
 *
 * @author Aleksey Plotnikov.
 */
public class RegistrationPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RegistrationPanel.class);

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private SubscriberManager subscriberManager;

    private TextField<String> loginField = new TextField<>("login", new Model<String>());
    private TextField<String> nameField = new TextField<>("name", new Model<String>());
    private PasswordTextField passField = new PasswordTextField("pass", new Model<String>());
    private PasswordTextField rePassField = new PasswordTextField("rePass", new Model<String>());

    private final CheckBox subscribeCheckBox = new CheckBox("subscribeCheckBox", new Model<Boolean>());
    private final CheckBox confirmationCheckBox = new CheckBox("confirmCheckBox", new Model<Boolean>());

    private Dialog<ResetPassPanel> resetPassPanelDialog = new Dialog<ResetPassPanel>("resetPassDialog", 325) {
        @Override
        public ResetPassPanel createDialogPanel(String id) {
            return new ResetPassPanel(id);
        }

        @Override
        public void registerCallback(ResetPassPanel panel) {
            super.registerCallback(panel);
            panel.setSelectCallback(new IDialogSelectCallback.IDialogActionProcessor<User>() {
                @Override
                public void process(IModel<User> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        emailMessageManager.sendEmailToUserResetPasswordRequest(model.getObject());
                    } catch (AddressException | EmailException e) {
                        LOGGER.error("Got exception when sending request.", e);
                        throw new WicketRuntimeException(e);
                    }
                }
            });
        }
    };

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public RegistrationPanel(String id, final Long courseId) {
        super(id);

        Form form = new Form("form");
        add(form);

        form.add(loginField.setRequired(true));
        loginField.add(StringUtils.emailValidator);
        form.add(nameField.setRequired(true));
        form.add(passField.setRequired(true));
        form.add(rePassField.setRequired(true));
        form.add(subscribeCheckBox);
        form.add(confirmationCheckBox);

        form.add(new BookmarkablePageLink<>("confirmLink", ConfirmationPage.class));

        subscribeCheckBox.setModelObject(true);
        confirmationCheckBox.setModelObject(false);

        WebMarkupContainer resetLink = new WebMarkupContainer("forget");
        add(resetLink);
        add(resetPassPanelDialog);

        resetLink.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resetPassPanelDialog.show(target);
            }
        });

        form.add(new EltiAjaxSubmitLink("registerButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                if (!confirmationCheckBox.getModelObject()) {
                    ELTAlerts.renderErrorPopup(getString("error.no.confirmation"), target);
                    return;
                }

                if (!(passField.getConvertedInput().equals(rePassField.getConvertedInput()))) {
                    ELTAlerts.renderErrorPopup(getString("validatePasswordConfirm"), target);
                    return;
                }
                User user = userManager.getUserByEmail(loginField.getModelObject());
                if (user != null) {
                    ELTAlerts.renderErrorPopup(getString("validateEmailUnique"), target);
                    return;
                }

                User newUser = new User();
                newUser.setName(nameField.getModelObject());
                newUser.setEmail(loginField.getModelObject());
                newUser.setPassword(HashesUtils.getSHA1inHex(passField.getModelObject()));
                newUser.setAvatar(fileManager.getStandardIconFile(UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT));
                newUser.setActive(true);

                try {
                    userManager.createUser(newUser);

                    EltilandSession session = EltilandSession.get();
                    session.bind();
                    session.signIn(newUser.getEmail(), passField.getModelObject());
                    emailMessageManager.sendEmailToUserRegistered(newUser);
                    EltiStaticAlerts.registerOKPopupModal(getString("newUserRegistrationMessage"));

                    if (courseId != null) {
                        // Redirect back to details page of the course
                        throw new RestartResponseException(CourseNewPage.class,
                                new PageParameters().add(CourseNewPage.PARAM_ID, courseId));
                    } else {
                        setResponsePage(Application.get().getHomePage());
                    }
                } catch (EltilandManagerException | UserException e) {
                    LOGGER.error("Cannot create user", e);
                    throw new WicketRuntimeException("Got exception when creating user", e);
                } catch (EmailException e) {
                    LOGGER.error("Cannot send email", e);
                    throw new WicketRuntimeException("Got exception when sending email", e);
                }

                if (subscribeCheckBox.getModelObject()) {
                    Subscriber subscriber = new Subscriber();
                    subscriber.setEmail(loginField.getModelObject());
                    try {
                        subscriberManager.createSubscriber(subscriber);
                    } catch (SubscriberException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }

            }

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                if ((loginField.getConvertedInput() == null) || (nameField.getConvertedInput() == null) ||
                        (passField.getConvertedInput() == null) || (rePassField.getConvertedInput() == null)) {
                    ELTAlerts.renderErrorPopup(getString("notFilledMessage"), target);
                } else {
                    ELTAlerts.renderErrorPopup(getString("emailValidator"), target);
                }
            }
        });
    }
}
