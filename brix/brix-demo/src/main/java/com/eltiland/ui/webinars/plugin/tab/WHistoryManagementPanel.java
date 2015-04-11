package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.components.column.WebinarModeratorColumn;
import com.eltiland.ui.webinars.plugin.tab.components.WebinarMemberPanel;
import com.eltiland.ui.webinars.plugin.tab.components.WebinarUploadFilePanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Webinars history management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WHistoryManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WHistoryManagementPanel.class);

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;

    private Dialog<WebinarMemberPanel> webinarMemberListPanelDialog =
            new Dialog<WebinarMemberPanel>("memberListDialog", 865) {
                @Override
                public WebinarMemberPanel createDialogPanel(String id) {
                    return new WebinarMemberPanel(id);
                }
            };

    private Dialog<DescriptionPanel> changeDescPanelDialog =
            new Dialog<DescriptionPanel>("descEditDialog", 380) {
                @Override
                public DescriptionPanel createDialogPanel(String id) {
                    return new DescriptionPanel(id) {
                        @Override
                        protected void onSave(AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                        }
                    };
                }
            };

    private Dialog<WebinarUploadFilePanel> webinarUploadFilePanelDialog =
            new Dialog<WebinarUploadFilePanel>("uploadDialog", 455) {
                @Override
                public WebinarUploadFilePanel createDialogPanel(String id) {
                    return new WebinarUploadFilePanel(id);
                }

                @Override
                public void registerCallback(WebinarUploadFilePanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Webinar>() {
                        @Override
                        public void process(IModel<Webinar> model, AjaxRequestTarget target) {
                            try {
                                webinarManager.updateFiles(model.getObject());
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot save webinar", e);
                                throw new WicketRuntimeException("Cannot save webinar", e);
                            }
                            close(target);
                            ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                        }
                    });
                }
            };

    private Dialog<SendMessagePanel> sendDialog = new Dialog<SendMessagePanel>("sendDialog", 380) {
        @Override
        public SendMessagePanel createDialogPanel(String id) {
            return new SendMessagePanel(id);
        }

        @Override
        public void registerCallback(SendMessagePanel panel) {
            super.registerCallback(panel);
            panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    ELTAlerts.renderOKPopup(getString("sendMessage"), target);
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
    public WHistoryManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        ELTTable<Webinar> table = new ELTTable<Webinar>("grid", 20) {
            @Override
            protected List<IColumn<Webinar>> getColumns() {
                List<IColumn<Webinar>> columnList = new ArrayList<>();
                columnList.add(new PropertyColumn<Webinar>(new ResourceModel("topicTabLabel"), "name", "name"));
                columnList.add(new PropertyColumn<Webinar>(
                        new ResourceModel("descriptionTabLabel"), "description", "description"));
                columnList.add(new PropertyColumn<Webinar>(
                        new ResourceModel("startDateTabLabel"), "startDate", "startDate"));
                columnList.add(new WebinarModeratorColumn(new ResourceModel("managerTabLabel")));
                columnList.add(new AbstractColumn<Webinar>(new ResourceModel("certTabLabel"), "certSended") {
                    @Override
                    public void populateItem(Item<ICellPopulator<Webinar>> cellItem,
                                             String componentId, IModel<Webinar> rowModel) {
                        boolean isCended = rowModel.getObject().isCertSended();
                        Label label = new Label(componentId, getString(isCended ? "send.yes" : "send.no"));
                        label.add(new AttributeModifier("style", new Model<>(isCended ? "color:green;" : "color:red")));
                        cellItem.add(label);
                    }
                });
                return columnList;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarManager.getWebinarList(
                        first, count, getSort().getProperty(), getSort().isAscending(), false, true, null).iterator();
            }

            @Override
            protected int getSize() {
                return webinarManager.getWebinarCount(false, true, null);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Webinar> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.EDIT, GridAction.USERS,
                        GridAction.UPLOAD, GridAction.SEND, GridAction.CERTIFICATE));
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.CERTIFICATE);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case USERS:
                        return getString("memberTooltip");
                    case EDIT:
                        return getString("editTooltip");
                    case UPLOAD:
                        return getString("uploadTooltip");
                    case SEND:
                        return getString("sendTooltip");
                    case CERTIFICATE:
                        return getString("certificateTooltip");
                    default:
                        return ReadonlyObjects.EMPTY_DISPLAY_MODEL.getObject();
                }
            }

            @Override
            protected void onClick(IModel<Webinar> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case USERS:
                        webinarMemberListPanelDialog.getDialogPanel().initData(rowModel);
                        webinarMemberListPanelDialog.show(target);
                        break;
                    case EDIT:
                        changeDescPanelDialog.getDialogPanel().initData(rowModel);
                        changeDescPanelDialog.show(target);
                        break;
                    case UPLOAD:
                        webinarUploadFilePanelDialog.getDialogPanel().initData(rowModel);
                        webinarUploadFilePanelDialog.show(target);
                        break;
                    case SEND:
                        sendDialog.getDialogPanel().initData(rowModel);
                        sendDialog.show(target);
                        break;
                    case CERTIFICATE:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                        if (rowModel.getObject().getRecord() == null) {
                            ELTAlerts.renderErrorPopup(getString("send.error"), target);
                        } else {
                            try {

                                List<WebinarUserPayment> users =
                                        webinarUserPaymentManager.getWebinarRealListeners(rowModel.getObject());
                                for (WebinarUserPayment user : users) {
                                    InputStream stream = webinarCertificateGenerator.generateCertificate(user);
                                    emailMessageManager.sendWebinarCertificate(user, stream);
                                }

                                if (!(rowModel.getObject().isCertSended())) {
                                    rowModel.getObject().setCertSended(true);
                                    genericManager.update(rowModel.getObject());
                                }
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot generate certificate", e);
                                throw new WicketRuntimeException("Cannot generate certificate", e);
                            } catch (EmailException e) {
                                LOGGER.error("Cannot send certificate", e);
                                throw new WicketRuntimeException("Cannot send certificate", e);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot save webinar", e);
                                throw new WicketRuntimeException("Cannot save webinar", e);
                            }
                            ELTAlerts.renderOKPopup(getString("sentCert"), target);
                        }
                }
            }
        };
        add(table.setOutputMarkupId(true));
        add(webinarMemberListPanelDialog);
        add(changeDescPanelDialog);
        add(webinarUploadFilePanelDialog);
        add(sendDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class SendMessagePanel extends ELTDialogPanel implements IDialogCloseCallback {

        private IDialogActionProcessor callback;

        private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

        private ELTTextArea mailText = new ELTTextArea(
                "mailText", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }

            @Override
            protected int getInitialHeight() {
                return 350;
            }
        };

        private CheckBox sentCert = new CheckBox("sentCert", new Model<Boolean>());
        private CheckBox sentFile = new CheckBox("sentFile", new Model<Boolean>());
        private CheckBox sentRecord = new CheckBox("sentRecord", new Model<Boolean>());

        public void initData(IModel<Webinar> webinarIModel) {
            this.webinarIModel = webinarIModel;

            genericManager.initialize(webinarIModel.getObject(), webinarIModel.getObject().getRecord());
            sentRecord.setEnabled(webinarIModel.getObject().getRecord() != null);
            genericManager.initialize(webinarIModel.getObject(), webinarIModel.getObject().getFiles());
            sentFile.setEnabled(!(webinarIModel.getObject().getFiles().isEmpty()));

            sentFile.setModelObject(false);
            sentCert.setModelObject(false);
            sentRecord.setModelObject(false);
        }

        public SendMessagePanel(String id) {
            super(id);
            form.add(mailText);
            form.add(sentCert);
            form.add(sentFile);
            form.add(sentRecord);
        }

        @Override
        protected String getHeader() {
            return getString("headerLabel");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Send));
        }

        @Override
        protected boolean showButtonDecorator() {
            return true;
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Send)) {
                try {
                    emailMessageManager.sendWebinarMessageToListeners(
                            webinarIModel.getObject(), mailText.getModelObject(),
                            sentCert.getModelObject(), sentFile.getModelObject(), sentRecord.getModelObject());
                    if (sentCert.getModelObject()) {
                        if (!(webinarIModel.getObject().isCertSended())) {
                            webinarIModel.getObject().setCertSended(true);
                            genericManager.update(webinarIModel.getObject());
                        }
                    }
                } catch (EmailException e) {
                    LOGGER.error("Cannot send message", e);
                    throw new WicketRuntimeException("Cannot send message", e);
                } catch (ConstraintException e) {
                    LOGGER.error("Cannot save webinar", e);
                    throw new WicketRuntimeException("Cannot save webinar", e);
                }
                callback.process(target);
            }
        }

        @Override
        public void setCloseCallback(IDialogActionProcessor callback) {
            this.callback = callback;
        }
    }

    public abstract class DescriptionPanel extends ELTDialogPanel {

        private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

        private ELTTextArea shortDesc = new ELTTextArea(
                "shortDesc", new ResourceModel("headerLabel"), new Model<String>(), true) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }
        };

        public DescriptionPanel(String id) {
            super(id);
            form.add(shortDesc);
        }

        public void initData(IModel<Webinar> webinar) {
            webinarIModel = webinar;
            shortDesc.setModelObject(webinar.getObject().getShortDesc());
        }

        @Override
        protected String getHeader() {
            return getString("headerLabel");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                Webinar webinar = webinarIModel.getObject();
                webinar.setShortDesc(shortDesc.getModelObject());
                try {
                    genericManager.update(webinar);
                } catch (ConstraintException e) {
                    LOGGER.error("Cannot save webinar", e);
                    throw new WicketRuntimeException("Cannot save webinar", e);
                }
                onSave(target);
            }
        }

        protected abstract void onSave(AjaxRequestTarget target);
    }
}
