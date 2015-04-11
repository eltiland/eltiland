package com.eltiland.ui.course.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.course2.listeners.ListenerType;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.course.CourseNewContentPage;
import com.eltiland.ui.course.components.panels.CourseInvoicePanel;
import com.eltiland.ui.paymentnew.PaymentPage;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;

/**
 * Button for jump to selected course version.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseVersionButton extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private GenericManager genericManager;

    private Dialog<CourseInvoicePanel> invoicePanelDialog = new Dialog<CourseInvoicePanel>("invoiceDialog", 445) {
        @Override
        public CourseInvoicePanel createDialogPanel(String id) {
            return new CourseInvoicePanel(id, getModel());
        }

        @Override
        public void registerCallback(CourseInvoicePanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseListener>() {
                @Override
                public void process(IModel<ELTCourseListener> model, AjaxRequestTarget target) {
                    close(target);
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

    public CourseVersionButton(String id, IModel<ELTCourse> eltCourseIModel, ContentStatus status) {
        super(id, eltCourseIModel);

        WebMarkupContainer button = new WebMarkupContainer("button");

        final boolean isDemo = status.equals(ContentStatus.DEMO);
        final BigDecimal price = isDemo ? BigDecimal.ZERO : getModelObject().getPrice();

        final boolean isInvoice = getModelObject().isNeedConfirm();
        final boolean isFree = !isInvoice && (price == null || price.equals(BigDecimal.ZERO));
        final boolean isEnter = !isFree && !isInvoice &&
                courseListenerManager.hasAccess(currentUserModel.getObject(), getModelObject());

        genericManager.initialize(getModelObject(), getModelObject().getContent());
        final boolean isFullPresent = getModelObject().getContent() != null && getModelObject().getContent().size() > 0;

        // Free access to course
        button.add(new WebMarkupContainer("free") {
            @Override
            public boolean isVisible() {
                return isDemo || isFree;
            }
        });

        // Access to course requires accept from the admin
        button.add(new WebMarkupContainer("invoice") {
            @Override
            public boolean isVisible() {
                return !isDemo && isInvoice;
            }
        });

        button.add(new WebMarkupContainer("enter") {
            @Override
            public boolean isVisible() {
                return !isDemo && isEnter;
            }
        });


        WebMarkupContainer paidContainer = new WebMarkupContainer("paid") {
            @Override
            public boolean isVisible() {
                return !isDemo && !isFree && !isInvoice && !isEnter && isFullPresent;
            }
        };

        button.add(paidContainer);
        paidContainer.add(new Label("price", paidContainer.isVisible() ? price.toString() : ""));

        button.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (isDemo) {
                    throw new RestartResponseException(CourseNewContentPage.class, new PageParameters()
                            .add(CourseNewContentPage.PARAM_ID, CourseVersionButton.this.getModelObject().getId())
                            .add(CourseNewContentPage.PARAM_VERSION, CourseNewContentPage.DEMO_VERSION));
                } else {
                    if (isFree || isEnter) {
                        throw new RestartResponseException(CourseNewContentPage.class, new PageParameters()
                                .add(CourseNewContentPage.PARAM_ID, CourseVersionButton.this.getModelObject().getId())
                                .add(CourseNewContentPage.PARAM_VERSION, CourseNewContentPage.FULL_VERSION));
                    } else {
                        if (isInvoice) {
                            invoicePanelDialog.show(target);
                        } else {
                            ELTCourseListener listener = courseListenerManager.getItem(
                                    currentUserModel.getObject(), getModelObject());
                            if (listener == null) {
                                listener = new ELTCourseListener();
                                listener.setStatus(PaidStatus.NEW);
                                listener.setCourse(getModelObject());
                                listener.setDays(getModelObject().getDays());
                                listener.setPrice(getModelObject().getPrice());
                                listener.setListener(currentUserModel.getObject());
                                listener.setType(ListenerType.PHYSICAL);
                                try {
                                    listener = courseListenerManager.create(listener);
                                } catch (CourseException e) {
                                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                }
                            }
                            throw new RestartResponseException(PaymentPage.class,
                                    new PageParameters().add(PaymentPage.PARAM_ID, listener.getId()));
                        }
                    }
                }
            }
        });

        button.add(new AttributeAppender("class", new Model<>(isDemo ? "demo_button" : "full_button"), " "));
        add(button);
        add(invoicePanelDialog);

        button.add(new Label("name", getString(isDemo ? "demo.label" : "full.label")));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_VERSION_SELECTOR);
    }
}
