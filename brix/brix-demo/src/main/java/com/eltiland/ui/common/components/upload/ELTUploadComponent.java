package com.eltiland.ui.common.components.upload;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUploadCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.components.itemPanel.FileItemPanel;
import com.eltiland.utils.MimeTypes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

/**
 * This component provide ability upload multiple files.
 * This component integrate uploading dialog with previews panels.
 *
 * @author Igor Cherednichenko
 */
public class ELTUploadComponent extends BaseEltilandPanel<Void> {
    public static final String UPLOAD_DIALOG_CONTAINER_ID = "uploadDialog";

    @SpringBean
    FileManager fileManager;

    private boolean readonly = false;

    /**
     * Maximum count of uploaded files.
     */
    private int maxCountUploadedFiles;

    /*
     Fragment for dialog placeholder. This fragment allow override mount point of dialog
     (dialog should be added outside form to prevent multipart header during submit)
    */
    protected Fragment uploadDialogFragment = new Fragment(UPLOAD_DIALOG_CONTAINER_ID, "uploadDialogFragment", this);
    /*
     Default container for upload dialog
     */
    private WebMarkupContainer uploadDialogDefaultContainer = new WebMarkupContainer(UPLOAD_DIALOG_CONTAINER_ID);

    /*
      File upload dialog.
     */
    private Dialog<WrappedELTUploadPanel> uploadFileDialog =
            new Dialog<WrappedELTUploadPanel>("uploadFileDialog", UIConstants.DIALOG_POPUP_WIDTH) {
                @Override
                public WrappedELTUploadPanel createDialogPanel(String id) {
                    return new WrappedELTUploadPanel(id, MimeTypes.ALL_SUPPORTED_TYPES);
                }

                @Override
                public void registerCallback(WrappedELTUploadPanel panel) {
                    super.registerCallback(panel);

                    panel.getInnerPanel().setUploadCallback(new IDialogUploadCallback.IDialogActionProcessor<FileUpload>() {
                        @Override
                        public void process(IModel<FileUpload> uploadedFileModel, AjaxRequestTarget target) {
                            target.add(ELTUploadComponent.this);
                            close(target);

                            //if only one upload allowed, remove existed file and add new one
                            if (maxCountUploadedFiles == 1) {
                                fileListView.getModelObject().clear();
                            }

                            //if already uploaded maximum files, reject new file and display warning
                            if (fileListView.size() >= maxCountUploadedFiles && showMaximumWarning()) {
                                String errorMessage =
                                        new StringResourceModel("uploadedMaximumCountOfFiles", ELTUploadComponent.this, null).getString();
                                ELTAlerts.renderErrorPopup(errorMessage, target);
                                return;
                            }

                            File newFile = fileManager.createFileFromUpload(uploadedFileModel.getObject());
                            fileListView.getModelObject().add(new FileWrapper(newFile.getName(), newFile));
                            target.add(uploadLink);
                            onFileUploaded(target);
                        }
                    });

                }
            };

    private HiddenField<Boolean> uploadFlag = new HiddenField<Boolean>("uploadFlag", new Model<Boolean>()) {
        @Override
        protected void onConfigure() {
            super.onConfigure();

            //check deserialisation state
            if (fileListView.size() == 0) {
                setModelObject(null);
            } else {
                setModelObject(true);
            }
        }
    };

