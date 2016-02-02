package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing course element, based of google.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleCourseItemPanel extends AbstractCourseItemPanel<ELTGoogleCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private Dialog<GooglePrintStatisticsPanel> printStatisticsPanelDialog =
            new Dialog<GooglePrintStatisticsPanel>("printStatisticsDialog", 510) {
        @Override
        public GooglePrintStatisticsPanel createDialogPanel(String id) {
            return new GooglePrintStatisticsPanel(id);
        }

        @Override
        public void registerCallback(GooglePrintStatisticsPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTDocumentCourseItem>() {
                @Override
                public void process(IModel<ELTDocumentCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    public GoogleCourseItemPanel(String id, IModel<ELTGoogleCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        GoogleDriveFile.TYPE type = getModelObject() instanceof ELTDocumentCourseItem ?
                GoogleDriveFile.TYPE.DOCUMENT : GoogleDriveFile.TYPE.PRESENTATION;

        genericManager.initialize(getModelObject(), getModelObject().getItem());

        ELTGoogleDriveEditor content = new ELTGoogleDriveEditor("content",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem()),
                ELTGoogleDriveEditor.MODE.EDIT, type) {
            @Override
            protected void onUpload(GoogleDriveFile file) {
                GoogleCourseItemPanel.this.getModelObject().setItem(file);
                try {
                    courseItemManager.update(GoogleCourseItemPanel.this.getModelObject());
                } catch (CourseException e) {
                    EltiStaticAlerts.registerErrorPopup(e.getMessage());
                }
            }

            @Override
            protected Panel getAdditionalPanel(String markupId) {
                return new ActionPanel(markupId, GoogleCourseItemPanel.this.getModel());
            }
        };

        add(content);
    }

    public class ActionPanel extends BaseEltilandPanel<ELTGoogleCourseItem> {

        EltiAjaxLink enablePrintButton, disablePrintButton, printControlButton;

        protected ActionPanel(String id, IModel<ELTGoogleCourseItem> googleCourseItemIModel) {
            super(id, googleCourseItemIModel);

            EltiAjaxLink saveButton = new EltiAjaxLink("saveButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        genericManager.initialize(ActionPanel.this.getModelObject(),
                                ActionPanel.this.getModelObject().getItem());
                        googleDriveManager.publishDocument(ActionPanel.this.getModelObject().getItem());
                        googleDriveManager.insertPermission(ActionPanel.this.getModelObject().getItem(),
                                new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER,
                                        ELTGooglePermissions.TYPE.ANYONE));
                    } catch (GoogleDriveException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                }
            };

            enablePrintButton = new EltiAjaxLink("enablePrintButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ((ELTDocumentCourseItem) ActionPanel.this.getModelObject()).setPrintable(true);
                    try {
                        courseItemManager.update(ActionPanel.this.getModelObject());
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    enablePrintButton.setVisible(false);
                    disablePrintButton.setVisible(true);
                    target.add(enablePrintButton);
                    target.add(disablePrintButton);
                }
            };

            disablePrintButton = new EltiAjaxLink("disablePrintButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ((ELTDocumentCourseItem) ActionPanel.this.getModelObject()).setPrintable(false);
                    try {
                        courseItemManager.update(ActionPanel.this.getModelObject());
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    enablePrintButton.setVisible(true);
                    disablePrintButton.setVisible(false);
                    target.add(enablePrintButton);
                    target.add(disablePrintButton);
                }
            };

            printControlButton = new EltiAjaxLink("printControlButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    printStatisticsPanelDialog.getDialogPanel().initData(GoogleCourseItemPanel.this.getModel());
                    printStatisticsPanelDialog.show(target);
                }
            };

            // check for demo or full
            genericManager.initialize(getModelObject(), getModelObject().getBlock());
            boolean isFull = false;

            if (getModelObject().getBlock() != null) {
                ELTCourseBlock block = getModelObject().getBlock();
                genericManager.initialize(block, block.getCourse());
                isFull = block.getCourse() != null;
            } else {
                genericManager.initialize(getModelObject(), getModelObject().getParent());
                ELTGroupCourseItem group = getModelObject().getParent();
                genericManager.initialize(group, group.getBlock());
                genericManager.initialize(group.getBlock(), group.getBlock().getCourse());
                isFull = group.getBlock().getCourse() != null;
            }

            boolean isDoc = getModelObject() instanceof ELTDocumentCourseItem;
            boolean isPrint = isDoc && ((ELTDocumentCourseItem) getModelObject()).isPrintable();
            enablePrintButton.setVisible(isDoc && !isPrint && !isFull);
            disablePrintButton.setVisible(isDoc && isPrint && !isFull);

            // Temporary
            printControlButton.setVisible(false);
            //printControlButton.setVisible(isDoc && isFull);

            add(saveButton);
            add(enablePrintButton.setOutputMarkupPlaceholderTag(true));
            add(disablePrintButton.setOutputMarkupPlaceholderTag(true));
            add(printControlButton.setOutputMarkupPlaceholderTag(true));
            saveButton.add(new AttributeModifier("title", GoogleCourseItemPanel.this.getString("save.tooltip")));
            saveButton.add(new TooltipBehavior());
            enablePrintButton.add(new AttributeModifier("title",
                    GoogleCourseItemPanel.this.getString("enable.print.tooltip")));
            enablePrintButton.add(new TooltipBehavior());
            disablePrintButton.add(new AttributeModifier("title",
                    GoogleCourseItemPanel.this.getString("disable.print.tooltip")));
            disablePrintButton.add(new TooltipBehavior());
            printControlButton.add(new AttributeModifier("title",
                    GoogleCourseItemPanel.this.getString("control.print.tooltip")));
            printControlButton.add(new TooltipBehavior());

            add(printStatisticsPanelDialog);
        }
    }
}
