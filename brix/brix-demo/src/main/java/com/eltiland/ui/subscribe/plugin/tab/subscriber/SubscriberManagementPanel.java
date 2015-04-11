package com.eltiland.ui.subscribe.plugin.tab.subscriber;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.SubscriberManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.datagrid.DefaultDataGrid;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Subscriber managements panel.
 */
public class SubscriberManagementPanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SubscriberManagementPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private SubscriberManager subscriberManager;
    @SpringBean
    private UserManager userManager;

    private ELTTable<Subscriber> grid;

    private IModel<List<User>> notSubscribersModel = new LoadableDetachableModel<List<User>>() {
        @Override
        protected List<User> load() {
            return userManager.getUsersNotSubscribers();
        }
    };

    private Dialog<AddSubscriberPanel> addSubscriberPanelDialog = new Dialog<AddSubscriberPanel>(
            "addSubscriberDialog", 370) {
        @Override
        public AddSubscriberPanel createDialogPanel(String id) {
            return new AddSubscriberPanel(id);
        }

        @Override
        public void registerCallback(AddSubscriberPanel panel) {
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Subscriber>() {
                @Override
                public void process(IModel<Subscriber> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("addMessage"), target);
                }
            });
            super.registerCallback(panel);
        }
    };

    private Dialog<CheckResultsDialog> checkResultsDialog =
            new Dialog<CheckResultsDialog>("checkResultsDialog", 300) {
                @Override
                public CheckResultsDialog createDialogPanel(String id) {
                    return new CheckResultsDialog(id);
                }

                @Override
                public void registerCallback(CheckResultsDialog panel) {
                    panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            close(target);
                        }
                    });
                    panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            for (User user : notSubscribersModel.getObject()) {
                                Subscriber subscriber = new Subscriber();
                                subscriber.setEmail(user.getEmail());
                                try {
                                    subscriberManager.createSubscriber(subscriber);
                                } catch (SubscriberException e) {
                                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                    return;
                                }
                            }

                            close(target);
                            ELTAlerts.renderOKPopup(getString("usersSubscribed"), target);
                        }
                    });
                    super.registerCallback(panel);
                }
            };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public SubscriberManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<Subscriber>("grid", 10) {
            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.PROFILE, GridAction.USERS);
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Subscriber> rowModel) {
                switch (action) {
                    case ON:
                        return !rowModel.getObject().isDisabled();
                    case OFF:
                        return rowModel.getObject().isDisabled();
                    default:
                        return true;
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Subscriber> rowModel) {
                return Arrays.asList(GridAction.ON, GridAction.OFF, GridAction.REMOVE);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ON:
                        return getString("activateAction");
                    case OFF:
                        return getString("disactivateAction");
                    case REMOVE:
                        return getString("deleteAction");
                    case PROFILE:
                        return getString("addSubscriber");
                    case USERS:
                        return getString("checkUsers");
                    default:
                        return "";
                }
            }

            @Override
            protected List<IColumn<Subscriber>> getColumns() {
                List<IColumn<Subscriber>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Subscriber>(new ResourceModel("dateColumn"), "creationDate",
                        "creationDate"));
                columns.add(new PropertyColumn<Subscriber>(new ResourceModel("emailColumn"), "email", "email"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return subscriberManager.getActiveSubscriberList(first, count, getSort().getProperty(),
                        getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return subscriberManager.getActiveSubscriberCount();
            }

            @Override
            protected void onClick(IModel<Subscriber> rowModel, GridAction action, AjaxRequestTarget target) {
                Subscriber subscriber;
                switch (action) {
                    case ON:
                        subscriber = rowModel.getObject();
                        subscriber.setDisabled(true);
                        try {
                            subscriberManager.updateSubscriber(subscriber);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);

                        break;

                    case OFF:
                        subscriber = rowModel.getObject();
                        subscriber.setDisabled(false);
                        try {
                            subscriberManager.updateSubscriber(subscriber);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);

                        break;

                    case REMOVE:
                        try {
                            subscriberManager.deleteSubscriber(rowModel.getObject());
                            ELTAlerts.renderOKPopup(getString("deleteMessage"), target);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);

                        break;

                    case PROFILE:
                        addSubscriberPanelDialog.show(target);

                        break;

                    case USERS:
                        notSubscribersModel.detach();
                        if (!(notSubscribersModel.getObject().isEmpty())) {
                            checkResultsDialog.show(target);
                        } else {
                            ELTAlerts.renderOKPopup(getString("allUsersSubscribed"), target);
                        }

                        break;
                }
            }
        };
        add(grid.setOutputMarkupId(true));

        add(addSubscriberPanelDialog);
        add(checkResultsDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class AddSubscriberPanel extends ELTDialogPanel implements IDialogNewCallback<Subscriber> {

        private IDialogActionProcessor<Subscriber> newCallback;

        private ELTTextEmailField emailField = new ELTTextEmailField(
                "emailField", new ResourceModel("emailLabel"), new Model<String>(), true);

        public AddSubscriberPanel(String id) {
            super(id);

            form.add(emailField);
            form.add(new FormRequired("required"));
        }

        @Override
        protected String getHeader() {
            return getString("addSubscriberHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Add);
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Add:
                    if (subscriberManager.checkSubscriberByEmail(emailField.getModelObject())) {
                        ELTAlerts.renderErrorPopup(getString("errorAlreadyExists"), target);
                    } else {
                        Subscriber subscriber = new Subscriber();
                        subscriber.setEmail(emailField.getModelObject());
                        try {
                            subscriberManager.createSubscriber(subscriber);
                        } catch (SubscriberException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            return;
                        }

                        newCallback.process(new GenericDBModel<>(Subscriber.class, subscriber), target);
                    }

                    break;
            }
        }

        @Override
        public void setNewCallback(IDialogActionProcessor<Subscriber> callback) {
            this.newCallback = callback;
        }

        @Override
        protected void onBeforeRender() {
            super.onBeforeRender();
            emailField.setModelObject(null);
        }
    }

    private class CheckResultsDialog extends ELTDialogPanel implements IDialogCloseCallback, IDialogConfirmCallback {

        private IDialogCloseCallback.IDialogActionProcessor closeCallback;
        private IDialogConfirmCallback.IDialogActionProcessor confirmCallback;

        public CheckResultsDialog(String id) {
            super(id);
            form.add(new Label("resultLabel",
                    String.format(getString("resultLabel"), notSubscribersModel.getObject().size())));
        }

        @Override
        protected String getHeader() {
            return getString("resultHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Yes, EVENT.No));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Yes)) {
                confirmCallback.process(target);
            } else if (event.equals(EVENT.No)) {
                closeCallback.process(target);
            }
        }

        @Override
        public void setCloseCallback(IDialogCloseCallback.IDialogActionProcessor callback) {
            this.closeCallback = callback;
        }

        @Override
        public void setConfirmCallback(IDialogConfirmCallback.IDialogActionProcessor callback) {
            this.confirmCallback = callback;
        }
    }
}
