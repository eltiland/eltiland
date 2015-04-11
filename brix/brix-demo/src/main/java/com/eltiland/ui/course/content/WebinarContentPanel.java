package com.eltiland.ui.course.content;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course.WebinarCourseItem;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.webinars.components.WebinarNewUserPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Folder content panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarContentPanel extends CourseContentPanel<WebinarCourseItem> {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarContentPanel.class);

    public WebinarContentPanel(String id, IModel<WebinarCourseItem> webinarCourseItemIModel) {
        super(id, webinarCourseItemIModel);
    }

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    @Override
    protected WebMarkupContainer getContent() {
        genericManager.initialize(getModelObject(), getModelObject().getWebinar());

        final Dialog<WebinarNewUserPanel> webinarNewUserPanelDialog
                = new Dialog<WebinarNewUserPanel>("addUserDialog", 330) {
            @Override
            public WebinarNewUserPanel createDialogPanel(String id) {
                return new WebinarNewUserPanel(id) {
                    @Override
                    public void onLogin(AjaxRequestTarget target, IModel<Webinar> webinarIModel) {
                    }
                };
            }

            @Override
            public void registerCallback(WebinarNewUserPanel panel) {
                super.registerCallback(panel);
                panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<WebinarUserPayment>() {
                    @Override
                    public void process(IModel<WebinarUserPayment> model, AjaxRequestTarget target) {
                        try {
                            webinarUserPaymentManager.createUser(model.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot create webinar user", e);
                            throw new WicketRuntimeException("Cannot create webinar user", e);
                        } catch (EmailException e) {
                            LOGGER.error("Cannot send mail to user", e);
                            throw new WicketRuntimeException("Cannot send mail to user", e);
                        }

                        close(target);

                        boolean isFree = model.getObject().getPrice() == null
                                || model.getObject().getPrice().equals(BigDecimal.valueOf(0));

                        ELTAlerts.renderOKPopup(getString(isFree ? "signupFreeMessage" : "signupPaidMessage"), target);
                    }
                });
            }
        };


        WebMarkupContainer content = new WebMarkupContainer("content");
        WebMarkupContainer webinarContainer = new WebMarkupContainer("webinarContainer") {
            @Override
            public boolean isVisible() {
                genericManager.initialize(getModelObject(), getModelObject().getWebinar());
                return getModelObject().getWebinar() != null && getModelObject().getWebinar().isApproved();
            }
        };

        WebMarkupContainer memberContainer = new WebMarkupContainer("memberContainer") {
            @Override
            public boolean isVisible() {
                if (currentUserModel.getObject() == null) {
                    return false;
                }

                genericManager.initialize(getModelObject(), getModelObject().getWebinar());
                return webinarUserPaymentManager.hasAlreadyRegistered(
                        getModelObject().getWebinar(), currentUserModel.getObject().getEmail());
            }
        };

        WebMarkupContainer notMemberContainer = new WebMarkupContainer("notMemberContainer") {
            @Override
            public boolean isVisible() {
                if (currentUserModel.getObject() == null) {
                    return true;
                }

                genericManager.initialize(getModelObject(), getModelObject().getWebinar());
                return !(webinarUserPaymentManager.hasAlreadyRegistered(
                        getModelObject().getWebinar(), currentUserModel.getObject().getEmail()));
            }
        };

        EltiAjaxLink signUpButton = new EltiAjaxLink("signUpButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                genericManager.initialize(WebinarContentPanel.this.getModelObject(),
                        WebinarContentPanel.this.getModelObject().getWebinar());
                webinarNewUserPanelDialog.getDialogPanel().initWebinarData(
                        WebinarContentPanel.this.getModelObject().getWebinar());
                webinarNewUserPanelDialog.show(target);
            }
        };

        Label message = new Label("message", new Model<String>()) {
            @Override
            public boolean isVisible() {
                genericManager.initialize(getModelObject(), getModelObject().getWebinar());
                return (getModelObject().getWebinar() == null) || !(getModelObject().getWebinar().isApproved());
            }
        };

        if (getModelObject().getWebinar() == null) {
            message.setDefaultModelObject(getString("inWorkMessage"));
        } else if (!(getModelObject().getWebinar().isApproved())) {
            message.setDefaultModelObject(getString("notApprovedMessage"));
        }

        content.add(webinarContainer);
        webinarContainer.add(memberContainer);
        webinarContainer.add(notMemberContainer);
        notMemberContainer.add(signUpButton);
        content.add(message);
        content.add(webinarNewUserPanelDialog);
        return content;
    }
}
