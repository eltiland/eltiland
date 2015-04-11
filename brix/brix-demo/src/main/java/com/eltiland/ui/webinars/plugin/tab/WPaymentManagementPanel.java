package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.export.Exportable;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.export.ExportButton;
import com.eltiland.ui.common.components.export.ExportPeriodPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.components.column.WebinarPriceColumn;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.*;

/**
 * Webinars announcements management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WPaymentManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private final ELTTable<WebinarUserPayment> grid;

    private Dialog<ExportPeriodPanel> periodDialog = new Dialog<ExportPeriodPanel>("periodDialog", 320) {
        @Override
        public ExportPeriodPanel createDialogPanel(String id) {
            return new ExportPeriodPanel<>(id, WebinarUserPayment.class);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WPaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<WebinarUserPayment>("grid", 10) {
            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EXPORT_EXCEL:
                        return getString("download");
                    default:
                        return "";
                }
            }

            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.EXPORT_EXCEL);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<IColumn<WebinarUserPayment>> getColumns() {
                List<IColumn<WebinarUserPayment>> columns = new ArrayList<>();

                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("nameLabel"), "webinar.name") {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> components, String s,
                                             IModel<WebinarUserPayment> webinarUserPaymentIModel) {
                        WebinarUserPayment item = webinarUserPaymentIModel.getObject();
                        genericManager.initialize(item, item.getWebinar());
                        components.add(new Label(s, new Model<>(item.getWebinar().getName())));
                    }
                });
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("managerLabel"),
                        "webinar.managersurname") {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> components, String s,
                                             IModel<WebinarUserPayment> webinarUserPaymentIModel) {
                        WebinarUserPayment item = webinarUserPaymentIModel.getObject();
                        genericManager.initialize(item, item.getWebinar());
                        String data = String.format("%s %s", item.getWebinar().getManagersurname(),
                                item.getWebinar().getManagername());
                        components.add(new Label(s, new Model<>(data)));
                    }
                });
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("memberLabel")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> components, String s,
                                             IModel<WebinarUserPayment> webinarUserPaymentIModel) {
                        WebinarUserPayment item = webinarUserPaymentIModel.getObject();
                        String name = String.format("%s %s", item.getUserSurname(), item.getUserName());
                        String data = String.format("%s\n%s", name, item.getUserEmail());
                        components.add(new MultiLineLabel(s, new Model<>(data)));
                    }
                });
                columns.add(new PropertyColumn<WebinarUserPayment>(new ResourceModel("dateLabel"), "date",
                        "date"));
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("priceLabel")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> components, String s,
                                             IModel<WebinarUserPayment> webinarUserPaymentIModel) {
                        WebinarUserPayment item = webinarUserPaymentIModel.getObject();
                        String data = String.format("%.2f руб.", item.getPrice());
                        components.add(new Label(s, new Model<>(data)));
                    }
                });

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarUserPaymentManager.getPaidPaymentsList(first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return webinarUserPaymentManager.getPaidPaymentsCount(getSearchString());
            }

            @Override
            protected void onClick(IModel<WebinarUserPayment> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case EXPORT_EXCEL:
                        periodDialog.show(target);

                        break;
                }
            }
        };

        add(grid.setOutputMarkupId(true));
        add(periodDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
