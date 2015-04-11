package com.eltiland.ui.course.control.users;

import com.eltiland.bl.*;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.control.listeners.panel.NamePanel;
import com.eltiland.ui.course.control.users.panel.FilePanel;
import com.eltiland.ui.course.control.users.panel.GeneralDataPanel;
import com.eltiland.ui.course.control.users.panel.MessagePanel;
import com.eltiland.ui.course.control.users.panel.OrganizationPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Invoice management panel for training courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseInvoicePanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseInvoicePanel.class);

    @SpringBean
    private CourseSessionManager courseSessionManager;
    @SpringBean
    private CourseListenerManager courseListenerManager;
    @SpringBean
    private CourseDocumentManager courseDocumentManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private FileUtility fileUtility;

    private ELTTable<CourseListener> grid;

    private Dialog<OfferPanel> offerPanelDialog = new Dialog<OfferPanel>("offerDialog", 325) {
        @Override
        public OfferPanel createDialogPanel(String id) {
            return new OfferPanel(id);
        }

        @Override
        public void registerCallback(OfferPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseListener>() {
                @Override
                public void process(IModel<CourseListener> model, AjaxRequestTarget target) {
                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save listener", e);
                        throw new WicketRuntimeException("Cannot save listener", e);
                    }
                    close(target);
                }
            });
        }
    };

    private Dialog<ReqPanel> reqPanelDialog = new Dialog<ReqPanel>("reqDialog", 325) {
        @Override
        public ReqPanel createDialogPanel(String id) {
            return new ReqPanel(id);
        }

        @Override
        public void registerCallback(ReqPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseListener>() {
                @Override
                public void process(IModel<CourseListener> model, AjaxRequestTarget target) {
                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save listener", e);
                        throw new WicketRuntimeException("Cannot save listener", e);
                    }
                    close(target);
                }
            });
        }
    };

    private Dialog<FilePanel> filePanelDialog = new Dialog<FilePanel>("fileDialog", 300) {
        @Override
        public FilePanel createDialogPanel(String id) {
            return new FilePanel(id);
        }

        @Override
        public void registerCallback(FilePanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseListener>() {
                @Override
                public void process(IModel<CourseListener> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save listener", e);
                        throw new WicketRuntimeException("Cannot save listener", e);
                    }
                    ELTAlerts.renderOKPopup(getString("uploadMessage"), target);
                }
            });
        }
    };

    private Dialog<MessagePanel> messagePanelDialog = new Dialog<MessagePanel>("sendDialog", 325) {
        @Override
        public MessagePanel createDialogPanel(String id) {
            return new MessagePanel(id);
        }

        @Override
        public void registerCallback(MessagePanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    CourseSession session = courseSessionManager.getActiveSession(getModelObject());
                    try {
                        emailMessageManager.sendCourseListenerMessage(session, model.getObject(), false);
                    } catch (EmailException e) {
                        LOGGER.error("Cannot send mail", e);
                        throw new WicketRuntimeException("Cannot send mail", e);
                    }
                    close(target);
                    ELTAlerts.renderOKPopup(getString("sendMessage"), target);
                }
            });
        }
    };

    private ELTSelectDialog<User> userSelectDialog = new ELTSelectDialog<User>(
            "selectUserDialog", 900) {
        @Override
        protected int getMaxRows() {
            return 20;
        }

        @Override
        protected String getHeader() {
            return CourseInvoicePanel.this.getString("selectUser");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            for (Long id : selectedIds) {
                User user = genericManager.getObject(User.class, id);
                if (user != null) {
                    CourseListener listener = courseListenerManager.getListener(getModelObject(), user);
                    if (listener != null) {
                        ELTAlerts.renderErrorPopup(String.format(getString("error.exists"), user.getName()), target);
                        return;
                    } else {
                        CourseListener newListener = new CourseListener();
                        newListener.setStatus(CourseListener.Status.NEW);
                        newListener.setKind(CourseListener.Kind.PHYSICAL);
                        newListener.setListener(user);
                        newListener.setSession(courseSessionManager.getActiveSession(getModelObject()));
                        try {
                            genericManager.saveNew(newListener);
                            LOGGER.info(String.format("Saved listener with id %d", newListener.getId()));
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot save user", e);
                            throw new WicketRuntimeException("Cannot save user", e);
                        }
                    }
                }
            }
            ELTAlerts.renderOKPopup(getString("addedMessage"), target);
            close(target);
            target.add(grid);
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("nameColumn"), "name", "name"));
            columns.add(new PropertyColumn<User>(new ResourceModel("emailColumn"), "email", "email"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return userManager.getUserSearchList(first, count,
                    getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return userManager.getUserSearchCount(getSearchString());
        }

        @Override
        protected String getSearchPlaceholder() {
            return CourseInvoicePanel.this.getString("searchUser");
        }
    };


    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public CourseInvoicePanel(String id, IModel<Course> courseIModel) {
        super(id, courseIModel);

        final IModel<CourseSession> sessionIModel = new LoadableDetachableModel<CourseSession>() {
            @Override
            protected CourseSession load() {
                return courseSessionManager.getActiveSession(getModelObject());
            }
        };

        add(offerPanelDialog);
        add(reqPanelDialog);
        add(filePanelDialog);
        add(messagePanelDialog);
        add(userSelectDialog);

        grid = new ELTTable<CourseListener>("grid", 10) {
            @Override
            protected List<IColumn<CourseListener>> getColumns() {
                List<IColumn<CourseListener>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<CourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "listener.name") {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> components,
                                             String s, IModel<CourseListener> listenerIModel) {
                        genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getListener());
                        components.add(new NamePanel(s, new Model<>(listenerIModel.getObject().getListener())));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("contactColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new GeneralDataPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("organizationColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new OrganizationPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("typeColumn"), "kind") {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new TypePanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("statusColumn"), "status") {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new Label(componentId, getString(rowModel.getObject().getStatus().toString())));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseListenerManager.getListeners(sessionIModel.getObject(), getSearchString(),
                        first, count, getSort().getProperty(), getSort().isAscending(), false).iterator();
            }

            @Override
            protected int getSize() {
                return courseListenerManager.getListenersCount(sessionIModel.getObject(), getSearchString(), false);
            }

            @Override
            protected void onClick(IModel<CourseListener> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case APPLY:
                        rowModel.getObject().setStatus(CourseListener.Status.APPROVED);
//                        try {
//                            emailMessageManager.sendTCUserAccepted(rowModel.getObject());
//                        } catch (EmailException e) {
//                            LOGGER.error("Cannot send mail", e);
//                            throw new WicketRuntimeException("Cannot send mail", e);
//                        }
                        try {
                            genericManager.update(rowModel.getObject());
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update invoice", e);
                            throw new WicketRuntimeException("Cannot update invoice", e);
                        }
                        ELTAlerts.renderOKPopup(getString("approvedMessage"), target);
                        target.add(grid);
                        break;
                    case DOWNLOAD:
                        break;
                    case REMOVE:
//                        try {
//                            emailMessageManager.sendTCUserDeclined(rowModel.getObject());
//                        } catch (EmailException e) {
//                            LOGGER.error("Cannot send mail", e);
//                            throw new WicketRuntimeException("Cannot send mail", e);
//                        }
//                        try {
//                            genericManager.delete(rowModel.getObject());
//                        } catch (EltilandManagerException e) {
//                            LOGGER.error("Cannot remove invoice", e);
//                            throw new WicketRuntimeException("Cannot remove invoice", e);
//                        }
//                        ELTAlerts.renderOKPopup(getString("deleteMessage"), target);
//                        target.add(grid);
//                        break;
                    case PAY:
//                        try {
//                            emailMessageManager.sendTCUserPayAccepted(rowModel.getObject());
//                        } catch (EmailException e) {
//                            LOGGER.error("Cannot send mail", e);
//                            throw new WicketRuntimeException("Cannot send mail", e);
//                        }
//                        try {
//                            rowModel.getObject().setStatus(CourseListener.Status.PAYS);
//                            genericManager.update(rowModel.getObject());
//                        } catch (ConstraintException e) {
//                            LOGGER.error("Cannot update invoice", e);
//                            throw new WicketRuntimeException("Cannot update invoice", e);
//                        }
//                        ELTAlerts.renderOKPopup(getString("payMessage"), target);
//                        target.add(grid);
//                        break;
                    case FULL_APPLY:
//                        try {
//                            emailMessageManager.sendTCUserPaid(rowModel.getObject());
//                        } catch (EmailException e) {
//                            LOGGER.error("Cannot send mail", e);
//                            throw new WicketRuntimeException("Cannot send mail", e);
//                        }
                        try {
                            rowModel.getObject().setStatus(CourseListener.Status.CONFIRMED);
                            genericManager.update(rowModel.getObject());
                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getUsers());
                            for (User user : rowModel.getObject().getUsers()) {
                                CourseListener listener = new CourseListener();
                                listener.setStatus(CourseListener.Status.CONFIRMED);
                                listener.setKind(CourseListener.Kind.PHYSICAL);
                                listener.setSession(courseSessionManager.getActiveSession(getModelObject()));
                                listener.setListener(user);
                                genericManager.saveNew(listener);
                            }
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update invoice", e);
                            throw new WicketRuntimeException("Cannot update invoice", e);
                        }
                        ELTAlerts.renderOKPopup(getString("acceptMessage"), target);
                        target.add(grid);
                        break;
                    case EDIT:
                        offerPanelDialog.getDialogPanel().initData(rowModel.getObject());
                        offerPanelDialog.show(target);
                        break;
                    case EDIT_PAYMENT:
                        reqPanelDialog.getDialogPanel().initData(rowModel.getObject());
                        reqPanelDialog.show(target);
                        break;
                    case UPLOAD:
                        filePanelDialog.getDialogPanel().initData(rowModel);
                        filePanelDialog.show(target);
                        break;
                    case SEND:
                        messagePanelDialog.show(target);
                        break;
                    case ADD:
                        userSelectDialog.show(target);
                        break;
                    case ON:
                        sessionIModel.getObject().setOpen(true);
                        try {
                            genericManager.update(sessionIModel.getObject());
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update session", e);
                            throw new WicketRuntimeException("Cannot update session", e);
                        }
                        grid.updateControlPanel(target);
                        ELTAlerts.renderOKPopup(getString("onMessage"), target);
                        break;
                    case OFF:
                        sessionIModel.getObject().setOpen(false);
                        try {
                            genericManager.update(sessionIModel.getObject());
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update session", e);
                            throw new WicketRuntimeException("Cannot update session", e);
                        }
                        grid.updateControlPanel(target);
                        ELTAlerts.renderOKPopup(getString("offMessage"), target);
                        break;
                }
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected boolean isControlling() {
                return getSize() != 0;
            }

            @Override
            protected List<GridAction> getControlActions() {
                List<GridAction> actions = new ArrayList<>();
                if (sessionIModel.getObject().isOpen()) {
                    actions.add(GridAction.OFF);
                } else {
                    actions.add(GridAction.ON);
                }
                actions.add(GridAction.SEND);
                actions.add(GridAction.ADD);
                return actions;
            }

            @Override
            protected List<GridAction> getGridActions(IModel<CourseListener> rowModel) {
                List<GridAction> actions = new ArrayList<>();

                CourseListener.Status status = rowModel.getObject().getStatus();
                if (status.equals(CourseListener.Status.NEW)) {
                    actions.add(GridAction.APPLY);
                }

                if (!status.equals(CourseListener.Status.NEW)) {
                    actions.add(GridAction.UPLOAD);
                }

                if (rowModel.getObject().getKind().equals(CourseListener.Kind.MOSCOW)) {
                    actions.add(GridAction.EDIT);
                }

                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getDocument());
                if (!(status.equals(CourseListener.Status.NEW)) && (rowModel.getObject().getDocument() != null)) {
                    actions.add(GridAction.DOWNLOAD);
                }

                if (status.equals(CourseListener.Status.APPROVED)) {
                    actions.add(GridAction.PAY);
                    actions.add(GridAction.EDIT_PAYMENT);
                }

                if (status.equals(CourseListener.Status.PAYS) || status.equals(CourseListener.Status.CONFIRMED)) {
                    actions.add(GridAction.FULL_APPLY);
                }

                actions.add(GridAction.REMOVE);

                return actions;
            }

            @Override
            protected String getNotFoundedMessage() {
                return getString("notFounded");
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.APPLY)) {
                    return getString("applyTooltip");
                } else if (action.equals(GridAction.REMOVE)) {
                    return getString("removeTooltip");
                } else if (action.equals(GridAction.DOWNLOAD)) {
                    return getString("downloadTooltip");
                } else if (action.equals(GridAction.PAY)) {
                    return getString("payTooltip");
                } else if (action.equals(GridAction.FULL_APPLY)) {
                    return getString("fullTooltip");
                } else if (action.equals(GridAction.EDIT)) {
                    return getString("editTooltip");
                } else if (action.equals(GridAction.EDIT_PAYMENT)) {
                    return getString("editPaymentTooltip");
                } else if (action.equals(GridAction.UPLOAD)) {
                    return getString("uploadTooltip");
                } else if (action.equals(GridAction.SEND)) {
                    return getString("sendTooltip");
                } else if (action.equals(GridAction.ADD)) {
                    return getString("addTooltip");
                } else if (action.equals(GridAction.ON)) {
                    return getString("onTooltip");
                } else if (action.equals(GridAction.OFF)) {
                    return getString("offTooltip");
                } else {
                    return StringUtils.EMPTY;
                }
            }

            @Override
            protected String getFileName(IModel<CourseListener> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getDocument());
                return rowModel.getObject().getDocument().getName();
            }

            @Override
            protected InputStream getInputStream(IModel<CourseListener> rowModel)
                    throws ResourceStreamNotFoundException {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getDocument());
                genericManager.initialize(rowModel.getObject().getDocument(),
                        rowModel.getObject().getDocument().getBody());
                IResourceStream resourceStream = fileUtility.getFileResource(
                        rowModel.getObject().getDocument().getBody().getHash());
                return resourceStream.getInputStream();
            }
        };

        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class TypePanel extends BaseEltilandPanel<CourseListener> {
        private Dialog<ListenersListPanel> listenersListPanelDialog =
                new Dialog<ListenersListPanel>("listenersListDialog", 500) {
                    @Override
                    public ListenersListPanel createDialogPanel(String id) {
                        return new ListenersListPanel(id);
                    }
                };

        public TypePanel(String id, IModel<CourseListener> courseListenerIModel) {
            super(id, courseListenerIModel);
            add(new Label("type", getString(getModelObject().getKind().toString())));

            genericManager.initialize(getModelObject(), getModelObject().getUsers());
            final boolean hasListeners = !getModelObject().getUsers().isEmpty();
            final boolean isLegal = !(getModelObject().getKind().equals(CourseListener.Kind.PHYSICAL));
            add(new WebMarkupContainer("noListeners") {
                @Override
                public boolean isVisible() {
                    return !hasListeners && isLegal;
                }
            });
            add(new EltiAjaxLink("showUsersLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    listenersListPanelDialog.getDialogPanel().initData(TypePanel.this.getModelObject());
                    listenersListPanelDialog.show(target);
                }

                @Override
                public boolean isVisible() {
                    return hasListeners && isLegal;
                }
            });
            add(listenersListPanelDialog);
        }
    }

    private class ListenersListPanel extends ELTDialogPanel {

        private IModel<CourseListener> courseSessionIModel = new GenericDBModel<>(CourseListener.class);

        public ListenersListPanel(String id) {
            super(id);
            form.add(new ELTTable<User>("grid", 15) {
                @Override
                protected List<IColumn<User>> getColumns() {
                    List<IColumn<User>> columns = new ArrayList<>();
                    columns.add(new PropertyColumn<User>(
                            new ResourceModel("nameColumn"), "name", "name"));
                    columns.add(new AbstractColumn<User>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                        @Override
                        public void populateItem(
                                Item<ICellPopulator<User>> components, String s, IModel<User> userIModel) {
                            Label label = new Label(s, new Model<String>());
                            if (userIModel.getObject().getConfirmationDate() == null) {
                                label.setDefaultModelObject(getString("no_activated"));
                                label.add(new AttributeModifier("style", "color:red"));
                            } else {
                                label.setDefaultModelObject(getString("activated"));
                                label.add(new AttributeModifier("style", "color:green"));
                            }
                            components.add(label);
                        }
                    });
                    return columns;
                }

                @Override
                protected Iterator getIterator(int first, int count) {
                    return userManager.getInvitedUsers(courseSessionIModel.getObject(),
                            first, count, getSort().getProperty(), getSort().isAscending()).iterator();
                }

                @Override
                protected int getSize() {
                    return userManager.getInvitedUsersCount(courseSessionIModel.getObject());
                }

                @Override
                protected void onClick(IModel<User> rowModel, GridAction action, AjaxRequestTarget target) {
                }
            });
        }

        public void initData(CourseListener session) {
            courseSessionIModel.setObject(session);
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>();
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        }
    }

    private class OfferPanel extends ELTDialogPanel implements IDialogUpdateCallback<CourseListener> {

        private IDialogActionProcessor<CourseListener> callback;

        private IModel<CourseListener> listenerIModel = new GenericDBModel<>(CourseListener.class);

        private ELTTextField<String> offerField = new ELTTextField<>(
                "offer", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class);

        public OfferPanel(String id) {
            super(id);
            form.add(offerField);
        }

        public void initData(CourseListener data) {
            listenerIModel.setObject(data);
            offerField.setModelObject(data.getOffer());
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                listenerIModel.getObject().setOffer(offerField.getModelObject());
                callback.process(listenerIModel, target);
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<CourseListener> callback) {
            this.callback = callback;
        }
    }

    private class ReqPanel extends ELTDialogPanel implements IDialogUpdateCallback<CourseListener> {

        private IDialogActionProcessor<CourseListener> callback;

        private IModel<CourseListener> listenerIModel = new GenericDBModel<>(CourseListener.class);

        private ELTTextArea reqField = new ELTTextArea("req", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }

            @Override
            protected int getInitialHeight() {
                return 350;
            }
        };

        public ReqPanel(String id) {
            super(id);
            form.add(reqField);
        }

        public void initData(CourseListener data) {
            listenerIModel.setObject(data);
            genericManager.initialize(data, data.getSession());

            String req = listenerIModel.getObject().getRequisites();
            if (req == null || req.isEmpty()) {
                genericManager.initialize(data, data.getSession());
                req = courseDocumentManager.getDocumentForSession(data.getSession()).getRequisites();
            }
            reqField.setModelObject(req);
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                listenerIModel.getObject().setRequisites(reqField.getModelObject());
                callback.process(listenerIModel, target);
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<CourseListener> callback) {
            this.callback = callback;
        }
    }
}
