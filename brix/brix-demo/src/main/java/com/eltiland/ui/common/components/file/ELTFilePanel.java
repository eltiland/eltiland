package com.eltiland.ui.common.components.file;

import com.eltiland.bl.FileManager;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.MimeTypes;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * File upload/download component.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTFilePanel extends BaseEltilandPanel<List<File>> {

    @SpringBean
    private FileManager fileManager;

    private WebMarkupContainer fileContainer = new WebMarkupContainer("fileContainer");

    private ListView<FileWrapper> fileList = new ListView<FileWrapper>("fileList", new FileWrapperModel()) {
        @Override
        protected void populateItem(final ListItem<FileWrapper> item) {
            if (item.getModelObject() != null) {
                FilePanel panel = new FilePanel("filePanel",
                        new GenericDBModel<>(File.class, item.getModelObject().file)) {
                    @Override
                    protected boolean canBeDeleted() {
                        return ELTFilePanel.this.canBeDeleted();
                    }

                    @Override
                    protected boolean canBeDownloaded() {
                        return ELTFilePanel.this.canBeDownloaded();
                    }

                    @Override
                    protected void onDelete(AjaxRequestTarget target) {
                        File file = item.getModelObject().file;
                        fileList.getModelObject().remove(item.getModelObject());
                        ELTFilePanel.this.getModelObject().remove(file);

                        if (file.getId() != null) {
                            onDeleteActions(target, file);
                            try {
                                fileManager.deleteFile(file);
                            } catch (FileException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                        target.add(fileContainer);
                    }
                };
                item.add(panel);
                if ((item.getIndex() + 1) < fileList.getModelObject().size()) {
                    panel.add(new AttributeAppender("class", new Model<>("not-last-item"), " "));
                }
                if (item.getIndex() == 0) {
                    panel.add(new AttributeAppender("class", new Model<>("first-item"), " "));
                }
            }
        }
    };

    private Dialog<UploadPanel> uploadPanelDialog = new Dialog<UploadPanel>("uploadDialog", 450) {
        @Override
        public UploadPanel createDialogPanel(String id) {
            return new UploadPanel(id, getAvailableTypes());
        }

        @Override
        public void registerCallback(UploadPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<FileUpload>() {
                @Override
                public void process(IModel<FileUpload> model, AjaxRequestTarget target) {
                    close(target);
                    File newFile = fileManager.createFileFromUpload(model.getObject());
                    fileList.getModelObject().add(new FileWrapper(newFile.getName(), newFile));
                    target.add(fileContainer);
                    onUploadActions(target, newFile);
                }
            });
        }
    };

    public ELTFilePanel(String id) {
        super(id, new GenericDBListModel<>(File.class));
        addComponents();
    }

    public ELTFilePanel(String id, IModel<List<File>> listIModel) {
        super(id, listIModel);
        setFiles(listIModel.getObject());
        addComponents();
    }

    private void addComponents() {
        add(new Label("caption", getCaption()));
        WebMarkupContainer uploadButton = new WebMarkupContainer("uploadButton") {
            @Override
            public boolean isVisible() {
                return canBeUploaded();
            }
        };
        uploadButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (fileList.getModelObject().size() >= getMaxFiles()) {
                    ELTAlerts.renderErrorPopup(getString("errorMaxFilesUploaded"), target);
                } else {
                    uploadPanelDialog.show(target);
                }
            }
        });

        add(uploadButton);
        uploadButton.add(new AttributeModifier("title", new ResourceModel("upload")));
        uploadButton.add(new TooltipBehavior());

        add(uploadPanelDialog);
        add(fileContainer.setOutputMarkupId(true));
        fileContainer.add(fileList);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_FILE_PANEL);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
    }

    /**
     * @param persist IF TRUE - if file not persisted, persist it.
     * @return list of files
     */
    public List<File> getFiles(boolean persist) throws FileException {
        List<File> files = new ArrayList<>();
        for (FileWrapper wrapper : fileList.getModelObject()) {
            File file = wrapper.file;
            if (persist) {
                if (file.getId() == null) {
                    file = fileManager.saveFile(file);
                }
            }
            files.add(file);
        }
        setFiles(files);
        return files;
    }

    public void setFiles(List<File> files) {
        List<FileWrapper> fileWrappers = new ArrayList<>();
        for (File file : files) {
            FileWrapper fileWrapper = new FileWrapper(file.getName(), file);
            fileWrappers.add(fileWrapper);
        }
        fileList.setModelObject(fileWrappers);
        setModelObject(files);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (getModelObject() != null) {
            setFiles(getModelObject());
        }
    }

    /**
     * @return caption of the control.
     */
    protected String getCaption() {
        return StringUtils.EMPTY;
    }

    /**
     * @return available file types for upload.
     */
    protected List<MimeTypes.MimeType> getAvailableTypes() {
        return MimeTypes.ALL_SUPPORTED_TYPES;
    }

    /**
     * @return TRUE, if element from file list can be deleted.
     */
    protected boolean canBeDeleted() {
        return false;
    }

    /**
     * @return TRUE, if element from file list can be downloaded.
     */
    protected boolean canBeDownloaded() {
        return true;
    }

    /**
     * @return TRUE, if user can upload new file to the list.
     */
    protected boolean canBeUploaded() {
        return true;
    }

    /**
     * @return number of files, which could be uploaded.
     */
    protected int getMaxFiles() {
        return 1;
    }

    /**
     * Function, containing additional operations on delete action.
     */
    protected void onDeleteActions(AjaxRequestTarget target, File file) {

    }

    /**
     * Function, containing additional operations on upload action.
     */
    protected void onUploadActions(AjaxRequestTarget target, File file) {

    }
}