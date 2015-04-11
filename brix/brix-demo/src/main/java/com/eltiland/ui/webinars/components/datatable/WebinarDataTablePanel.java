package com.eltiland.ui.webinars.components.datatable;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarUser;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.datagrid.styled.DataTablePanel;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.multiply.WebinarAddUsersPanel;
import org.apache.commons.lang.RandomStringUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Webinar data table control.
 *
 * @author Aleksey Plotnikov
 */
public abstract class WebinarDataTablePanel extends DataTablePanel<Webinar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarDataTablePanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private Dialog<WebinarAddUsersPanel> webinarAddUsersPanelDialog =
            new Dialog<WebinarAddUsersPanel>("addCoupleUsersDialog", 420) {
                @Override
                public WebinarAddUsersPanel createDialogPanel(String id) {
                    return new WebinarAddUsersPanel(id);
                }

                @Override
                public void registerCallback(WebinarAddUsersPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<List<User>>() {
                        @Override
                        public void process(IModel<List<User>> model, AjaxRequestTarget target) {
                            if (!(model.getObject().isEmpty())) {
                                BigDecimal totalPrice = BigDecimal.ZERO;
                                for (int i = 0; i < model.getObject().size(); i++) {
                                    totalPrice = totalPrice.add(webinarIModel.getObject().getPrice());
                                }

                                WebinarMultiplyPayment payment = new WebinarMultiplyPayment();
                                payment.setWebinar(webinarIModel.getObject());
                                payment.setPrice(totalPrice);
                                payment.setPayLink(RandomStringUtils.randomAlphanumeric(10));
                                payment.setStatus(false);

                                try {
                                    genericManager.saveNew(payment);

                                    for (User user : model.getObject()) {
                                        WebinarUser wUser = new WebinarUser();
                                        wUser.setUser(user);
                                        wUser.setPayment(payment);
                                        genericManager.saveNew(wUser);
                                    }
                                } catch (ConstraintException e) {
                                    LOGGER.error("Got exception when creating payment", e);
                                    throw new WicketRuntimeException("Got exception when creating payment", e);
                                }
                                try {
                                    emailMessageManager.sendWebinarMultiplyInvitationToUser(payment);
                                } catch (EmailException e) {
                                    LOGGER.error("Got exception ", e);
                                    throw new WicketRuntimeException("Got exception when creating payment", e);
                                }
                                close(target);
                                ELTAlerts.renderOKPopup(getString("usersInviteMessage"), target);
                            }
                        }
                    });
                }
            };

    /**
     * Table constructor.
     *
     * @param id      panel's id.
     * @param maxRows rows limit
     */
    public WebinarDataTablePanel(String id, EltiDataProviderBase<Webinar> provider, int maxRows) {
        super(id, provider, maxRows);
        add(webinarAddUsersPanelDialog);
    }

    @Override
    protected List<IColumn<Webinar>> getColumns() {
        ArrayList<IColumn<Webinar>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<Webinar>(new ResourceModel("nameColumn"), "name", "name"));
        columns.add(new PropertyColumn<Webinar>(new ResourceModel("descriptionColumn"), "description", "description"));
        columns.add(new AbstractColumn<Webinar>(new ResourceModel("managerColumn")) {
            @Override
            public void populateItem(
                    Item<ICellPopulator<Webinar>> cellItem, String componentId, IModel<Webinar> rowModel) {
                String managerName = rowModel.getObject().getManagername();
                String managerSurname = rowModel.getObject().getManagersurname();
                cellItem.add(new Label(componentId, managerName + " " + managerSurname));
            }
        });
        columns.add(new PropertyColumn<Webinar>(new ResourceModel("startDateColumn"), "startDate", "startDate"));
        columns.add(new PropertyColumn<Webinar>(new ResourceModel("durationColumn"), "duration", "duration"));
        columns.add(new PropertyColumn<Webinar>(new ResourceModel("deadlineDateColumn"),
                "registrationDeadline", "registrationDeadline"));
        columns.add(new PriceColumn(new ResourceModel("priceColumn"), "price", "price") {
            @Override
            protected String getZeroPrice() {
                return getString("freePrice");
            }
        });
        AbstractColumn<Webinar> statusColumn = getStatusColumn();
        if (statusColumn != null) {
            columns.add(statusColumn);
        }
        columns.add(getActionColumn());
        return columns;
    }

    protected abstract AbstractColumn<Webinar> getActionColumn();

    protected abstract AbstractColumn<Webinar> getStatusColumn();

    protected void onAddManyUsers(IModel<Webinar> webinarIModel, AjaxRequestTarget target) {
        this.webinarIModel.setObject(webinarIModel.getObject());
        webinarAddUsersPanelDialog.getDialogPanel().initWebinarData(webinarIModel.getObject());
        webinarAddUsersPanelDialog.show(target);
    }
}
