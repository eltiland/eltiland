package com.eltiland.ui.common.components.avatar;


import com.eltiland.bl.FileManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.ContextRelativeResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author knorr
 * @version 1.0
 * @since 8/1/12
 */
public class AvatarPreviewPanel extends FormComponentPanel<File> {

    private final Logger LOGGER = LoggerFactory.getLogger(AvatarPreviewPanel.class);

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private FileUtility fileUtility;

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private final int initialWidth = 400;

    private Form form = new Form("fixForm");

    private Image avatarPreview;

    private Label avatarLabel = new Label("avatarLabel", new ResourceModel("panelLabel")) {
        @Override
        public boolean isVisible() {
            return isLabelVisible();
        }
    };

    /**
     * Avatar creation dialog.
     */
    private Dialog<CreateAvatarPanel> dialog = new Dialog<CreateAvatarPanel>("changeAvatarDialog", initialWidth) {
        @Override
        public CreateAvatarPanel createDialogPanel(String id) {
            return new CreateAvatarPanel(id) {
                @Override
                public String getAspectRatio() {
                    return AvatarPreviewPanel.this.getAspectRatio();
                }

                @Override
                public String getWidth() {
                    return AvatarPreviewPanel.this.getWidth();
                }

                @Override
                public String getHeight() {
                    return AvatarPreviewPanel.this.getHeight();
                }

                @Override
                protected String getHeader() {
                    return AvatarPreviewPanel.this.getImageHeader();
                }
            };
        }

        @Override
        public void registerCallback(CreateAvatarPanel panel) {
            super.registerCallback(panel);

            panel.setProcessCallback(new IDialogProcessCallback.IDialogActionProcessor<File>() {
                @Override
                public void process(IModel<File> fileModel, AjaxRequestTarget target) {
                    File file = fileModel.getObject();
                    AvatarPreviewPanel.this.setDefaultModel(new TransientReadOnlyModel<File>(file));
                    IResource resource = new ByteArrayResource(file.getType(), file.getPreviewBody().getBody(), file.getName());
                    avatarPreview.setImageResource(resource);
                    target.add(AvatarPreviewPanel.this);
                    close(target);
                }
            });

            panel.setCloseCallback(new IDialogCloseCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    private AbstractLink ajaxLink = new EltiAjaxLink("changeAvatarLink") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            dialog.getDialogPanel().initPanel();
            dialog.show(target);
        }

        @Override
        public boolean isVisible() {
            return isEditLinkVisible();
        }
    };

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(AttributeAppender.append("class", "elti-avatar-preview"));
    }

    /**
     * Construct panel for already created avatar(Use for object editing).
     *
     * @param id
     * @param fileModel
     */
    public AvatarPreviewPanel(String id, IModel<File> fileModel) {
        super(id);
        setOutputMarkupId(true);
        //TODO: double check it
        setDefaultModel(fileModel);
        File avatar = fileModel.getObject();
        if (avatar == null || avatar.getPreviewBody() == null) {
            throw new IllegalArgumentException("Incorrect file passed");
        }
        IResource imageResource;
        if (avatar.getPreviewBody().getBody() != null) {
            imageResource = new ByteArrayResource(avatar.getType(),
                    avatar.getPreviewBody().getBody(), avatar.getName());
        } else {
            imageResource = new ContextRelativeResource(avatar.getPreviewBody().getFilename());
        }
        form.setMultiPart(true);
        add(form);

        WebMarkupContainer descriptionContainer = new WebMarkupContainer("description") {
            @Override
            public boolean isVisible() {
                return isDescriptionVisible();
            }
        };
        form.add(descriptionContainer);

        descriptionContainer.add(avatarLabel.setOutputMarkupId(true));
        form.add(avatarPreview = new Image("avatarPreviewImage", imageResource));
        descriptionContainer.add(ajaxLink);
        form.add(dialog);
        // dialog.setCloseCrossVisible(false);
    }

