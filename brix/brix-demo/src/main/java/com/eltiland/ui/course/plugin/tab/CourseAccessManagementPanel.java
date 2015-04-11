package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CourseInvoiceManager;
import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Courses paid invoice management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CourseAccessManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private CourseInvoiceManager courseInvoiceManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;


    private final EltiDefaultDataGrid<CourseAccessDataSource, CourseInvoice> grid;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseAccessManagementPanel.class);

    public CourseAccessManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<CourseAccessDataSource, CourseInvoice>> columns = new ArrayList<>();

        columns.add(new PropertyWrapColumn(new ResourceModel("dateLabel"), "creationDate", "creationDate"));
        columns.add(new PropertyWrapColumn(new ResourceModel("userLabel"), "listener.name", null) {
            @Override
            protected void initialize(Object object) {
                genericManager.initialize(object, ((CourseInvoice) object).getListener());
            }
        });
        columns.add(new PropertyWrapColumn(new ResourceModel("courseLabel"), "course.name", null) {
            @Override
            protected void initialize(Object object) {
                genericManager.initialize(object, ((CourseInvoice) object).getCourse());
            }
        });
        columns.add(new AbstractColumn<CourseAccessDataSource, CourseInvoice>(
                "actionColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(
                    WebMarkupContainer components, String s, final IModel<CourseInvoice> courseInvoiceIModel) {
                return new ActionPanel(s, courseInvoiceIModel) {
                    @Override
                    protected void onClose(AjaxRequestTarget target) {
                        try {
                            emailMessageManager.sendCourseAccessDeniedToUser(courseInvoiceIModel.getObject());
                        } catch (EmailException e) {
                            LOGGER.error("Cannot send mail to user", e);
                            throw new WicketRuntimeException("Cannot send mail to user", e);
                        }

                        try {
                            genericManager.delete(courseInvoiceIModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot delete invoice", e);
                            throw new WicketRuntimeException("Cannot delete invoice", e);
                        }
                        target.add(grid);
                        ELTAlerts.renderOKPopup(getString("denyMessage"), target);
                    }

                    @Override
                    protected void onApply(AjaxRequestTarget target) {
                        CourseInvoice invoice = courseInvoiceIModel.getObject();
                        invoice.setApply(true);

//                        try {
//                            emailMessageManager.sendCourseAccessGrantedToUser(courseInvoiceIModel.getObject());
//                        } catch (EmailException e) {
//                            LOGGER.error("Cannot send mail to user", e);
//                            throw new WicketRuntimeException("Cannot send mail to user", e);
//                        }

                        try {
                            genericManager.update(invoice);
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update invoice", e);
                            throw new WicketRuntimeException("Cannot update invoice", e);
                        }
                        target.add(grid);

                        ELTAlerts.renderOKPopup(getString("applyMessage"), target);
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new CourseAccessDataSource(), columns);
        add(grid);
    }

    private class CourseAccessDataSource implements IDataSource<CourseInvoice> {
        @Override
        public void query(IQuery iQuery, IQueryResult<CourseInvoice> courseInvoiceIQueryResult) {
            int count = courseInvoiceManager.getInvoicesCount();
            courseInvoiceIQueryResult.setTotalCount(count);

            if (count < 1) {
                courseInvoiceIQueryResult.setItems(Collections.<CourseInvoice>emptyIterator());
            }

            String sortProperty = "creationDate";
            boolean isAscending = false;

            if (!iQuery.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = iQuery.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            courseInvoiceIQueryResult.setItems(
                    courseInvoiceManager.getCourseInvoiceList(
                            iQuery.getFrom(), iQuery.getCount(), sortProperty, isAscending).iterator());

        }

        @Override
        public IModel<CourseInvoice> model(CourseInvoice courseInvoice) {
            return new GenericDBModel<>(CourseInvoice.class, courseInvoice);
        }

        @Override
        public void detach() {
        }
    }

    private abstract class ActionPanel extends BaseEltilandPanel<CourseInvoice> {

        EltiAjaxLink applyButton = new EltiAjaxLink("applyButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onApply(target);
            }
        };

        EltiAjaxLink closeButton = new EltiAjaxLink("closeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onClose(target);
            }
        };

        protected ActionPanel(String id, IModel<CourseInvoice> courseInvoiceIModel) {
            super(id, courseInvoiceIModel);

            add(applyButton);
            add(closeButton);

            applyButton.add(new AttributeModifier("title", new ResourceModel("applyAction")));
            closeButton.add(new AttributeModifier("title", new ResourceModel("denyAction")));

            applyButton.add(new TooltipBehavior());
            closeButton.add(new TooltipBehavior());
        }

        abstract protected void onClose(AjaxRequestTarget target);

        abstract protected void onApply(AjaxRequestTarget target);
    }
}
