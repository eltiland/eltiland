package com.eltiland.ui.course.components.listeners;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.users.plugin.tab.UserDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.datagrid.DataGrid;
import com.inmethod.grid.datagrid.DefaultDataGrid;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog panel for selecting user.
 *
 * @author Aleksey Plotnikov.
 */
public class ListenerSelectPanel extends ELTDialogPanel implements IDialogCloseCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseListenersGridPanel.class);

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;

    private IDialogCloseCallback.IDialogActionProcessor callback;

    private final DataGrid<UserDataSource, User> grid;

    private TextField<String> searchField = new TextField("searchField", new Model<String>());

    private boolean hasConfirmation = false;

    private EltiAjaxSubmitLink searchButton = new EltiAjaxSubmitLink("searchButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            target.add(grid);
        }
    };

    public ListenerSelectPanel(String id, final IModel<Course> courseIModel) {
        super(id);

        List<IGridColumn<UserDataSource, User>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<UserDataSource, User, String>
                (new ResourceModel("nameColumn"), "name", "name") {
            @Override
            public int getInitialSize() {
                return 270;
            }
        });
        columns.add(new PropertyColumn<UserDataSource, User, String>
                (new ResourceModel("emailColumn"), "email", "email") {
            @Override
            public int getInitialSize() {
                return 200;
            }
        });
        columns.add(new AbstractColumn<UserDataSource, User>(
                "actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer components, String s, final IModel<User> simpleUserIModel) {
                return new ActionPanel(s) {
                    @Override
                    protected void onSelect(AjaxRequestTarget target) {
                        CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(
                                courseIModel.getObject(), null);
                        // check for existence
                        try {
                            CoursePayment checkPayment =
                                    coursePaymentManager.getPayment(simpleUserIModel.getObject(), invoice, false);
                            if (checkPayment != null && checkPayment.getStatus()) {
                                ELTAlerts.renderErrorPopup(getString("errorExists"), target);
                                return;
                            }
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot get payment", e);
                            throw new WicketRuntimeException("Cannot get payment", e);
                        }

                        CoursePayment payment = new CoursePayment();
                        payment.setListener(simpleUserIModel.getObject());
                        payment.setPrice(hasConfirmation ? BigDecimal.valueOf(0) : invoice.getPrice());
                        payment.setTerm(invoice.getTerm());

                        payment.setInvoice(invoice);
                        try {
                            coursePaymentManager.createPayment(payment);
                            if (hasConfirmation) {
                                coursePaymentManager.payCoursePayment(payment);
                            }
                        } catch (EltilandManagerException | UserException | ConstraintException e) {
                            LOGGER.error("Cannot create and pay payment", e);
                            throw new WicketRuntimeException("Cannot create and pay payment", e);
                        }

                        callback.process(target);
                    }
                };
            }
        });

        grid = new DefaultDataGrid<UserDataSource, User>("grid",
                new Model<UserDataSource>(new UserDataSource() {
                    @Override
                    public UserManager getManager() {
                        return userManager;
                    }

                    @Override
                    public TextField getSearchField() {
                        return searchField;
                    }
                }), columns) {
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                searchField.setModelObject(null);
            }
        };

        form.add(searchField);
        form.add(searchButton);
        form.add(grid);
    }

    @Override
    protected String getHeader() {
        return getString("selectHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public void setCloseCallback(IDialogCloseCallback.IDialogActionProcessor callback) {
        this.callback = callback;
    }

    public void setConfirmation(boolean value) {
        hasConfirmation = value;
    }

    private abstract class ActionPanel extends BaseEltilandPanel {

        public ActionPanel(String id) {
            super(id);

            add(new EltiAjaxLink("selectLink") {
                {
                    if (hasConfirmation) {
                        add(new ConfirmationDialogBehavior(new ResourceModel("freeConfirmation")));
                    }
                }

                @Override
                public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                    onSelect(ajaxRequestTarget);
                }
            });
        }

        abstract protected void onSelect(AjaxRequestTarget target);
    }
}