    private EltiAjaxLink<Void> uploadLink = new EltiAjaxLink<Void>("uploadLink") {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!isReadonly() && fileListView.getModelObject().size() < (maxCountUploadedFiles));

        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            uploadFileDialog.show(target);
        }
    };

    private ELTFeedbackLabel uploadLabel =
            new ELTFeedbackLabel("uploadLabel", new ResourceModel("uploadFilesLabel"), this) {
                @Override
                public boolean isVisible() {
                    return showLabel();
                }
            };

    private Label uploadRestrictedLabel =
            new Label("uploadRestrictedLabel", new ResourceModel("uploadedMaximumCountOfFiles")) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(!uploadLink.isVisible() && showMaximumWarning());
                }
            };

    /*
    Uploaded item panels
     */
    private ListView<FileWrapper> fileListView = new ListView<FileWrapper>("fileListView", new FileWrapperModel()) {
        @Override
        protected void populateItem(ListItem<FileWrapper> components) {
            components.add(new FileItemPanel("item", components.getModelObject().file) {
                @Override
                protected void onDelete(AjaxRequestTarget target) {
                    CollectionUtils.filter(fileListView.getModelObject(), new Predicate() {
                        @Override
                        public boolean evaluate(Object object) {
                            FileWrapper wrapper = (FileWrapper) object;

                            // equality by class and id or reference
                            if (wrapper.file.equals(getModelObject())) {
                                // remove from list
                                return false;

                                // if proxy
                            } else if (getModelObject() instanceof HibernateProxy) {
                                // then compare by id
                                return !Objects.equals(getModelObject().getId(), wrapper.file.getId());
                            }
                            // isn't proxy. Skipp...
                            return true;

                        }
                    });
                    onFileDeleted(target);
                    target.add(ELTUploadComponent.this);
                    target.add(uploadLink);
                }

                @Override
                public boolean isReadonly() {
                    return readonly;
                }

                /*                @Override
                protected void onItemTitleChanged(AjaxRequestTarget target, File modelObject, String newLabelName) {
                    for (FileWrapper fileWrapper : fileListView.getModelObject()) {
                        if (fileWrapper.file == getModelObject()) {
                            fileWrapper.name = newLabelName;
                            break;
                        }
                    }
                }*/
            });
        }
    };

    /**
     * Default constructor. Allowed only one uploaded file.
     *
     * @param id wicket id
     */
    public ELTUploadComponent(String id) {
        this(id, 1);
    }

    /**
     * Constructor with specify max count of uploaded files.
     *
     * @param id                    wicket id
     * @param maxCountUploadedFiles maximum count of uploaded files allowed to ulpoad
     */
    public ELTUploadComponent(String id, int maxCountUploadedFiles) {
        super(id);
        this.maxCountUploadedFiles = maxCountUploadedFiles;

        setOutputMarkupId(true);

        //default mount point for upload dialog
        add(uploadDialogDefaultContainer);

        add(uploadLink);
        add(uploadLabel);
        add(uploadFlag);
        add(uploadRestrictedLabel);

        add(fileListView);
    }


    @Override
    protected void onInitialize() {
        super.onInitialize();
        //mount dialog to panel (host panel overridden in addUploadDialog() should be already attached to page)
        uploadDialogFragment.add(uploadFileDialog);
        addUploadDialog();
    }

    /**
     * Sets the required flag
     *
     * @param required required flag
     * @return this component
     */
    public final ELTUploadComponent setRequired(final boolean required) {
        uploadFlag.setRequired(required);
        return this;
    }

    /**
     * Override this method to add dialog component to custom placeholder.
     * By default, uploading dialog added inside this component.
     * <p/>
     * To prevent multipart conflict (if this component added to form)
     * add dialog to other component, using {@link WebMarkupContainer#replace(org.apache.wicket.Component)}}.
     */
    public void addUploadDialog() {
        replace(uploadDialogFragment);
    }

    public void onFileUploaded(AjaxRequestTarget target) {

    }

    public void onFileDeleted(AjaxRequestTarget target) {

    }


    /**
     * Set limit of uploaded files.
     * This method allow adjust max uploaded files after constructor was called.
     *
     * @param maxCountUploadedFiles max count of uploaded files
     */
    public void setMaxCountUploadedFiles(int maxCountUploadedFiles) {
        while (fileListView.getModelObject().size() > maxCountUploadedFiles) {
            fileListView.getModelObject().remove(maxCountUploadedFiles);
        }
        this.maxCountUploadedFiles = maxCountUploadedFiles;
    }

    /**
     * Check if some files was uploaded but was lost due to serialisation.
     *
     * @return true, if file was lost
     */
    public boolean isNewFilesLost() {
        return ((FileWrapperModel) fileListView.getModel()).isNewFilesLost();
    }

    /**
     * Get access to uploaded files. This files are not persisted.
     *
     * @return map of uploaded files (value is modifiable name oploaded file)
     */
    public Map<File, String> getUploadedFilesMap() {
        Map<File, String> map = new HashMap<File, String>();
        for (FileWrapper fileWrapper : fileListView.getModelObject()) {
            map.put(fileWrapper.file, fileWrapper.name);
        }

        return map;
    }

    /**
     * Get access to uploaded files. This files are not persisted.
     *
     * @return list of uploaded files
     */
    public List<File> getUploadedFiles() {
        return new ArrayList<File>(getUploadedFilesMap().keySet());
    }

    /**
     * Set list to uploaded files.
     *
     * @param files list of uploaded files
     */
    public void setUploadedFiles(List<File> files) {
        fileListView.getModelObject().clear();

        for (File file : files) {
            fileListView.getModelObject().add(new FileWrapper(file.getName(), file));
        }
    }

    /**
     * Whether this component is readonly.
     *
     * @return true if component is readonly, else otherwise.
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Set this component to readonly state. This means that user cannot edit.
     *
     * @param readonly is readonly
     * @return {@code this}
     */
    public ELTUploadComponent setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * Inner file and name wrapper entity.
     */
    private class FileWrapper {
        public String name;
        public File file;

        private FileWrapper(String name, File file) {
            this.name = name;
            this.file = file;
        }
    }

    /**
     * Inner smart LoadableDetachableModel with support mixed entity list (existed and new)
     */
    private class FileWrapperModel extends LoadableDetachableModel<List<FileWrapper>> {
        @SpringBean
        GenericManager manager;

        private List<Long> existedFileIds = new ArrayList<Long>();

        private boolean hasNewFiles;
        private transient Set<FileWrapper> newFiles = new HashSet<FileWrapper>();

        private FileWrapperModel() {
            Injector.get().inject(this);
        }

        private FileWrapperModel(List<FileWrapper> object) {
            super(object);
            Injector.get().inject(this);
        }

        @Override
        protected List<FileWrapper> load() {
            List<FileWrapper> result = new ArrayList<FileWrapper>();

            if (newFiles == null) {
                newFiles = new HashSet<FileWrapper>();
            }

            if (existedFileIds.isEmpty() && newFiles.isEmpty()) {
                return result;
            }

            if (!existedFileIds.isEmpty()) {
                List<File> existedFiles = fileManager.getFileListByIds(existedFileIds);
                for (File file : existedFiles) {
                    result.add(new FileWrapper(file.getName(), file));
                }
            }

            result.addAll(newFiles);

            return result;
        }

        @Override
        protected void onDetach() {
            super.onDetach();

            existedFileIds.clear();
            newFiles.clear();
            hasNewFiles = false;

            for (FileWrapper fileWrapper : getObject()) {
                if (fileWrapper.file.getId() != null) {
                    existedFileIds.add(fileWrapper.file.getId());
                } else {
                    newFiles.add(fileWrapper);
                    hasNewFiles = true;
                }
            }
        }

        public boolean isNewFilesLost() {
            if (!isAttached()) {
                getObject();
            }
            return hasNewFiles && newFiles.isEmpty();
        }
    }

    public void setUploadTitle(String title) {
        uploadLabel.setDefaultModel(new Model<>(title));
    }

    /**
     * Wrapper model for file upload panel.
     * Used only for customise design
     */
    private class WrappedELTUploadPanel extends Panel {
        private ELTUploadPanel innerPanel;

        private WrappedELTUploadPanel(String id, List<MimeTypes.MimeType> allowedToUploadFileTypes) {
            super(id);
            innerPanel = new ELTUploadPanel("innerPanel", allowedToUploadFileTypes);
            add(innerPanel);
        }

        protected ELTUploadPanel getInnerPanel() {
            return innerPanel;
        }
    }

    protected boolean showMaximumWarning() {
        return true;
    }

    protected boolean showLabel() {
        return true;
    }
}