package com.eltiland.ui.login.panels;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.HomePage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.course.CourseNewPage;
import com.eltiland.ui.login.LoginPage;
import com.eltiland.ui.login.RegisterPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;

/**
 * Login panel.
 *
 * @author Aleksey Plotnikov.
 */
public class LoginPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LoginPanel.class);

    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserManager userManager;

    private TextField<String> loginField = new TextField<>("login", new Model<String>());
    private PasswordTextField passField = new PasswordTextField("pass", new Model<String>());
    private WebMarkupContainer error = new WebMarkupContainer("error");

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
     * Panel constructor.
     *
     * @param id panel's id
     */
    public LoginPanel(String id, final Long courseId) {
        super(id);

        add(new WebMarkupContainer("course_login") {
            @Override
            public boolean isVisible() {
                return courseId != null;
            }
        });

        Form form = new Form("form");
        add(form);

        form.add(loginField);
        form.add(passField);

        form.add(new EltiAjaxSubmitLink("loginButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                // check for deactivated user
                User user = userManager.getUserByEmail(loginField.getModelObject());
                if (user != null && !(user.isActive())) {
                    PageParameters pp = new PageParameters();
                    pp.add(LoginPage.FAILED_LOGIN_PARAM, loginField.getModelObject());
                    throw new RestartResponseException(LoginPage.class, pp);
                }

                EltilandSession session = EltilandSession.get();
                session.bind();
                if (session.signIn(loginField.getModelObject(), passField.getModelObject())) {
                    if (!continueToOriginalDestination()) {
                        if (courseId != null) {
                            // Redirect back to details page of the course
                            throw new RestartResponseException(CourseNewPage.class,
                                    new PageParameters().add(CourseNewPage.PARAM_ID, courseId));
                        } else {
                            throw new RestartResponseException(HomePage.class);
                        }
                    }
                } else {
                    PageParameters pp = new PageParameters();
                    pp.add(LoginPage.FAILED_LOGIN_PARAM, loginField.getModelObject());
                    throw new RestartResponseException(LoginPage.class, pp);
                }
            }
        });

        WebMarkupContainer resetLink = new WebMarkupContainer("forget");
        WebMarkupContainer registerLink = new WebMarkupContainer("register");

        add(resetLink);
        add(registerLink);

        resetLink.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                resetPassPanelDialog.show(target);
            }
        });

        registerLink.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (courseId == null) {
                    throw new RestartResponseException(RegisterPage.class);
                } else {
                    throw new RestartResponseException(RegisterPage.class,
                            new PageParameters().add(RegisterPage.PARAM_COURSE, courseId));
                }
            }
        });

        add(resetPassPanelDialog);
        add(error.setVisible(false));
    }

    public void initErrorMode(String email) {
        loginField.setModelObject(email);
        error.setVisible(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LOGIN);
    }
}
