package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.components.column.WebinarUserNameColumn;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Member list panel
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarMemberPanel extends ELTDialogPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarMemberPanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private ELTTable<WebinarUserPayment> grid;

    public WebinarMemberPanel(String id) {
        super(id);

        grid = new ELTTable<WebinarUserPayment>("grid", 20) {
            @Override
            protected List<IColumn<WebinarUserPayment>> getColumns() {
                List<IColumn<WebinarUserPayment>> columns = new ArrayList<>();
                columns.add(new WebinarUserNameColumn(new ResourceModel("fioColumnTitle"), "userSurname"));
                columns.add(new PropertyColumn<WebinarUserPayment>(
                        new ResourceModel("emailColumnTitle"), "userEmail", "userEmail"));
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("roleColumnTitle"), "role") {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> cellItem,
                                             String componentId, IModel<WebinarUserPayment> rowModel) {
                        cellItem.add(new Label(componentId,
                                getString(rowModel.getObject().getRole().toString() + ".role")));
                    }
                });
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("certTitle"), "cert") {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> item,
                                             String s, IModel<WebinarUserPayment> iModel) {
                        item.add(new Label(s, getString(iModel.getObject().isCert() ? "enabled" : "disabled")));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                try {
                    return webinarUserPaymentManager.getWebinarUserList(webinarIModel.getObject(), first, count,
                            getSort().getProperty(), getSort().isAscending(), PaidStatus.CONFIRMED).iterator();
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot get user list", e);
                    throw new WicketRuntimeException("Cannot get user list", e);
                }
            }

            @Override
            protected int getSize() {
                return webinarUserPaymentManager.getWebinarConfirmedUserCount(webinarIModel.getObject());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<WebinarUserPayment> rowModel) {
                return new ArrayList<>(Arrays.asList(
                        GridAction.DOWNLOAD, GridAction.CERTIFICATE, GridAction.ON, GridAction.OFF));
            }

            @Override
            protected String getFileName(IModel<WebinarUserPayment> rowModel) {
                return "certificate.pdf";
            }

            @Override
            protected InputStream getInputStream(IModel<WebinarUserPayment> rowModel)
                    throws ResourceStreamNotFoundException {
                try {
                    return webinarCertificateGenerator.generateCertificate(rowModel.getObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot generate certificate", e);
                    throw new WicketRuntimeException("Cannot generate certificate", e);
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case DOWNLOAD:
                        return getString("download.tooltip");
                    case CERTIFICATE:
                        return getString("send.tooltip");
                    case ON:
                        return getString("on.tooltip");
                    case OFF:
                        return getString("off.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<WebinarUserPayment> rowModel) {
                switch (action) {
                    case ON:
                        return !(rowModel.getObject().isCert());
                    case OFF:
                        return rowModel.getObject().isCert();
                    default:
                        return true;
                }
            }

            @Override
            protected void onClick(IModel<WebinarUserPayment> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case DOWNLOAD:
                        break;
                    case CERTIFICATE:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getWebinar());
                        genericManager.initialize(rowModel.getObject().getWebinar(),
                                rowModel.getObject().getWebinar().getRecord());
                        if (rowModel.getObject().getWebinar().getRecord() == null) {
                            ELTAlerts.renderErrorPopup(getString("send.error"), target);
                        } else {
                            try {
                                emailMessageManager.sendWebinarCertificate(rowModel.getObject(),
                                        webinarCertificateGenerator.generateCertificate(rowModel.getObject()));
                            } catch (EmailException e) {
                                LOGGER.error("Cannot save mail", e);
                                throw new WicketRuntimeException("Cannot save mail", e);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot generate certificate", e);
                                throw new WicketRuntimeException("Cannot generate certificate", e);
                            }
                            ELTAlerts.renderOKPopup(getString("send.message"), target);
                        }
                    case ON:
                    {
                        rowModel.getObject().setCert(true);
                        try {
                            webinarUserPaymentManager.update(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Error while saving user payment", e);
                            ELTAlerts.renderErrorPopup("Error while saving user payment", target);
                        }
                        target.add(grid);
                        break;
                    }
                    case OFF:
                    {
                        rowModel.getObject().setCert(false);
                        try {
                            webinarUserPaymentManager.update(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Error while saving user payment", e);
                            ELTAlerts.renderErrorPopup("Error while saving user payment", target);
                        }
                        target.add(grid);
                        break;
                    }
                }
            }
        };

        form.add(grid.setOutputMarkupId(true));
    }

    public void initData(IModel<Webinar> webinarIModel) {
        this.webinarIModel = webinarIModel;
    }

    @Override
    protected String getHeader() {
        return getString("memberHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }
}
