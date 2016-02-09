package com.eltiland.ui.webinars.plugin.tab.record;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.export.ExportPeriodPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.brixcms.workspace.Workspace;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Webinars records invoices management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WRInvoiceManagementPanel extends BaseEltilandPanel<Workspace> {
    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private final ELTTable<WebinarRecordPayment> grid;

    private Dialog<ExportPeriodPanel> periodDialog = new Dialog<ExportPeriodPanel>("periodDialog", 320) {
        @Override
        public ExportPeriodPanel createDialogPanel(String id) {
            return new ExportPeriodPanel<>(id, WebinarRecordPayment.class);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WRInvoiceManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<WebinarRecordPayment>("grid", 10) {
            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.EXPORT_EXCEL);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case DOWNLOAD:
                        return getString("downloadTooltip");
                    case SEND:
                        return getString("sendTooltip");
                    case EXPORT_EXCEL:
                        return getString("downloadReportTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected String getFileName(IModel<WebinarRecordPayment> rowModel) {
                return "certificate.pdf";
            }

            @Override
            protected InputStream getInputStream(IModel<WebinarRecordPayment> rowModel) throws
                    ResourceStreamNotFoundException {
                try {
                    return webinarCertificateGenerator.generateRecordCertificate(
                            rowModel.getObject());
                } catch (EltilandManagerException e) {
                    return null;
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<WebinarRecordPayment> rowModel) {
                return Arrays.asList(GridAction.DOWNLOAD, GridAction.SEND);
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected List<IColumn<WebinarRecordPayment>> getColumns() {
                List<IColumn<WebinarRecordPayment>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<WebinarRecordPayment>(new ResourceModel("userLabel"), "user.name",
                        "userProfile.name"));
                columns.add(new PropertyColumn<WebinarRecordPayment>(new ResourceModel("emailLabel"), "user.email",
                        "userProfile.email"));
                columns.add(new PropertyColumn<WebinarRecordPayment>(new ResourceModel("dateLabel"), "date",
                        "date"));
                columns.add(new PropertyColumn<WebinarRecordPayment>(new ResourceModel("webinarLabel"), "webinar.name",
                        "record.webinar.name"));
                columns.add(new AbstractColumn<WebinarRecordPayment>(new ResourceModel("priceLabel"), "price") {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarRecordPayment>> components, String s,
                                             IModel<WebinarRecordPayment> webinarRecordPaymentIModel) {
                        String data = String.format("%.2f руб.", webinarRecordPaymentIModel.getObject().getPrice());
                        components.add(new Label(s, new Model<>(data)));
                    }
                });

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarRecordPaymentManager.getPaymentsList(first, count, getSort().getProperty(),
                        getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return webinarRecordPaymentManager.getPaymentsCount(getSearchString());
            }

            @Override
            protected void onClick(IModel<WebinarRecordPayment> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case SEND:
                        try {
                            InputStream stream = getInputStream(rowModel);
                            emailMessageManager.sendRecordLinkToUser(rowModel.getObject(), stream);
                            ELTAlerts.renderOKPopup(getString("certificateSended"), target);
                        } catch (EmailException | ResourceStreamNotFoundException e) {
                            ELTAlerts.renderErrorPopup(getString("certificateSendError"), target);
                        }

                        break;

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
