package com.eltiland.ui.magazine.plugin.tab;

import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.model.magazine.Client;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.export.ExportPeriodPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for magazine payments.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazinePaymentPanel extends Panel {

    @SpringBean
    private ClientManager clientManager;

    private Dialog<ExportPeriodPanel> periodDialog = new Dialog<ExportPeriodPanel>("periodDialog", 320) {
        @Override
        public ExportPeriodPanel createDialogPanel(String id) {
            return new ExportPeriodPanel(id, Client.class);
        }
    };

    public MagazinePaymentPanel(String id, IModel<?> model) {
        super(id, model);

        add(new ELTTable<Client>("grid", 20) {

            @Override
            protected List<IColumn<Client>> getColumns() {
                List<IColumn<Client>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Client>(new ResourceModel("nameColumn"), "name", "name"));
                columns.add(new PropertyColumn<Client>(new ResourceModel("emailColumn"), "email", "email"));
                columns.add(new PropertyColumn<Client>(new ResourceModel("phoneColumn"), "phone", "phone"));
                columns.add(new PropertyColumn<Client>(new ResourceModel("dateColumn"), "date", "date"));
                columns.add(new PriceColumn(new ResourceModel("priceColumn"), "price", "price"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return clientManager.getAppliedClients(
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.EXPORT_EXCEL));
            }

            @Override
            protected int getSize() {
                return clientManager.getAppliedClientsCount();
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.EXPORT_EXCEL)) {
                    return getString("reportTooltip");
                } else return "";
            }

            @Override
            protected void onClick(IModel<Client> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.EXPORT_EXCEL)) {
                    periodDialog.show(target);
                }
            }
        });

        add(periodDialog);
    }
}
