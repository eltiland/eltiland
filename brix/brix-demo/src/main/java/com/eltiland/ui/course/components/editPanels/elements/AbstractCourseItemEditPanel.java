package com.eltiland.ui.course.components.editPanels.elements;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTDateTimeField;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Abstract panel for editing course item.
 *
 * @author Aleksey Plotnikov.
 */
public class AbstractCourseItemEditPanel<T extends CourseItem> extends BaseEltilandPanel<T> {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCourseItemEditPanel.class);

    private Dialog<IntervalPanel> intervalPanelDialog = new Dialog<IntervalPanel>("intervalDialog", 330) {
        @Override
        public IntervalPanel createDialogPanel(String id) {
            return new IntervalPanel(id);
        }

        @Override
        public void registerCallback(IntervalPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CourseItem>() {
                @Override
                public void process(IModel<CourseItem> model, AjaxRequestTarget target) {
                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot update item", e);
                        throw new WicketRuntimeException("Cannot update item", e);
                    }
                    close(target);
                    ELTAlerts.renderOKPopup(getString("invoiceMessage"), target);
                }
            });
        }
    };

    protected AbstractCourseItemEditPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);

        genericManager.initialize(getModelObject(), getModelObject().getFiles());

        WebMarkupContainer fileContainer = new WebMarkupContainer("fileContainer");
        add(fileContainer);

        ELTUploadComponent uploadComponent = new ELTUploadComponent("fileUploadPanel", 10) {
            @Override
            public void onFileUploaded(AjaxRequestTarget target) {
                super.onFileUploaded(target);
                CourseItem item = AbstractCourseItemEditPanel.this.getModelObject();

                for (File file : getUploadedFiles()) {
                    if (file.getId() == null) {
                        try {
                            file.setItem(item);
                            fileManager.saveFile(file);
                        } catch (FileException e) {
                            LOGGER.error("Cannot upload file", e);
                            throw new WicketRuntimeException("Cannot upload file", e);
                        }
                    }
                }
            }

            @Override
            public void onFileDeleted(AjaxRequestTarget target) {
                super.onFileDeleted(target);
                CourseItem item = AbstractCourseItemEditPanel.this.getModelObject();

                List<File> files = fileManager.getFilesOfCourseItem(item);
                if (!(files.isEmpty())) {
                    files.removeAll(getUploadedFiles());
                    for (File file : files) {
                        try {
                            fileManager.deleteFile(file);
                        } catch (FileException e) {
                            LOGGER.error("Cannot remove file", e);
                            throw new WicketRuntimeException("Cannot remove file", e);
                        }
                    }
                }
            }
        };
        uploadComponent.setUploadedFiles(fileManager.getFilesOfCourseItem(tiModel.getObject()));
        fileContainer.add(uploadComponent);

        add(new ActionPanel("actionPanel", getModel()).setVisible(showActions()));
        add(intervalPanelDialog);
    }

    public class ActionPanel<T extends CourseItem> extends BaseEltilandPanel<T> {

        private EltiAjaxLink controlSetButton, controlResetButton, accessDatesButton;

        private IModel<CourseItem> itemModel = new GenericDBModel<>(CourseItem.class);

        protected ActionPanel(String id, IModel<T> courseItemIModel) {
            super(id, courseItemIModel);
            itemModel.setObject(genericManager.getObject(CourseItem.class, ((CourseItem) getModelObject()).getId()));

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

            accessDatesButton = new EltiAjaxLink("accessDateButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    intervalPanelDialog.getDialogPanel().initData(itemModel);
                    intervalPanelDialog.show(target);
                }
            };

            controlSetButton.setVisible(!itemModel.getObject().isControl());
            controlResetButton.setVisible(itemModel.getObject().isControl());

            add(controlSetButton.setOutputMarkupPlaceholderTag(true));
            add(controlResetButton.setOutputMarkupPlaceholderTag(true));
            add(accessDatesButton.setOutputMarkupPlaceholderTag(true));
            controlSetButton.add(new AttributeModifier("title", new ResourceModel("setControlTooltip")));
            controlSetButton.add(new TooltipBehavior());
            controlResetButton.add(new AttributeModifier("title", new ResourceModel("resetControlTooltip")));
            controlResetButton.add(new TooltipBehavior());
            accessDatesButton.add(new AttributeModifier("title", new ResourceModel("accessTooltip")));
            accessDatesButton.add(new TooltipBehavior());
        }
    }

    protected boolean showActions() {
        return false;
    }

    private class IntervalPanel extends ELTDialogPanel implements IDialogUpdateCallback<CourseItem> {

        private IDialogActionProcessor<CourseItem> callback;

        private IModel<CourseItem> courseItemIModel = new GenericDBModel<>(CourseItem.class);

        private ELTDateTimeField start =
                new ELTDateTimeField("start_date", new ResourceModel("start"), new Model<Date>(), Date.class, true);
        private ELTDateTimeField end =
                new ELTDateTimeField("end_date", new ResourceModel("end"), new Model<Date>(), Date.class, true);

        public IntervalPanel(String id) {
            super(id);
            form.add(start);
            form.add(end);
        }

        public void initData(IModel<CourseItem> courseItemIModel) {
            this.courseItemIModel = courseItemIModel;
            start.setModelObject(courseItemIModel.getObject().getAccessStartDate());
            end.setModelObject(courseItemIModel.getObject().getAccessEndDate());
        }

        @Override
        protected String getHeader() {
            return getString("headerInterval");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                courseItemIModel.getObject().setAccessStartDate(start.getModelObject());
                courseItemIModel.getObject().setAccessEndDate(end.getModelObject());
                callback.process(courseItemIModel, target);
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<CourseItem> callback) {
            this.callback = callback;
        }
    }
}
