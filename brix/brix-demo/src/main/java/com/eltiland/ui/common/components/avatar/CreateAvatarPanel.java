package com.eltiland.ui.common.components.avatar;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUploadCallback;
import com.eltiland.ui.common.components.image.ImageAreaSelectorPanel;
import com.eltiland.ui.common.components.upload.ELTUploadPanel;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author knorr
 * @version 1.0
 * @since 7/30/12
 */
public class CreateAvatarPanel extends Panel implements IDialogProcessCallback<File>, IDialogCloseCallback {

    public static final List<String> SUPPORTED_FILE_MIMES = Collections.unmodifiableList(Arrays.asList(
            "image/png",
            "image/jpg",
            "image/jpeg"
    ));

    @SpringBean
    private FileManager fileManager;

    @SpringBean
    private FileUtility fileUtility;

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    /**
     * Container for current page fragment.
     */
    private Fragment currentFragment;

    /**
     * Represents empty markup fragment.
     */
    private Fragment emptyFragment;

    /**
     * Fragment, that contains image area selector panel.
     */
    private Fragment resizePanelFragment;

    /**
     * When showed, signals that image for avatar was successfully uploaded.
     */
    private Label statusLabel = new Label("imageIsLoaded", new ResourceModel("imageIsLoaded"));

    private Label createDesc = new Label("createSteps", new ResourceModel("createSteps"));

