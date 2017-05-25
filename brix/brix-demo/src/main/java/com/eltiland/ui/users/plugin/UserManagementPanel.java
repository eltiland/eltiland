package com.eltiland.ui.users.plugin;

import com.eltiland.bl.*;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTPasswordField;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.BaseWorktopPage;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User management panel.
 */
public class UserManagementPanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(UserManagementPanel.class);

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private IndexCreator indexCreator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private ConfirmationManager confirmationManager;

    private ELTTable<User> grid;

    private final static String ACTIVE_CLASS = "active_item";
    private final static String DISACTIVE_CLASS = "disactive_item";

    private Dialog<ApplyPanel> applyDialog = new Dialog<ApplyPanel>("applyDialog", 320) {
        @Override
        public ApplyPanel createDialogPanel(String id) {
            return new ApplyPanel(id);
        }

        @Override
        public void registerCallback(ApplyPanel panel) {
            super.registerCallback(panel);
            panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    private Dialog<InvitePanel> inviteUserDialog = new Dialog<InvitePanel>("inviteDialog", 320) {
        @Override
        public InvitePanel createDialogPanel(String id) {
            return new InvitePanel(id);
        }

        @Override
        public void registerCallback(InvitePanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<User>() {
                @Override
                public void process(IModel<User> model, AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected UserManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<User>("grid", 20) {
            @Override
            protected List<IColumn<User>> getColumns() {
                List<IColumn<User>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<User>(new ResourceModel("nameColumn"), "name", "name"));
                columns.add(new PropertyColumn<User>(new ResourceModel("emailColumn"), "email", "email"));
                columns.add(new AbstractColumn<User>(new ResourceModel("confirmColumn"), "confirmationDate") {
                    @Override
                    public void populateItem(Item<ICellPopulator<User>> cellItem,
                                             String componentId, IModel<User> rowModel) {
                        boolean confirmed = (rowModel.getObject().getConfirmationDate() != null);
                        Label label = new Label(componentId,
                                confirmed ? DateUtils.formatFullDate(rowModel.getObject().getConfirmationDate()) :
                                        getString("not_confirmed"));
                        label.add(new AttributeModifier("class",
                                new Model<>(confirmed ? ACTIVE_CLASS : DISACTIVE_CLASS)));
                        cellItem.add(label);
                    }
                });
                columns.add(new AbstractColumn<User>(new ResourceModel("activeColumn"), "isActive") {
                    @Override
                    public void populateItem(Item<ICellPopulator<User>> cellItem,
                                             String componentId, IModel<User> rowModel) {
                        boolean active = rowModel.getObject().isActive();
                        Label label = new Label(componentId, getString(active ? "yes" : "no"));
                        label.add(new AttributeModifier("class",
                                new Model<>(active ? ACTIVE_CLASS : DISACTIVE_CLASS)));
                        cellItem.add(label);
                    }
                });
                columns.add(new AbstractColumn<User>(new ResourceModel("checkedColumn"), "isChecked") {
                    @Override
                    public void populateItem(Item<ICellPopulator<User>> cellItem,
                                             String componentId, IModel<User> rowModel) {
                        boolean active = rowModel.getObject().isChecked();
                        Label label = new Label(componentId, getString(active ? "yes" : "no"));
                        label.add(new AttributeModifier("class",
                                new Model<>(active ? ACTIVE_CLASS : DISACTIVE_CLASS)));
                        cellItem.add(label);
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return userManager.getUserSearchList(first, count, getSearchString(),
                        getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return userManager.getUserSearchCount(getSearchString());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<User> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.PROFILE, GridAction.ON,
                        GridAction.OFF, GridAction.APPLY));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<User> rowModel) {
                switch (action) {
                    case ON:
                        return !(rowModel.getObject().isActive());
                    case OFF:
                        return rowModel.getObject().isActive();
                    case APPLY:
                        return rowModel.getObject().getConfirmationDate() == null;
                    default:
                        return true;
                }
            }

            @Override
            protected void onClick(IModel<User> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case PROFILE:
                        setResponsePage(BaseWorktopPage.class,
                                new PageParameters().add(BaseWorktopPage.PARAM_USER, rowModel.getObject().getId()));
                        break;
                    case ON:
                        rowModel.getObject().setActive(true);
                        try {
                            genericManager.update(rowModel.getObject());
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update user", e);
                            throw new WicketRuntimeException(e);
                        }
                        target.add(grid);
                        break;
                    case OFF:
                        rowModel.getObject().setActive(false);
                        try {
                            genericManager.update(rowModel.getObject());
                        } catch (ConstraintException e) {
                            LOGGER.error("Cannot update user", e);
                            throw new WicketRuntimeException(e);
                        }
                        target.add(grid);
                        break;
                    case APPLY:
                        applyDialog.getDialogPanel().initData(rowModel);
                        applyDialog.show(target);
                        break;
                    case ADD:
                        inviteUserDialog.show(target);
                        break;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case PROFILE:
                        return getString("profile_tooltip");
                    case ON:
                        return getString("activate_tooltip");
                    case OFF:
                        return getString("deactivate_tooltip");
                    case APPLY:
                        return getString("apply_tooltip");
                    case ADD:
                        return getString("add_tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }
        };

        add(grid.setOutputMarkupId(true));
        add(applyDialog);
        add(inviteUserDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        indexCreator.doRebuildIndex(User.class);
    }

    private class ApplyPanel extends ELTDialogPanel implements IDialogConfirmCallback {

        private IDialogActionProcessor callback;

        private Label label = new Label("name", new Model<String>());
        private ELTPasswordField pass = new ELTPasswordField(
                "pass", new ResourceModel("passField"), new Model<String>(), true);

        private IModel<User> userModel = new GenericDBModel<>(User.class);

        private ApplyPanel(String id) {
            super(id);

            form.add(label);
            form.add(pass);
        }

        public void initData(IModel<User> userModel) {
            this.userModel = userModel;
            label.setDefaultModelObject(userModel.getObject().getName());
        }

        @Override
        protected String getHeader() {
            return getString("applyHeader");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Apply));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Apply)) {
                genericManager.initialize(userModel.getObject(), userModel.getObject().getConfirmation());
                Confirmation confirmation = userModel.getObject().getConfirmation();

                userModel.getObject().setPassword(HashesUtils.getSHA1inHex(pass.getModelObject()));
                userModel.getObject().setConfirmationDate(DateUtils.getCurrentDate());
                userModel.getObject().setConfirmation(null);
                try {
                    genericManager.update(userModel.getObject());
                    genericManager.delete(confirmation);

                    emailMessageManager.sendConfirmationMessage(userModel.getObject().getEmail(), pass.getModelObject());
                } catch (ConstraintException | EltilandManagerException e) {
                    LOGGER.error("Cannot confirm user", e);
                    throw new WicketRuntimeException(e);
                } catch (EmailException e) {
                    LOGGER.error("Cannot send message", e);
                    throw new WicketRuntimeException(e);
                }
                callback.process(target);
            }
        }

        @Override
        public void setConfirmCallback(IDialogActionProcessor callback) {
            this.callback = callback;
        }

        @Override
        protected boolean showButtonDecorator() {
            return true;
        }
    }

    private class InvitePanel extends ELTDialogPanel implements IDialogNewCallback<User> {

        private ELTTextField<String> nameField =
                new ELTTextField<>("nameField", new ResourceModel("name"), new Model<String>(), String.class, true);
        private ELTTextEmailField emailField =
                new ELTTextEmailField("emailField", new ResourceModel("email"), new Model<String>(), true);

        private IDialogActionProcessor<User> callback;

        public InvitePanel(String id) {
            super(id);
            form.add(nameField);
            form.add(emailField);
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Send));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Send)) {
                User user = new User();
                user.setName(nameField.getModelObject());
                user.setEmail(emailField.getModelObject());
                user.setPassword(RandomStringUtils.randomAlphanumeric(10));
                user.setAvatar(fileManager.getStandardIconFile(UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT));
                user.setActive(true);

                try {
                    user = userManager.createUser(user);
                    user.setConfirmationDate(null);
                    genericManager.update(user);

                    Date endDate = DateTime.now().plusDays(Integer.decode(
                            eltilandProps.getProperty("confirmation.days")).intValue()).toDate();
                    confirmationManager.createConfirmation(user, endDate);
                    emailMessageManager.sendEmailConfirmationToUser(user);
                } catch (UserException | EltilandManagerException | ConstraintException e) {
                    LOGGER.error("Got exception when creating user", e);
                    throw new WicketRuntimeException("Got exception when creating user", e);
                } catch (EmailException e) {
                    LOGGER.error("Got exception when sending email", e);
                    throw new WicketRuntimeException("Got exception when sending email", e);
                }
                ELTAlerts.renderOKPopup(getString("registerMessage"), target);
                callback.process(new GenericDBModel<>(User.class, user), target);
            }
        }

        @Override
        public void setNewCallback(IDialogActionProcessor<User> callback) {
            this.callback = callback;
        }

        @Override
        protected boolean showButtonDecorator() {
            return true;
        }
    }

}
