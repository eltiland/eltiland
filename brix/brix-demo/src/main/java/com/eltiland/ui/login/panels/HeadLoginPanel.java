package com.eltiland.ui.login.panels;

import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.faq.components.AddQuestionPanel;
import com.eltiland.ui.login.LoginPage;
import com.eltiland.ui.login.LogoutPage;
import com.eltiland.ui.login.RegisterPage;
import com.eltiland.ui.webinars.components.multiply.WebinarAddUsersPanel;
import com.eltiland.ui.worktop.BaseWorktopPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.brixcms.demo.web.admin.AdminPage;

/**
 * Panel for header of the portal with Login, Administration, Profile, Logout links.
 *
 * @author Aleksey Plotnikov.
 */
public class HeadLoginPanel extends BaseEltilandPanel {

    private final String CSS = "static/css/panels/head_login.css";

    private Dialog<AddQuestionPanel> addQuestionPanelDialog =
            new Dialog<AddQuestionPanel>("addQuestionDialog", 325) {
                @Override
                public AddQuestionPanel createDialogPanel(String id) {
                    return new AddQuestionPanel(id);
                }

                @Override
                public void registerCallback(AddQuestionPanel panel) {
                    super.registerCallback(panel);
                    panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("questionSuccessfullySended"), target);
                        }
                    });
                }
            };

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    /*
   * *
    * Panel constructor.
    *
    * @param id markup id.
    */
    public HeadLoginPanel(String id) {
        super(id);

        add(new Label("welcome", String.format(getString("welcome"),
                (currentUserModel.getObject() == null) ? getString("guest") : currentUserModel.getObject().getName())));
        add(new ActionPanel("registerAction", ButtonAction.REGISTER) {
            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() == null;
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(RegisterPage.class);
            }
        });
        add(new ActionPanel("loginButton", ButtonAction.LOGIN) {
            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() == null;
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(LoginPage.class);
            }
        });
        add(new ActionPanel("logoutButton", ButtonAction.LOGOUT) {
            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null;
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(LogoutPage.class);
            }
        });
        add(new ActionPanel("profileAction", ButtonAction.PROFILE) {
            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null;
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(BaseWorktopPage.class,
                        new PageParameters().add(BaseWorktopPage.PARAM_ID, currentUserModel.getObject().getId()));
            }
        });
        add(new ActionPanel("adminAction", ButtonAction.ADMIN) {
            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null && currentUserModel.getObject().isSuperUser();
            }

            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(AdminPage.class);
            }
        });

        add(new ActionPanel("askQuestion", ButtonAction.ASK_QUESTION) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                addQuestionPanelDialog.show(target);
            }
        });
        add(addQuestionPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
    }

    private enum ButtonAction {
        LOGIN, LOGOUT, REGISTER, ADMIN, PROFILE, ASK_QUESTION;

        @Override
        public String toString() {
            switch (this) {
                case LOGIN:
                    return "loginAction";
                case LOGOUT:
                    return "logoutAction";
                case REGISTER:
                    return "registerAction";
                case ADMIN:
                    return "adminAction";
                case PROFILE:
                    return "profileAction";
                case ASK_QUESTION:
                    return "askQuestion";
                default:
                    return "";
            }
        }
    }

    private abstract class ActionPanel extends BaseEltilandPanel {

        public ActionPanel(String id, ButtonAction action) {
            super(id);

            WebMarkupContainer iconContainer = new WebMarkupContainer("iconContainer");
            iconContainer.add(new AttributeAppender("class", new Model<>(action.toString()), " "));

            add(iconContainer);
            add(new Label("label", new ResourceModel(action.toString())));

            add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    onClick(target);
                }
            });
        }

        protected abstract void onClick(AjaxRequestTarget target);
    }
}
