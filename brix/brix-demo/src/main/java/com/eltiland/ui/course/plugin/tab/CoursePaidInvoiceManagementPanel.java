package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePaidTerm;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.GeneralPriceColumn;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.panels.changeprice.ChangePricePanel;
import com.eltiland.ui.course.plugin.components.CourseInvoiceKindColumn;
import com.eltiland.ui.course.plugin.components.CourseItemColumn;
import com.eltiland.ui.course.plugin.components.InvoiceActionPanel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Courses paid invoice management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CoursePaidInvoiceManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private GenericManager genericManager;

    private final Logger LOGGER = LoggerFactory.getLogger(CoursePaidInvoiceManagementPanel.class);

    private IModel<CoursePaidInvoice> currentInvoiceModel = new GenericDBModel<>(CoursePaidInvoice.class);

    private Dialog<ChangePricePanel> changePricePanelDialog = new Dialog<ChangePricePanel>("changePriceDialog", 300) {
        @Override
        public ChangePricePanel createDialogPanel(String id) {
            return new ChangePricePanel(id, new Model<>(BigDecimal.valueOf(0))) {
                @Override
                protected boolean willFreeCheckBoxShown() {
                    return false;
                }
            };
        }

        @Override
        public void registerCallback(ChangePricePanel panel) {
            super.registerCallback(panel);
            panel.setProcessCallback(new IDialogProcessCallback.IDialogActionProcessor<BigDecimal>() {
                @Override
                public void process(IModel<BigDecimal> model, AjaxRequestTarget target) {
                    CoursePaidInvoice invoice = currentInvoiceModel.getObject();
                    invoice.setPrice(model.getObject());
                    try {
                        coursePaidInvoiceManager.updateCoursePaidInvoice(invoice);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot update course invoice.", e);
                        throw new WicketRuntimeException("Cannot update course invoice.", e);
                    }
                    close(target);
                    ELTAlerts.renderOKPopup(getString("updateMessage"), target);
                    target.add(grid);
                }
            });
        }
    };

    private final EltiDefaultDataGrid<CoursePaidInvoiceDataSource, CoursePaidInvoice> grid;

    public CoursePaidInvoiceManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>> columns = new ArrayList<>();

        columns.add(new CourseInvoiceKindColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>(
                "kindColumn", new ResourceModel("kindLabel")));
        columns.add(new PropertyWrapColumn(new ResourceModel("dateLabel"), "creationdate", "creationdate"));
        columns.add(new CourseItemColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>(
                "itemColumn", new ResourceModel("courseLabel")));
        columns.add(new GeneralPriceColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>(
                "priceColumn", new ResourceModel("priceLabel"), "price"));
        columns.add(new AbstractColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>(
                "termColumn", new ResourceModel("termLabel")) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, IModel<CoursePaidInvoice> rowModel) {
                String text = "";
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getTerm());
                CoursePaidTerm term = rowModel.getObject().getTerm();

                if ((term != null) && ((term.getYears() != 0) || (term.getMonths() != 0) || (term.getDays() != 0))) {
                    text += getDateString(term.getYears(), "year", "years", "years_many");
                    text += getDateString(term.getMonths(), "month", "months", "months_many");
                    text += getDateString(term.getDays(), "day", "days", "days_many");
                } else {
                    text += getString("noTerm");
                }
                return new Label(componentId, text);
            }

            @Override
            public int getInitialSize() {
                return 200;
            }
        });
        columns.add(new AbstractColumn<CoursePaidInvoiceDataSource, CoursePaidInvoice>(
                "actionColumn", new ResourceModel("actionLabel")) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId, final IModel<CoursePaidInvoice> rowModel) {
                return new InvoiceActionPanel(componentId, rowModel) {
                    @Override
                    public void onApply(AjaxRequestTarget target) {
                        CoursePaidInvoice invoice = rowModel.getObject();
                        invoice.setStatus(true);
                        try {
                            coursePaidInvoiceManager.updateCoursePaidInvoice(invoice);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot apply course invoice.", e);
                            throw new WicketRuntimeException("Cannot apply course invoice.", e);
                        }
                        ELTAlerts.renderOKPopup(getString("applyMessage"), target);
                        target.add(grid);
                    }

                    @Override
                    public void onEdit(AjaxRequestTarget target) {
                        changePricePanelDialog.getDialogPanel().initEditMode(rowModel.getObject().getPrice());
                        currentInvoiceModel.setObject(rowModel.getObject());
                        changePricePanelDialog.show(target);
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        CoursePaidInvoice invoice = rowModel.getObject();
                        try {
                            coursePaidInvoiceManager.removeCoursePaidInvoice(invoice);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot remove course invoice.", e);
                            throw new WicketRuntimeException("Cannot remove course invoice.", e);
                        }
                        ELTAlerts.renderOKPopup(getString("denyMessage"), target);
                        target.add(grid);
                    }
                };
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new CoursePaidInvoiceDataSource(), columns);
        add(grid);
        add(changePricePanelDialog);
    }

    private class CoursePaidInvoiceDataSource implements IDataSource<CoursePaidInvoice> {
        @Override
        public void query(IQuery query, IQueryResult<CoursePaidInvoice> result) {
            int count = coursePaidInvoiceManager.getCountOfNotApprovedInvoices();
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<CoursePaidInvoice>emptyIterator());
            }

            String sortProperty = "creationDate";
            boolean isAscending = false;

            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            result.setItems(coursePaidInvoiceManager.getListOfNotApprovedInvoices(
                    query.getFrom(), query.getCount(), sortProperty, isAscending).iterator());
        }

        @Override
        public IModel<CoursePaidInvoice> model(CoursePaidInvoice object) {
            return new GenericDBModel<>(CoursePaidInvoice.class, object);
        }

        @Override
        public void detach() {
        }
    }

    private String getDateString(int value, String one, String couple, String many) {
        if (value != 0) {
            if (value == 1) {
                return getString(one);
            } else if ((value > 1) && (value < 5)) {
                return String.format(getString(couple), value);
            } else {
                return String.format(getString(many), value);
            }
        } else {
            return "";
        }
    }
}
