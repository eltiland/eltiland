package com.eltiland.ui.course.components.editPanels.elements;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course.DocumentCourseItem;
import com.eltiland.model.course.GoogleCourseItem;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.utils.MimeType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Google course item editing panel.
 *
 * @author Aleksey Plotnikov
 */
public abstract class GoogleEditPanel extends AbstractCourseItemEditPanel<GoogleCourseItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleEditPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    private EltiAjaxLink enablePrintButton, disablePrintButton, controlSetButton, controlResetButton;

    protected GoogleEditPanel(String id, final IModel<GoogleCourseItem> googleCourseItemIModel) {
        super(id, googleCourseItemIModel);

        genericManager.initialize(getModelObject(), getModelObject().getDriveFile());

        GoogleDriveFile.TYPE type = GoogleDriveFile.TYPE.DOCUMENT;
        String mimeType = getModelObject().getDriveFile().getMimeType();
        if (MimeType.getDocumentTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.DOCUMENT;
        } else if (MimeType.getPresentationTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.PRESENTATION;
        }

        final ELTGoogleDriveEditor content = new ELTGoogleDriveEditor("contentField",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getDriveFile()),
                ELTGoogleDriveEditor.MODE.EDIT, type) {
            @Override
            protected void onUpload(GoogleDriveFile file) {
                googleCourseItemIModel.getObject().setDriveFile(file);
                try {
                    genericManager.update(googleCourseItemIModel.getObject());
                } catch (ConstraintException e) {
                    LOGGER.error("Error while saving course item");
                    throw new WicketRuntimeException("Error while saving course item", e);
                }
            }

            @Override
            protected Panel getAdditionalPanel(String markupId) {
                return new ActionPanel(markupId, GoogleEditPanel.this.getModel());
            }
        };

        add(content);
    }

    protected abstract void onSave(AjaxRequestTarget target);

    public class ActionPanel extends BaseEltilandPanel<GoogleCourseItem> {

        private IModel<GoogleCourseItem> itemModel = new GenericDBModel<>(GoogleCourseItem.class);

        protected ActionPanel(String id, IModel<GoogleCourseItem> googleCourseItemIModel) {
            super(id, googleCourseItemIModel);
            itemModel.setObject(genericManager.getObject(GoogleCourseItem.class, getModelObject().getId()));

            EltiAjaxLink saveButton = new EltiAjaxLink("saveButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    try {
                        genericManager.initialize(GoogleEditPanel.this.getModelObject(),
                                GoogleEditPanel.this.getModelObject().getDriveFile());
                        googleDriveManager.publishDocument(GoogleEditPanel.this.getModelObject().getDriveFile());
                        googleDriveManager.insertPermission(GoogleEditPanel.this.getModelObject().getDriveFile(),
                                new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER,
                                        ELTGooglePermissions.TYPE.ANYONE));
                    } catch (GoogleDriveException e) {
                        LOGGER.error("Error while publish course");
                        throw new WicketRuntimeException("Error while publish course", e);
                    }
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                    onSave(target);
                }
            };

            enablePrintButton = new EltiAjaxLink("enablePrintButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    ((DocumentCourseItem) itemModel.getObject()).setPrintable(true);
                    try {
                        genericManager.update(itemModel.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Error while change item params");
                        throw new WicketRuntimeException("Error while change item params", e);
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
                    ((DocumentCourseItem) itemModel.getObject()).setPrintable(false);
                    try {
                        genericManager.update(itemModel.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Error while change item params");
                        throw new WicketRuntimeException("Error while change item params", e);
                    }
                    enablePrintButton.setVisible(true);
                    disablePrintButton.setVisible(false);
                    target.add(enablePrintButton);
                    target.add(disablePrintButton);
                }
            };

            controlSetButton = new EltiAjaxLink("controlSetButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    itemModel.getObject().setControl(true);
                    try {
                        genericManager.update(itemModel.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Error while change item params");
                        throw new WicketRuntimeException("Error while change item params", e);
                    }
                    controlSetButton.setVisible(false);
                    controlResetButton.setVisible(true);
                    target.add(controlSetButton);
                    target.add(controlResetButton);
                }
            };

            controlResetButton = new EltiAjaxLink("controlResetButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    itemModel.getObject().setControl(false);
                    try {
                        genericManager.update(itemModel.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Error while change item params");
                        throw new WicketRuntimeException("Error while change item params", e);
                    }
                    controlSetButton.setVisible(true);
                    controlResetButton.setVisible(false);
                    target.add(controlSetButton);
                    target.add(controlResetButton);
                }
            };

            enablePrintButton.setVisible((itemModel.getObject() instanceof DocumentCourseItem) &&
                    (!((DocumentCourseItem) itemModel.getObject()).isPrintable()));
            disablePrintButton.setVisible((itemModel.getObject() instanceof DocumentCourseItem) &&
                    ((DocumentCourseItem) itemModel.getObject()).isPrintable());
            controlSetButton.setVisible(!itemModel.getObject().isControl());
            controlResetButton.setVisible(itemModel.getObject().isControl());

            add(saveButton);
            add(enablePrintButton.setOutputMarkupPlaceholderTag(true));
            add(disablePrintButton.setOutputMarkupPlaceholderTag(true));
            add(controlSetButton.setOutputMarkupPlaceholderTag(true));
            add(controlResetButton.setOutputMarkupPlaceholderTag(true));
            saveButton.add(new AttributeModifier("title", new ResourceModel("saveTooltip")));
            saveButton.add(new TooltipBehavior());
            enablePrintButton.add(new AttributeModifier("title", new ResourceModel("enablePrintTooltip")));
            enablePrintButton.add(new TooltipBehavior());
            disablePrintButton.add(new AttributeModifier("title", new ResourceModel("disablePrintTooltip")));
            disablePrintButton.add(new TooltipBehavior());
            controlSetButton.add(new AttributeModifier("title", new ResourceModel("setControlTooltip")));
            controlSetButton.add(new TooltipBehavior());
            controlResetButton.add(new AttributeModifier("title", new ResourceModel("resetControlTooltip")));
            controlResetButton.add(new TooltipBehavior());
        }
    }
}
