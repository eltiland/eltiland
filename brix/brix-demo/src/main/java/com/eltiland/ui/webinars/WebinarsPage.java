package com.eltiland.ui.webinars;

import com.eltiland.BrixPanel;
import com.eltiland.bl.*;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.*;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.WebinarLoginPanel;
import com.eltiland.ui.webinars.components.WebinarNewUserPanel;
import com.eltiland.ui.webinars.components.multiply.WebinarAddUsersPanel;
import com.eltiland.ui.webinars.plugin.tab.subscribe.components.WebinarListPanel;
import com.eltiland.ui.worktop.BaseWorktopPage;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Page with webinar's announcements. Used for registering on webinars.
 *
 * @author Aleksey PLotnikov
 */
public class WebinarsPage extends BaseEltilandPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarsPage.class);

    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarSubscriptionManager webinarSubscriptionManager;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private Dialog<WebinarLoginPanel> loginPanelDialog = new Dialog<WebinarLoginPanel>("loginDialog", 340) {
        @Override
        public WebinarLoginPanel createDialogPanel(String id) {
            return new WebinarLoginPanel(id);
        }
    };

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
                                    if (webinarIModel.getObject().getPrice() == null) {
                                        totalPrice = totalPrice.add(BigDecimal.ZERO);
                                    } else {
                                        totalPrice = totalPrice.add(webinarIModel.getObject().getPrice());
                                    }
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

    private Dialog<WebinarNewUserPanel> webinarNewUserPanelDialog
            = new Dialog<WebinarNewUserPanel>("addUserDialog", 330) {
        @Override
        public WebinarNewUserPanel createDialogPanel(String id) {
            return new WebinarNewUserPanel(id) {
                @Override
                public void onLogin(AjaxRequestTarget target, IModel<Webinar> webinarIModel) {
                    close(target);
                    loginPanelDialog.getDialogPanel().initWebinarData(webinarIModel);
                    loginPanelDialog.show(target);
                }
            };
        }

        @Override
        public void registerCallback(WebinarNewUserPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<WebinarUserPayment>() {
                @Override
                public void process(IModel<WebinarUserPayment> model, AjaxRequestTarget target) {
                    try {
                        webinarUserPaymentManager.createUser(model.getObject());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create webinar user", e);
                        throw new WicketRuntimeException("Cannot create webinar user", e);
                    } catch (EmailException e) {
                        LOGGER.error("Cannot send mail to user", e);
                        throw new WicketRuntimeException("Cannot send mail to user", e);
                    } catch (WebinarException e) {
                        e.printStackTrace();
                    }

                    close(target);

                    boolean isFree = model.getObject().getPrice() == null
                            || model.getObject().getPrice().equals(BigDecimal.valueOf(0));

                    ELTAlerts.renderOKPopup(getString(isFree ? "signupFreeMessage" : "signupPaidMessage"), target);
                }
            });
        }
    };

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private GenericManager genericManager;

    public static final String MOUNT_PATH = "/webinars";

    ELTTable availableTable = new ELTTable<Webinar>("grid", 10) {
        @Override
        protected String getActionTooltip(GridAction action) {
            switch (action) {
                case APPLY:
                    return getString("registerTooltip");
                case USERS:
                    return getString("inviteUsers");
                default:
                    return "";
            }
        }

        protected void onAddManyUsers(IModel<Webinar> webinarIModel_new, AjaxRequestTarget target) {
            webinarIModel.setObject(webinarIModel_new.getObject());
            webinarAddUsersPanelDialog.getDialogPanel().initWebinarData(webinarIModel_new.getObject());
            webinarAddUsersPanelDialog.show(target);
        }

        @Override
        public void onClick(IModel<Webinar> rowModel, GridAction action, AjaxRequestTarget target) {
            switch (action) {
                case APPLY:
                    webinarNewUserPanelDialog.getDialogPanel().initWebinarData(rowModel.getObject());
                    webinarNewUserPanelDialog.show(target);
                    break;
                case USERS:
                    if (currentUserModel.getObject() == null) {
                        ELTAlerts.renderErrorPopup(getString("loginNeededMessage"), target);
                    } else {
                        onAddManyUsers(rowModel, target);
                    }
                    break;
            }
        }

        @Override
        public Iterator getIterator(int first, int count) {
            return webinarManager.getWebinarAvailableList(
                    first, count, getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return webinarManager.getWebinarAvailableCount();
        }

        @Override
        protected boolean isActionVisible(GridAction action, IModel<Webinar> rowModel) {
            if (action.equals(GridAction.USERS)) {
                BigDecimal price = rowModel.getObject().getPrice();
                return !(price == null || price.equals(BigDecimal.ZERO));
            } else {
                return true;
            }
        }

        @Override
        protected List<GridAction> getGridActions(IModel<Webinar> rowModel) {
            return new ArrayList<>(Arrays.asList(GridAction.APPLY, GridAction.USERS));
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
            columns.add(new AbstractColumn<Webinar>(new ResourceModel("durationColumn")) {
                @Override
                public void populateItem(
                        Item<ICellPopulator<Webinar>> cellItem, String componentId, IModel<Webinar> rowModel) {
                    String duration = Integer.toString(rowModel.getObject().getDuration());
                    cellItem.add(new Label(componentId, duration + " минут"));
                }
            });
            columns.add(new PropertyColumn<Webinar>(new ResourceModel("deadlineDateColumn"),
                    "registrationDeadline", "registrationDeadline"));
            columns.add(new PriceColumn(new ResourceModel("priceWebinar"), "price", "price") {
                @Override
                protected String getZeroPrice() {
                    return getString("freePrice");
                }
            });
            return columns;
        }
    };

    ELTTable recordsTable = new ELTTable<WebinarRecord>("recordGrid", 10) {
        @SpringBean
        private GenericManager genericManager;
        @SpringBean
        private WebinarRecordManager webinarRecordManager;
        @SpringBean
        private WebinarRecordPaymentManager webinarRecordPaymentManager;
        @SpringBean
        private WebinarCertificateGenerator webinarCertificateGenerator;

        @Override
        protected String getActionTooltip(GridAction action) {
            switch (action) {
                case APPLY:
                    return getString("subscribeRecord");
                default:
                    return "";
            }
        }

        @Override
        protected List<GridAction> getGridActions(IModel<WebinarRecord> rowModel) {
            return new ArrayList<>(Arrays.asList(GridAction.APPLY));
        }

        public Iterator getIterator(int first, int count) {
            return webinarRecordManager.getList(
                    first, count, getSort().getProperty(), getSort().isAscending(), false).iterator();
        }

        public int getSize() {
            return webinarRecordManager.getCount(false);
        }

        public List<IColumn<WebinarRecord>> getColumns() {
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
            return columns;
        }

        public void onClick(IModel<WebinarRecord> rowModel, GridAction action, AjaxRequestTarget target) {
            switch (action) {
                case APPLY:
                    if (currentUserModel.getObject() == null) {
                        ELTAlerts.renderErrorPopup(getString("loginNeededMessage"), target);
                    } else {
                        if (webinarRecordPaymentManager.hasRecordInvoicesForCurrentUser(rowModel.getObject())) {
                            ELTAlerts.renderWarningPopup(getString("alreadySendedError"), target);
                        } else {
                            WebinarRecordPayment payment = new WebinarRecordPayment();

                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getWebinar());

                            payment.setPrice(rowModel.getObject().getPrice());
                            payment.setRecord(rowModel.getObject());
                            payment.setDate(rowModel.getObject().getWebinar().getStartDate());
                            payment.setStatus(PaidStatus.NEW);

                            boolean isFree = (rowModel.getObject().getPrice().longValue() == 0);
                            if (!isFree) {
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
                                if (!isFree) {
                                    emailMessageManager.sendRecordInvitationToUser(payment);
                                } else {
                                    InputStream pdfStream =
                                            webinarCertificateGenerator.generateRecordCertificate(payment);
                                    emailMessageManager.sendRecordLinkToUser(payment, pdfStream);
                                }

                            } catch (EmailException | EltilandManagerException e) {
                                LOGGER.error("Can't send mail", e);
                                throw new WicketRuntimeException("Can't send mail", e);
                            }

                            ELTAlerts.renderOKPopup(getString("sendedMessage"), target);
                        }
                    }
            }
        }
    };

    private ELTTable<WebinarSubscription> subscriptionGrid = new ELTTable<WebinarSubscription>("subGrid", 10) {
        @Override
        protected List<IColumn<WebinarSubscription>> getColumns() {
            ArrayList<IColumn<WebinarSubscription>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<WebinarSubscription>(new ResourceModel("nameColumn"), "name", "name"));
            columns.add(new PropertyColumn<WebinarSubscription>(new ResourceModel("descriptionColumn"), "info", "info"));
            columns.add(new AbstractColumn<WebinarSubscription>(new ResourceModel("webinarsColumn")) {
                @Override
                public void populateItem(
                        Item<ICellPopulator<WebinarSubscription>> item, String s, IModel<WebinarSubscription> iModel) {
                    item.add(new WebinarListPanel(s, iModel));
                }
            });
            columns.add(new PriceColumn(new ResourceModel("priceWebinar"), "price", "price"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return webinarSubscriptionManager.getList(
                    first, count, getSort().getProperty(), getSort().isAscending(), true).iterator();
        }

        @Override
        protected int getSize() {
            return webinarSubscriptionManager.getCount(true);
        }

        @Override
        protected void onClick(IModel<WebinarSubscription> rowModel, GridAction action, AjaxRequestTarget target) {

        }
    };

    WebMarkupContainer noWebinarsContainer = new WebMarkupContainer("noWebinarMessage") {
        @Override
        public boolean isVisible() {
            return webinarManager.getWebinarAvailableCount() == 0 && webinarManager.getUserWebinarCount() == 0;
        }
    };

    WebMarkupContainer availableWebinarsContainer = new WebMarkupContainer("availableWebinarsContainer") {
        @Override
        public boolean isVisible() {
            return webinarManager.getWebinarAvailableCount() != 0;
        }
    };

    WebMarkupContainer recordsContainer = new WebMarkupContainer("recordContainer") {
        @Override
        public boolean isVisible() {
            return genericManager.getEntityCount(WebinarRecord.class, null, null) > 0;
        }
    };

    WebMarkupContainer subscriptionContainer = new WebMarkupContainer("subscriptionContainer") {
        @Override
        public boolean isVisible() {
            return webinarSubscriptionManager.getCount(true) > 0;
        }
    };

    public WebinarsPage() {
        add(noWebinarsContainer);
        add(new EltiAjaxLink("myWebinarsButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(BaseWorktopPage.class,
                        new PageParameters().add(BaseWorktopPage.PARAM_ID, "4"));
            }

            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser != null;
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });

        add(availableWebinarsContainer.setOutputMarkupPlaceholderTag(true));
        add(recordsContainer.setOutputMarkupPlaceholderTag(true));
        add(subscriptionContainer.setOutputMarkupPlaceholderTag(true));

        availableWebinarsContainer.add(availableTable);
        subscriptionContainer.add(subscriptionGrid.setOutputMarkupPlaceholderTag(true));

        recordsContainer.add(recordsTable);
        recordsContainer.add(new BrixPanel("recordInfo", UrlUtils.createBrixPathForPanel("WEBINAR/recordInfo.html")));
        add(new BrixPanel("webinarInfo", UrlUtils.createBrixPathForPanel("WEBINAR/webinarInfo.html")));
        add(webinarNewUserPanelDialog);
        add(loginPanelDialog);
        add(webinarAddUsersPanelDialog);
    }

    private void updateGrids(AjaxRequestTarget target) {
        target.add(availableWebinarsContainer);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

}
