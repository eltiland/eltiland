package com.eltiland.ui.webinars.components.datatable;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.datagrid.styled.DataTablePanel;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.webinars.components.RecordActionPanel;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Webinar record data table.
 *
 * @author Aleksey PLotnikov.
 */
public class RecordDataTablePanel extends DataTablePanel<WebinarRecord> {

    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataTablePanel.class);

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public RecordDataTablePanel(String id, int maxRows) {
        this(id, new EltiDataProviderBase<WebinarRecord>() {
            @SpringBean
            private GenericManager genericManager;
            @SpringBean
            private WebinarRecordManager webinarRecordManager;

            @Override
            public Iterator iterator(int first, int count) {
                return webinarRecordManager.getList(
                        first, count, getSort().getProperty(), getSort().isAscending(), false).iterator();
            }

            @Override
            public int size() {
                return webinarRecordManager.getCount(false);
            }
        }, maxRows);
    }

    /**
     * Default constructor. Will show all users.
     *
     * @param id panel's id.
     */
    public RecordDataTablePanel(String id, ISortableDataProvider<WebinarRecord> dataProvider, int maxRows) {
        super(id, dataProvider, maxRows);
    }

    @Override
    protected List<IColumn<WebinarRecord>> getColumns() {
        ArrayList<IColumn<WebinarRecord>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<WebinarRecord>(
                new ResourceModel("webinarColumn"), "name", "name"));
        columns.add(new AbstractColumn<WebinarRecord>(new ResourceModel("managerColumn")) {
            @Override
            public void populateItem(
                    Item<ICellPopulator<WebinarRecord>> cellItem, String componentId, IModel<WebinarRecord> rowModel) {
                String managerName = rowModel.getObject().getWebinar().getManagername();
                String managerSurname = rowModel.getObject().getWebinar().getManagersurname();
                cellItem.add(new Label(componentId, managerName + " " + managerSurname));
            }
        });
        columns.add(new PriceColumn(new ResourceModel("priceColumn"), "price", "price") {
            @Override
            protected String getZeroPrice() {
                return getString("freePrice");
            }
        });
        columns.add(new AbstractColumn<WebinarRecord>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<WebinarRecord>> cellItem,
                                     String componentId, final IModel<WebinarRecord> rowModel) {
                cellItem.add(new RecordActionPanel(componentId) {
                    @Override
                    public void onAction(AjaxRequestTarget target) {
                        if (currentUserModel.getObject() == null) {
                            ELTAlerts.renderErrorPopup(getString("loginNeededMessage"), target);
                        } else {
                            if (webinarRecordPaymentManager.hasRecordInvoicesForCurrentUser(rowModel.getObject())) {
                                ELTAlerts.renderWarningPopup(getString("alreadySendedError"), target);
                            } else {
                                WebinarRecordPayment payment = new WebinarRecordPayment();
                                payment.setPrice(rowModel.getObject().getPrice());
                                payment.setRecord(rowModel.getObject());
                                payment.setStatus(PaidStatus.NEW);

                                if (!(rowModel.getObject().getPrice().longValue() == 0)) {
                                    payment.setPayLink(RandomStringUtils.randomAlphanumeric(10));
                                }
                                payment.setUserProfile(currentUserModel.getObject());

                                try {
                                    genericManager.saveNew(payment);
                                } catch (ConstraintException e) {
                                    LOGGER.error("Can't create new record invoice", e);
                                    throw new WicketRuntimeException("Can't create new record invoice", e);
                                }
                                try {
                                    emailMessageManager.sendRecordInvitationToUser(payment);
                                } catch (EmailException e) {
                                    LOGGER.error("Can't send mail", e);
                                    throw new WicketRuntimeException("Can't send mail", e);
                                }

                                ELTAlerts.renderOKPopup(getString("sendedMessage"), target);
                            }
                        }
                    }
                });
            }
        });
        return columns;
    }
}
