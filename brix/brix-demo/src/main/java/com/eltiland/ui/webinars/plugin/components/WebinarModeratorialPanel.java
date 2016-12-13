package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.interval.ELTIntervalDialog;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.components.multiply.WebinarAddUsersPanel;
import com.eltiland.ui.webinars.plugin.components.column.WebinarFullNameColumn;
import com.eltiland.ui.webinars.plugin.components.column.WebinarPriceColumn;
import com.eltiland.ui.webinars.plugin.components.column.WebinarRoleColumn;
import com.eltiland.utils.DateUtils;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Webinar moderation panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarModeratorialPanel extends BaseEltilandPanel<Webinar> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarModeratorialPanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarServiceManager webinarServiceManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private IModel<String> confirmedCountModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            if (webinarIModel.getObject() != null) {
                return String.format(getString("confirmedCount"),
                        webinarUserPaymentManager.getWebinarConfirmedUserCount(webinarIModel.getObject()));
            } else {
                return "";
            }
        }
    };

    private Label headerLabel = new Label("header", "");
    private Label confirmedCountLabel = new Label("confirmedCount", confirmedCountModel);

    private ELTTextField<String> searchField =
            new ELTTextField<>("searchField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class);

    private ELTTextField<String> inviteField =
            new ELTTextField<>("inviteField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class);

    private EltiAjaxSubmitLink searchButton = new EltiAjaxSubmitLink("searchButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            target.add(grid);
        }
    };

    private EltiDefaultDataGrid<WebinarUserPaymentDataSource, WebinarUserPayment> grid;

    private Dialog<WebinarCheckErrorPanel> webinarCheckErrorPanelDialog =
            new Dialog<WebinarCheckErrorPanel>("checkPanel", 370) {
                @Override
                public WebinarCheckErrorPanel createDialogPanel(String id) {
                    return new WebinarCheckErrorPanel(id) {
                        @Override
                        public void onAddUsers(AjaxRequestTarget target, List<WebinarUserPayment> payments) {
                            for (WebinarUserPayment payment : payments) {
                                try {
                                    genericManager.initialize(payment, payment.getWebinar());
                                    webinarServiceManager.addUser(payment);
                                    payment.setWebinarlink(webinarUserPaymentManager.getLink(payment));
                                    webinarUserPaymentManager.update(payment);
                                } catch (EltilandManagerException e) {
                                    LOGGER.error("Cannot add user to webinar", e);
                                    throw new WicketRuntimeException("Cannot add user to webinar", e);
                                } catch (WebinarException e) {
                                    e.printStackTrace();
                                }
                            }
                            close(target);
                            ELTAlerts.renderOKPopup(getString("addMessage"), target);
                        }
                    };
                }
            };

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
                                for (User user : model.getObject()) {

                                    // split name into parts
                                    String name = user.getName();
                                    String[] nameParts = name.split(" ");
                                    String surnameValue = nameParts[0],
                                            nameValue = nameParts[1],
                                            patronymicValue = nameParts[2];

                                    WebinarUserPayment payment = new WebinarUserPayment();
                                    payment.setRole(WebinarUserPayment.Role.MEMBER);
                                    payment.setWebinar(webinarIModel.getObject());
                                    payment.setPrice(webinarIModel.getObject().getPrice());
                                    payment.setRegistrationDate(DateUtils.getCurrentDate());
                                    payment.setUserName(nameValue);
                                    payment.setPatronymic(patronymicValue);
                                    payment.setUserSurname(surnameValue);
                                    payment.setUserProfile(user);
                                    payment.setUserEmail(user.getEmail());

                                    try {
                                        try {
                                            webinarUserPaymentManager.createUser(payment);
                                        } catch (WebinarException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (EltilandManagerException e) {
                                        LOGGER.error("Got exception when creating user", e);
                                        throw new WicketRuntimeException("Got exception when creating user", e);
                                    } catch (EmailException e) {
                                        LOGGER.error("Got exception when sending email", e);
                                        throw new WicketRuntimeException("Got exception when sending email", e);
                                    }

                                }
                                close(target);
                                ELTAlerts.renderOKPopup(getString("usersInvited"), target);
                            }
                        }
                    });
                }
            };

    private Dialog<AddUser> addUserDialog = new Dialog<AddUser>("addUserDialog", 400) {
        @Override
        public AddUser createDialogPanel(String id) {
            return new AddUser(id);
        }

        @Override
        public void registerCallback(AddUser panel) {
            super.registerCallback(panel);
            panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id panel markup id.
     */
    public WebinarModeratorialPanel(String id) {
        super(id);

        addComponents();
    }

    public void initWebinarData(IModel<Webinar> webinarIModel) {
        this.webinarIModel.setObject(webinarIModel.getObject());
        headerLabel.setDefaultModelObject(String.format(getString("header"), webinarIModel.getObject().getName()));
        confirmedCountModel.detach();
    }

    private void addComponents() {
        add(headerLabel);

        Form form = new Form("form");

        form.add(searchField);
        form.setMultiPart(true);
        searchField.addMaxLengthValidator(256);
        form.add(searchButton);
        form.add(inviteField);
        add(form);

        List<IGridColumn<WebinarUserPaymentDataSource, WebinarUserPayment>> columns = new ArrayList<>();
        columns.add(new WebinarFullNameColumn<WebinarUserPaymentDataSource>());
        columns.add(new PropertyWrapColumn(new ResourceModel("emailColumnTitle"), "userEmail", "userEmail"));
        columns.add(new WebinarPriceColumn<WebinarUserPaymentDataSource>(
                "priceColumn", new ResourceModel("priceColumnTitle"), "price") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(grid);
            }

            @Override
            public int getInitialSize() {
                return 130;
            }
        });
        columns.add(new AbstractColumn<WebinarUserPaymentDataSource, WebinarUserPayment>(
                "statusColumn", new ResourceModel("statusColumnTitle"), "status") {
            @Override
            public Component newCell(WebMarkupContainer parent,
                                     String componentId, IModel<WebinarUserPayment> rowModel) {
                boolean status = rowModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
                return new Label(componentId, new ResourceModel(status ? "CONFIRMED" : "PAYS"));
            }

            @Override
            public int getInitialSize() {
                return 100;
            }
        });
        columns.add(new AbstractColumn<WebinarUserPaymentDataSource, WebinarUserPayment>(
                "actionColumn", new ResourceModel("actionColumnTitle")) {
            @Override
            public Component newCell(WebMarkupContainer parent,
                                     String componentId, final IModel<WebinarUserPayment> rowModel) {
                return new WebinarModeratorialActionPanel(componentId, rowModel) {
                    @Override
                    public void onDeny(AjaxRequestTarget target) {
                        try {
                            webinarUserPaymentManager.removeUser(rowModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot remove user from webinar", e);
                            throw new WicketRuntimeException("Cannot remove user from webinar", e);
                        }

                        try {
                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getWebinar());
                            emailMessageManager.sendWebinarDenyToUser(rowModel.getObject());
                        } catch (EmailException e) {
                            LOGGER.error("Cannot send mail about user removing", e);
                            throw new WicketRuntimeException("Cannot send mail about user removing", e);
                        }

                        target.add(grid);
                        confirmedCountModel.detach();
                        target.add(confirmedCountLabel);
                    }
                };
            }

            @Override
            public int getInitialSize() {
                return 100;
            }
        });
        columns.add(new WebinarRoleColumn<WebinarUserPaymentDataSource>(
                "roleColumn", new ResourceModel("roleColumnTitle"), "role") {
            @Override
            public int getInitialSize() {
                return 108;
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(grid);
            }
        });
        columns.add(new AbstractColumn<WebinarUserPaymentDataSource, WebinarUserPayment>
                ("checkColumn", ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public Component newCell(WebMarkupContainer parent, String componentId,
                                     final IModel<WebinarUserPayment> rowModel) {
                return new WebinarLinkPanel(componentId, rowModel) {
                    @Override
                    protected boolean showLink() {
                        return rowModel.getObject().getStatus().equals(PaidStatus.CONFIRMED) &&
                                !(rowModel.getObject().getRole().equals(WebinarUserPayment.Role.MODERATOR));
                    }
                };
            }

            @Override
            public int getInitialSize() {
                return 100;
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new Model<>(new WebinarUserPaymentDataSource()), columns);
        grid.setRowsPerPage(10);
        add(grid.setOutputMarkupId(true));
        add(new Label("capacity",
                String.format(getString("capacity"), eltilandProps.getProperty("webinar.usercount"))));
        add(confirmedCountLabel.setOutputMarkupId(true));

        add(new EltiAjaxLink("checkButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    List<WebinarUserPayment> payments =
                            webinarUserPaymentManager.checkWebinarUsers(webinarIModel.getObject());
                    if (payments == null) {
                        ELTAlerts.renderOKPopup(getString("checkMessage"), target);
                    } else {
                        webinarCheckErrorPanelDialog.getDialogPanel().initUserList(payments);
                        webinarCheckErrorPanelDialog.show(target);
                    }
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot check users", e);
                    throw new WicketRuntimeException("Cannot check users", e);
                }
            }
        });

        form.add(new EltiAjaxSubmitLink("inviteButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                User user = userManager.getUserByEmail(inviteField.getModelObject());
                if( user != null ) {
                    // split name into parts
                    String name = user.getName();
                    String[] nameParts = name.split(" ");
                    String surnameValue = nameParts[0],
                            nameValue = nameParts[1],
                            patronymicValue = nameParts[2];


                    WebinarUserPayment payment = new WebinarUserPayment();
                    payment.setRole(WebinarUserPayment.Role.MEMBER);
                    payment.setWebinar(webinarIModel.getObject());
                    payment.setPrice(webinarIModel.getObject().getPrice());
                    payment.setRegistrationDate(DateUtils.getCurrentDate());
                    payment.setUserName(nameValue);
                    payment.setPatronymic(patronymicValue);
                    payment.setUserSurname(surnameValue);
                    payment.setUserProfile(user);
                    payment.setUserEmail(user.getEmail());

                    try {
                        webinarUserPaymentManager.createUser(payment);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Got exception when creating user", e);
                        throw new WicketRuntimeException("Got exception when creating user", e);
                    } catch (EmailException e) {
                        LOGGER.error("Got exception when sending email", e);
                        throw new WicketRuntimeException("Got exception when sending email", e);
                    } catch (WebinarException e) {
                        e.printStackTrace();
                    }
                    target.add(grid);
                }


          //      addUserDialog.show(target);
//                webinarAddUsersPanelDialog.getDialogPanel().initWebinarData(webinarIModel.getObject());
//                webinarAddUsersPanelDialog.show(target);
            }
        });

        add(webinarCheckErrorPanelDialog);
        add(webinarAddUsersPanelDialog);
        add(addUserDialog);
    }

    private class AddUser extends ELTDialogPanel implements IDialogCloseCallback {

        ELTTextEmailField emailField =
                new ELTTextEmailField("emailField", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), true);

        private IDialogActionProcessor callback;

        public AddUser(String id) {
            super(id);
            form.add(emailField);
        }

        @Override
        protected String getHeader() {
            return "Введите email пользователя";
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<EVENT>(Arrays.asList(EVENT.Register));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            User user = userManager.getUserByEmail(emailField.getModelObject());
            if( user != null ) {
                // split name into parts
                String name = user.getName();
                String[] nameParts = name.split(" ");
                String surnameValue = nameParts[0],
                        nameValue = nameParts[1],
                        patronymicValue = nameParts[2];


                WebinarUserPayment payment = new WebinarUserPayment();
                payment.setRole(WebinarUserPayment.Role.MEMBER);
                payment.setWebinar(webinarIModel.getObject());
                payment.setPrice(webinarIModel.getObject().getPrice());
                payment.setRegistrationDate(DateUtils.getCurrentDate());
                payment.setUserName(nameValue);
                payment.setPatronymic(patronymicValue);
                payment.setUserSurname(surnameValue);
                payment.setUserProfile(user);
                payment.setUserEmail(user.getEmail());

                try {
                    webinarUserPaymentManager.createUser(payment);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Got exception when creating user", e);
                    throw new WicketRuntimeException("Got exception when creating user", e);
                } catch (EmailException e) {
                    LOGGER.error("Got exception when sending email", e);
                    throw new WicketRuntimeException("Got exception when sending email", e);
                } catch (WebinarException e) {
                    e.printStackTrace();
                }
                callback.process(target);
            }

        }

        @Override
        public void setCloseCallback(IDialogActionProcessor callback) {
            this.callback = callback;
        }
    }

    private class WebinarUserPaymentDataSource implements IDataSource<WebinarUserPayment> {

        @Override
        public void query(IQuery query, IQueryResult<WebinarUserPayment> result) {
            String pattern = searchField.getModelObject();

            int count = 0;
            try {
                count = webinarUserPaymentManager.getWebinarUserCount(webinarIModel.getObject(), pattern);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get count of users", e);
                throw new WicketRuntimeException("Cannot get count of users", e);
            }
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<WebinarUserPayment>emptyIterator());
            }

            String sortProperty = "registrationDate";
            boolean isAscending = false;

            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            try {
                result.setItems(webinarUserPaymentManager.getWebinarUserList(
                        webinarIModel.getObject(),
                        query.getFrom(),
                        query.getCount(),
                        sortProperty,
                        isAscending,
                        pattern
                ).iterator());
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get list of users", e);
                throw new WicketRuntimeException("Cannot get list of users", e);
            }
        }

        @Override
        public IModel<WebinarUserPayment> model(WebinarUserPayment object) {
            return new GenericDBModel<>(WebinarUserPayment.class, object);
        }

        @Override
        public void detach() {
        }
    }
}