    private AbstractLink changeLink = new AjaxLink("loadNew") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            if ("resizePanel".equals(currentFragment.getAssociatedMarkupId())) {
                setFragment(getEmptyFragment());
                CreateAvatarPanel.this.setDefaultModel(null);
                uploadPanel.setVisible(true);
                statusLabel.setVisible(false);
                createDesc.setVisible(false);
                changeLink.setVisible(false);
                target.add(CreateAvatarPanel.this);
            }
        }
    };

    private ELTUploadPanel uploadPanel = new ELTUploadPanel("uploadPanel",
            UIConstants.MAX_AVATAR_FILE_SIZE_MB,
            Arrays.asList(MimeTypes.IMAGE),
            fileManager.getSupportedForAvatarSubTypes());

    private Behavior areaSelectorPluginBugFixBehavior = new Behavior() {

        @Override
        public void renderHead(Component component, IHeaderResponse response) {
            String componentId = CreateAvatarPanel.this.getMarkupId();
            response.renderOnDomReadyJavaScript(String.format("AvatarCreatorComponentWorkaround('%s');", componentId));
        }
    };

    public CreateAvatarPanel(String id) {
        super(id);
        setOutputMarkupId(true);

        uploadPanel.setUploadCallback(new IDialogUploadCallback.IDialogActionProcessor<FileUpload>() {
            @Override
            public void process(IModel<FileUpload> uploadedFileModel, AjaxRequestTarget target) {
                //Get uploaded file
                FileUpload upload = uploadedFileModel.getObject();
                /**
                 * Pass uploaded file into the {@link TransientReadOnlyModel} and set this model
                 * as CreateAvatarPanel default model.
                 */
                CreateAvatarPanel.this.setDefaultModel(new TransientReadOnlyModel<FileUpload>(upload));
                /**
                 * When image file successfully uploaded, upload panel became invisible.
                 * And {@link ImageAreaSelectorPanel}, {@link changeLink} {@link statusLabel} is shown.
                 */
                if ("empty".equals(currentFragment.getAssociatedMarkupId())) {
                    setFragment(getResizePanelFragment());
                    uploadPanel.setVisible(false);
                    statusLabel.setVisible(true);
                    createDesc.setVisible(true);
                    changeLink.setVisible(true);
                    target.add(CreateAvatarPanel.this);
                }
            }
        });
        currentFragment = getEmptyFragment();

//        add(buttonClose);
        add(currentFragment);
        add(statusLabel);
        add(createDesc);
        add(changeLink);
        add(uploadPanel);

        add(new Label("avatarHeader", getHeader()));

        initPanel();
    }

    public void initPanel() {
        createDesc.setVisible(false);
        statusLabel.setVisible(false);
        changeLink.setVisible(false);
        uploadPanel.setVisible(true);
        setFragment(getEmptyFragment());
    }

    private Fragment getResizePanelFragment() {
        if (resizePanelFragment == null) {
            resizePanelFragment = new Fragment("fragment", "resizePanel", this);
            /**
             * Get uploaded file model, from the CreateAvatarPanel!
             * Notice when {@link getResizePanelFragment()} is called, that means, that file has been already
             * uploaded, And so TransientReadOnlyModel with uploaded file is already putted as default model
             * of the CreateAvatarPanel.
             */
            FileUpload upload = (FileUpload) CreateAvatarPanel.this.getDefaultModelObject();

            ImageAreaSelectorPanel areaSelectorPanel = new ImageAreaSelectorPanel("panel",
                    new TransientReadOnlyModel<>(upload)) {


                @Override
                public void doProcessCreateImagePreview(FileUpload uploadedFile,
                                                        ByteArrayOutputStream imagePreviewOutputStream,
                                                        AjaxRequestTarget target) {
                    File file = new File();
                    file.setName(uploadedFile.getClientFileName());
                    file.setSize(uploadedFile.getSize());
                    file.setType(uploadedFile.getContentType());

                    FileBody fileBody = new FileBody();
                    fileBody.setHash(fileUtility.saveTemporalFile(uploadedFile.getBytes()));

                    FileBody previewBody = new FileBody();
                    previewBody.setBody(imagePreviewOutputStream.toByteArray());

                    file.setPreviewBody(previewBody);
                    file.setBody(fileBody);
                    //Set callback, which will be called, when user finished cutting image. And submit the form.
                    if (processCallback != null) {
                        processCallback.process(new TransientReadOnlyModel<>(file), target);
                    }
                }

                @Override
                public String getAspectRatio() {
                    return CreateAvatarPanel.this.getAspectRatio();
                }

                @Override
                public String getWidth() {
                    return CreateAvatarPanel.this.getWidth();
                }

                @Override
                public String getHeight() {
                    return CreateAvatarPanel.this.getHeight();
                }

                @Override
                public float getQuality() {
                    return CreateAvatarPanel.this.getQuality();
                }


            };
            //Add JQuery image area selector plugin workaround.
            areaSelectorPanel.add(areaSelectorPluginBugFixBehavior);
            resizePanelFragment.add(areaSelectorPanel);
        } else {
            /**
             * If {@link ImageAreaSelectorPanel} was already created, then find her, and pass a parameter 
             * {@link TransientReadOnlyModel} with new uploaded file.
             */
            resizePanelFragment.visitChildren(ImageAreaSelectorPanel.class, new IVisitor<Component, ImageAreaSelectorPanel>() {
                @Override
                public void component(Component component, IVisit<ImageAreaSelectorPanel> visit) {
                    FileUpload upload = (FileUpload) CreateAvatarPanel.this.getDefaultModelObject();
                    component.setDefaultModel(new TransientReadOnlyModel<FileUpload>(upload));
                    visit.stop();
                }
            });
        }
        return resizePanelFragment;
    }

    private Fragment getEmptyFragment() {
        if (emptyFragment == null) {
            emptyFragment = new Fragment("fragment", "empty", this);
        }
        return emptyFragment;
    }

    private void setFragment(Fragment fragment) {
        currentFragment.replaceWith(fragment);
        currentFragment = fragment;
    }


    IDialogProcessCallback.IDialogActionProcessor<File> processCallback;

    @Override
    public void setProcessCallback(IDialogProcessCallback.IDialogActionProcessor<File> callback) {
        processCallback = callback;
    }

    IDialogCloseCallback.IDialogActionProcessor processCloseCallback;

    @Override
    public void setCloseCallback(IDialogCloseCallback.IDialogActionProcessor callback) {
        processCloseCallback = callback;
    }

    public String getAspectRatio() {
        return eltilandProps.getProperty("avatar.aspect.ratio");
    }

    public String getWidth() {
        return eltilandProps.getProperty("avatar.width");
    }

    public String getHeight() {
        return eltilandProps.getProperty("avatar.height");
    }

    protected String getHeader() {
        return getString("avatarPanelHeader");
    }

    protected float getQuality() {
        return 1;
    }
}
