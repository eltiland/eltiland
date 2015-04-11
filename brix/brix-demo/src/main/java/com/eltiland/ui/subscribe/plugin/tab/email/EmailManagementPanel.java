package com.eltiland.ui.subscribe.plugin.tab.email;

import com.eltiland.bl.EmailManager;
import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.SubscriberManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.subscribe.Email;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.PreviewPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Email managements panel.
 */
public class EmailManagementPanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(EmailManagementPanel.class);

    @SpringBean
    private EmailManager emailManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private SubscriberManager subscriberManager;

    private ELTTable<Email> grid, gridSended;

    private Dialog<EmailPropertyPanel> addEmailDialog = new Dialog<EmailPropertyPanel>("addEmailDialog", 740) {
        @Override
        public EmailPropertyPanel createDialogPanel(String id) {
            return new EmailPropertyPanel(id, addEmailDialog);
        }

        @Override
        public void registerCallback(EmailPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Email>() {
                @Override
                public void process(IModel<Email> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("emailCreateMessage"), target);
                    setResponsePage(getPage());
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Email>() {
                @Override
                public void process(IModel<Email> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                    ELTAlerts.renderOKPopup(getString("emailSaveMessage"), target);
                    setResponsePage(getPage());
                }
            });
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            super.onClose(target);
            setResponsePage(getPage());
        }
    };

    private Dialog<PreviewPanel> viewPanelDialog = new Dialog<PreviewPanel>("viewEmailDialog", 760) {
        @Override
        public PreviewPanel createDialogPanel(String id) {
            return new PreviewPanel(id);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public EmailManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        final WebMarkupContainer sendContainer = new WebMarkupContainer("sendContainer") {
            @Override
            public boolean isVisible() {
                return (emailManager.getEmailCount(true) > 0);
            }
        };

        grid = new ELTTable<Email>("emailGrid", 10) {
            @Override
            protected boolean hasConfirmation(GridAction action) {
                switch (action) {
                    case PLAY:
                    case USERS:
                    case PROFILE:
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case EDIT:
                        return getString("editTooltip");
                    case REMOVE:
                        return getString("removeTooltip");
                    case USERS:
                        return getString("sendTooltip");
                    case PROFILE:
                        return getString("sendOnlyMeTooltip");
                    case PLAY:
                        return getString("sendOtherTooltip");
                    default:
                        return "";
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Email> rowModel) {
                return Arrays.asList(GridAction.EDIT, GridAction.REMOVE, GridAction.USERS, GridAction.PROFILE,
                        GridAction.PLAY);
            }

            @Override
            protected List<IColumn<Email>> getColumns() {
                List<IColumn<Email>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Email>(new ResourceModel("headerColumn"), "header", "header"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return emailManager.getEmailList(false, first, count, getSort().getProperty(),
                        getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return emailManager.getEmailCount(false);
            }

            @Override
            protected void onClick(IModel<Email> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REMOVE:
                        try {
                            emailManager.deleteEmail(rowModel.getObject());
                            ELTAlerts.renderOKPopup(getString("emailRemoveMessage"), target);
                        } catch (EmailException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);

                        break;

                    case EDIT:
                        addEmailDialog.getDialogPanel().initEditMode(rowModel.getObject());
                        addEmailDialog.show(target);

                        break;

                    case USERS:
                        if (subscriberManager.getActiveSubscriberCount() == 0) {
                            ELTAlerts.renderErrorPopup(getString("noActiveSubscribers"), target);
                        } else {
                            try {
                                emailMessageManager.sendSubscribe(rowModel.getObject());
                                ELTAlerts.renderOKPopup(getString("emailSendMessage"), target);
                            } catch (EmailException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                            target.add(grid);
                            target.add(sendContainer);
                            target.add(gridSended);
                        }

                        break;

                    case PROFILE:
                        try {
                            emailMessageManager.sendSubscribeToCurrentUser(rowModel.getObject());
                            ELTAlerts.renderOKPopup(getString("emailSendMessage"), target);
                        } catch (EmailException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }

                        break;

                    case PLAY:
                        sendTestMailDialog.getDialogPanel().setMail(rowModel);
                        sendTestMailDialog.show(target);

                        break;
                }
            }

            @Override
            public boolean isVisible() {
                return (emailManager.getEmailCount(false) > 0);
            }
        };

        gridSended = new ELTTable<Email>("emailSendedGrid", 10) {
            @Override
            protected List<IColumn<Email>> getColumns() {
                List<IColumn<Email>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Email>(new ResourceModel("dateColumn"), "sendDate", "sendDate"));
                columns.add(new PropertyColumn<Email>(new ResourceModel("headerColumn"), "header", "header"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return emailManager.getEmailList(true, first, count, getSort().getProperty(),
                        getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return emailManager.getEmailCount(true);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Email> rowModel) {
                return Arrays.asList(GridAction.PAGE_PREVIEW);
            }

            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.ADD);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected void onClick(IModel<Email> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        addEmailDialog.getDialogPanel().initCreateMode();
                        addEmailDialog.show(target);

                        break;
                    case PAGE_PREVIEW:
                        viewPanelDialog.getDialogPanel().setData(Model.of(rowModel.getObject().getContent()));
                        viewPanelDialog.show(target);

                        break;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ADD:
                        return getString("addEmailLabel");
                    case PAGE_PREVIEW:
                        return getString("openAction");
                    default:
                        return "";
                }
            }
        };

        add(grid.setOutputMarkupId(true));
        sendContainer.add(gridSended.setOutputMarkupId(true));

        add(sendContainer.setOutputMarkupId(true));
        add(addEmailDialog);
        add(viewPanelDialog);
        add(sendTestMailDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private Dialog<SendTestMailPanel> sendTestMailDialog = new Dialog<SendTestMailPanel>("sendTestMailDialog",
            UIConstants.DIALOG_POPUP_WIDTH_SMALL) {
        @Override
        public SendTestMailPanel createDialogPanel(String id) {
            return new SendTestMailPanel(id);
        }

        @Override
        public void registerCallback(SendTestMailPanel panel) {
            super.registerCallback(panel);
            panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    private class SendTestMailPanel extends ELTDialogPanel implements IDialogCloseCallback {
        private IDialogActionProcessor callback;
        private IModel<Email> mailModel;
        private ELTTextArea emailPanel = new ELTTextArea("email", Model.of("Email"), new Model<String>());

        public SendTestMailPanel(String id) {
            super(id);

            form.add(emailPanel);
        }

        public void setMail(IModel<Email> email) {
            mailModel = email;
        }

        @Override
        protected String getHeader() {
            return EmailManagementPanel.this.getString("sendOtherTooltip");
        }

        @Override
        protected List<EVENT> getActionList() {
            return Arrays.asList(EVENT.Send);
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            switch (event) {
                case Send:
                    if (mailModel == null) {
                        callback.process(target);
                        return;
                    }

                    try {
                        emailMessageManager.sendSubscribeToUser(mailModel.getObject(), emailPanel.getModelObject());
                        ELTAlerts.renderOKPopup(EmailManagementPanel.this.getString("emailSendMessage"), target);
                    } catch (EmailException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }


                    callback.process(target);
                    break;
            }
        }

        @Override
        public void setCloseCallback(IDialogActionProcessor callback) {
            this.callback = callback;
        }
    }
}