    /**
     * Construct panel with default image(Use for object creating)
     *
     * @param id   wicket markup id
     * @param icon default icon
     */
    public AvatarPreviewPanel(String id, final UrlUtils.StandardIcons icon) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new GenericDBModel<>(File.class, fileManager.getStandardIconFile(icon)));
        form.setMultiPart(true);

        WebMarkupContainer descriptionContainer = new WebMarkupContainer("description") {
            @Override
            public boolean isVisible() {
                return isDescriptionVisible();
            }
        };
        form.add(descriptionContainer);
        descriptionContainer.add(avatarLabel.setOutputMarkupId(true));
        form.add(avatarPreview = new Image("avatarPreviewImage", new ContextRelativeResource(icon.getPath())));
        descriptionContainer.add(ajaxLink);
        form.add(dialog);
        // dialog.setCloseCrossVisible(false);
        add(form);
    }

    public File getAvatarFile() {
        return (File) AvatarPreviewPanel.this.getDefaultModelObject();
    }

    /**
     * Init edit mode!
     *
     * @param fileModel
     */
    public void initEditMode(IModel<File> fileModel) {
        File avatar = fileManager.getFileById(fileModel.getObject().getId());
        if (avatar == null || avatar.getPreviewBody() == null) {
            throw new IllegalArgumentException("Incorrect file passed");
        }
        IResource imageResource;
        if (avatar.getPreviewBody().getBody() != null) {
            if (outputFullVersion()) {
                try {
                    IResourceStream resourceStream = fileUtility.getFileResource(avatar.getBody().getHash());
                    InputStream stream = resourceStream.getInputStream();
                    imageResource = new ByteArrayResource(avatar.getType(),
                            IOUtils.toByteArray(stream), avatar.getName());
                } catch (IOException | ResourceStreamNotFoundException e) {
                    LOGGER.error("Cannot create image stream", e);
                    throw new WicketRuntimeException("Cannot create image stream", e);
                }
            } else {
                imageResource = new ByteArrayResource(avatar.getType(),
                        avatar.getPreviewBody().getBody(), avatar.getName());
            }
        } else {
            imageResource = new ContextRelativeResource(avatar.getPreviewBody().getFilename());
        }
        avatarPreview.setImageResource(imageResource);
        setDefaultModel(fileModel);
    }

    /**
     * InitCreateMode
     */
    public void initCreateMode(UrlUtils.StandardIcons icon) {
        File standart = fileManager.getStandardIconFile(icon);
        avatarPreview.setImageResource(new ContextRelativeResource(icon.getPath()));
        setDefaultModel(new GenericDBModel<>(File.class, standart));
    }

    public boolean isDataWasLost() {
        if (getDefaultModel() instanceof TransientReadOnlyModel) {
            /*
            Avatar panel has TransientReadOnlyModel as default model only if user has load some file.
            So if default model is TransientReadOnlyModel and getObject() is then loaded image was lost!
            */
            TransientReadOnlyModel model = (TransientReadOnlyModel) getDefaultModel();
            try {
                model.getObject();
                return false;
            } catch (NullPointerException e) {
                return true;
            }
        }
        return false;
    }

    public void setAvatarLabelText(String text) {
        avatarLabel.setDefaultModel(new Model<>(text));
    }

    @Override
    protected void convertInput() {
        setConvertedInput(getModelObject());
    }

    public void setReadOnly(boolean readOnly) {
        ajaxLink.setVisible(readOnly);
    }

    protected boolean isLabelVisible() {
        return true;
    }

    protected boolean isEditLinkVisible() {
        return true;
    }

    protected boolean isDescriptionVisible() {
        return true;
    }

    protected String getImageHeader() {
        return getString("avatarPanelHeader");
    }

    public Label getAvatarLabel() {
        return avatarLabel;
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

    protected boolean outputFullVersion() {
        return false;
    }
}