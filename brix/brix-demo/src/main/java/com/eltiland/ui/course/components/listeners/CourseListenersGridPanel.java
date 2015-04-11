package com.eltiland.ui.course.components.listeners;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Grid panel for course listeners.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseListenersGridPanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseListenersGridPanel.class);

    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;

    private CourseListenersDataTablePanel gridPanel;
    private WebMarkupContainer noUsersContainer;

    private Dialog<ListenerSelectPanel> listenerSelectPanelDialog =
            new Dialog<ListenerSelectPanel>("selectorDialog", 615) {
                @Override
                public ListenerSelectPanel createDialogPanel(String id) {
                    return new ListenerSelectPanel(id, CourseListenersGridPanel.this.getModel());
                }

                @Override
                public void registerCallback(ListenerSelectPanel panel) {
                    super.registerCallback(panel);
                    panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            close(target);
                            target.add(gridPanel);
                            target.add(noUsersContainer);
                        }
                    });
                }
            };

    private Dialog<CoursePriceChangePanel> coursePriceChangePanelDialog =
            new Dialog<CoursePriceChangePanel>("changePriceDialog", 310) {
                @Override
                public CoursePriceChangePanel createDialogPanel(String id) {
                    return new CoursePriceChangePanel(id);
                }

                @Override
                public void registerCallback(CoursePriceChangePanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CoursePayment>() {
                        @Override
                        public void process(IModel<CoursePayment> model, AjaxRequestTarget target) {
                            CoursePayment payment = model.getObject();
                            try {
                                genericManager.update(payment);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot update payment", e);
                                throw new WicketRuntimeException("Cannot update payment", e);
                            }
                            close(target);
                            target.add(gridPanel);
                        }
                    });
                }
            };

    public CourseListenersGridPanel(String id, final IModel<Course> courseIModel) {
        super(id, courseIModel);

        final boolean isPaid = coursePaidInvoiceManager.isCoursePaid(courseIModel.getObject());
        genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getFullVersion());

        String header = getString(isPaid ? "paidCourseHeader" :
                (courseIModel.getObject().getFullVersion().isEmpty() ? "preJoinHeader" : "freeCourseHeader"));

        add(new Label("gridHeader", header));

        final EltiDataProviderBase<User> gridProvider = new EltiDataProviderBase<User>() {
            @Override
            public Iterator iterator(int first, int count) {
                if (isPaid) {
                    return userManager.getCoursePaidListeners(
                            courseIModel.getObject(), first, count,
                            getSort().getProperty(), getSort().isAscending()).iterator();
                } else {
                    return userManager.getCourseListeners(
                            courseIModel.getObject(), first, count,
                            getSort().getProperty(), getSort().isAscending()).iterator();
                }
            }

            @Override
            public int size() {
                if (isPaid) {
                    return userManager.getCoursePaidListenersCount(courseIModel.getObject());
                } else {
                    genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getListeners());
                    return courseIModel.getObject().getListeners().size();
                }
            }
        };

        gridPanel = new CourseListenersDataTablePanel("grid", gridProvider, 30) {
            @Override
            protected IModel<Course> getCourseModel() {
                return CourseListenersGridPanel.this.getModel();
            }

            @Override
            protected void updateInfo(AjaxRequestTarget target) {
                target.add(gridPanel);
                target.add(noUsersContainer);
            }

            @Override
            protected void editPrice(AjaxRequestTarget target, IModel<CoursePayment> paymentIModel) {
                coursePriceChangePanelDialog.getDialogPanel().initData(paymentIModel);
                coursePriceChangePanelDialog.show(target);
            }

            @Override
            protected boolean isPaid() {
                return isPaid;
            }

            @Override
            public boolean isVisible() {
                return gridProvider.size() > 0;
            }
        };
        gridPanel.setOutputMarkupPlaceholderTag(true);
        add(gridPanel);

        noUsersContainer = new WebMarkupContainer("noUsersMessage") {
            @Override
            public boolean isVisible() {
                return gridProvider.size() == 0;
            }
        };
        noUsersContainer.setOutputMarkupPlaceholderTag(true);
        add(noUsersContainer);

        add(new EltiAjaxLink("addFreeAccessButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                listenerSelectPanelDialog.getDialogPanel().setConfirmation(true);
                listenerSelectPanelDialog.show(ajaxRequestTarget);
            }

            @Override
            public boolean isVisible() {
                return isPaid;
            }
        });

        add(new EltiAjaxLink("addPersonalAccessButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                listenerSelectPanelDialog.getDialogPanel().setConfirmation(false);
                listenerSelectPanelDialog.show(ajaxRequestTarget);
            }

            @Override
            public boolean isVisible() {
                return isPaid;
            }
        });

        add(listenerSelectPanelDialog);
        add(coursePriceChangePanelDialog);
    }
}
