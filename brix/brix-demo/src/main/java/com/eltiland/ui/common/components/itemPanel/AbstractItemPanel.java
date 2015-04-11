package com.eltiland.ui.common.components.itemPanel;

import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.AjaxDownloadLink;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.resource.StaticImage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * Base class for item panels. Provide common functionality and design.
 * This panel represent item name, type, image icon and download functionality.
 * Used for file item and record item panels/
 *
 * @param <T>
 * @author Igor Cherednichenko
 */
public abstract class AbstractItemPanel<T> extends BaseEltilandPanel<T> {

    private boolean readonly = false;

    private AttributeModifier attributeModifierOpenTooltip =
            new AttributeModifier("title", new ResourceModel("open")) {
                @Override
                public boolean isEnabled(Component component) {
                    return component.isEnabled();
                }
            };

    private final AttributeModifier attributeModifierDownloadTooltip =
            new AttributeModifier("title", new ResourceModel("download")) {
                @Override
                public boolean isEnabled(Component component) {
                    return component.isEnabled();
                }
            };

    protected Component typeLabel = createTypeLabel("typeLabel");

    protected Component createTypeLabel(String id) {
        return new Label(id, new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                String itemTypeValue = getItemType();
                if (itemTypeValue == null) {
                    return null;
                }
                return UIConstants.formatShortDescription(itemTypeValue, 16);
            }
        });
    }

    protected Label nameLabel = new Label("nameLabel", new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            return getItemName();
        }
    });

    private final AttributeModifier attributeModifierFullNameTooltip =
            new AttributeModifier("title", nameLabel.getDefaultModel());

    private WebMarkupContainer openLink = createOpenLink("openLink");

    protected WebMarkupContainer createOpenLink(String id) {
        return createDisabledOpenLink(id);
    }

    /**
     * Utility method that create disabled open link.
     *
     * @param id id
     * @return disabled link
     */
    protected final WebMarkupContainer createDisabledOpenLink(String id) {
        return new WebMarkupContainer(id) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                // disable link
                tag.setName("em");
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }

    private AjaxLink<T> downloadLink = new AjaxDownloadLink<T>("downloadLink") {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(isDownloadingAllowed());
        }

        @Override
        public String getFileName() {
            return getDownloadFileName();
        }

        @Override
        public IResourceStream getResourceStream() {
            return getDownloadFileResourceStream();
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return new IAjaxCallDecorator() {

                @Override
                public CharSequence decorateScript(Component component, CharSequence script) {
                    return String.format("$('#%s').addClass('loading');", component.getMarkupId()) + script;
                }

                @Override
                public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
                    return String.format("$('#%s').removeClass('loading');", component.getMarkupId()) + script;
                }

                @Override
                public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
                    return decorateOnSuccessScript(component, script);
                }
            };
        }
    };

    protected EltiAjaxLink<Void> deleteLink =
            new EltiAjaxLink<Void>("deleteLink") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    onDelete(target);
                }

                @Override
                public boolean isVisible() {
                    return !AbstractItemPanel.this.isReadonly();
                }
            };

    /*
    Preview image fragments
     */
    private static final String PREVIEW_IMAGE_CONTAINER = "imageContainer";
    private WebMarkupContainer imageContainer = new WebMarkupContainer(PREVIEW_IMAGE_CONTAINER);

    private Image previewImage = new Image(PREVIEW_IMAGE_CONTAINER, (IResource) null);
    private StaticImage staticPreviewImage = new StaticImage(PREVIEW_IMAGE_CONTAINER, "");

    @Override
    protected void onConfigure() {
        super.onConfigure();

        openLink.setEnabled(getDefaultModelObject() != null);
    }

    /**
     * Default constructor.
     * <p/>
     * Please, update component model by calling {@link org.apache.wicket.Component#setDefaultModelObject(Object)}
     *
     * @param id wicket id
     */
    public AbstractItemPanel(String id) {
        super(id);

        setOutputMarkupId(true);
        add(AttributeModifier.append("class", UIConstants.CLASS_ITEMPANEL));


        openLink.add(imageContainer);
        openLink.add(attributeModifierOpenTooltip);
        openLink.add(new TooltipBehavior());
        add(openLink);
        nameLabel.add(attributeModifierFullNameTooltip);
        nameLabel.add(new TooltipBehavior());
        add(nameLabel);
        add(typeLabel);
        downloadLink.add(attributeModifierDownloadTooltip);
        downloadLink.add(new TooltipBehavior());
        add(downloadLink);
        deleteLink.add(new ConfirmationDialogBehavior());
        add(deleteLink);

    }

    /**
     * Constructor with model initialisation.
     * <p/>
     * Please, update component model by calling {@link org.apache.wicket.Component#setDefaultModelObject(Object)}
     *
     * @param id    wicket id
     * @param model model
     */
    public AbstractItemPanel(String id, IModel<T> model) {
        this(id);
        setDefaultModel(model);
    }

    @Override
    protected void onModelChanged() {
        super.onModelChanged();

        downloadLink.setModel(getModel());

        File previewImageFile = getPreviewImageFile();
        if (previewImageFile == null) {
            //no image
            previewImage.setImageResource(null);
            setVisible(false);
        } else {
            if (previewImageFile.getPreviewBody().getFilename() != null) {
                //standard icon
                staticPreviewImage.setImageUrl(previewImageFile.getPreviewBody().getFilename());
                openLink.replace(staticPreviewImage);
            } else {
                if (previewImageFile.getId() == null) {
                    //transient preview
                    previewImage.setImageResource(new ByteArrayResource(previewImageFile.getType(), previewImageFile.getPreviewBody().getBody()));
                    openLink.replace(previewImage);
                } else {
                    //persistent preview
                    staticPreviewImage.setImageParams(previewImageFile.getId(), true);
                    openLink.replace(staticPreviewImage);
                }
            }
            setVisible(true);
        }
    }

    public boolean isReadonly() {
        return readonly;
    }

    public AbstractItemPanel<T> setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * Override this method for ENABLE downloading mechanism.
     * If return false, download link will be invisible.
     * <p/>
     * Your must override also {@link #getDownloadFileResourceStream()} and {@link #getDownloadFileName()}.
     *
     * @return true, if downloading is enabled
     */
    protected boolean isDownloadingAllowed() {
        return false;
    }

    /**
     * Override this method for downloading mechanism.
     * If return null, download link will be invisible.
     * <p/>
     * Your must override also {@link #getDownloadFileResourceStream()}.
     *
     * @return name of downloaded file
     */
    protected String getDownloadFileName() {
        return null;
    }

    /**
     * Override this method for downloading mechanism.
     * <p/>
     * Your must override also {@link #getDownloadFileName()}.
     *
     * @return resource stream of downloaded file
     */
    protected IResourceStream getDownloadFileResourceStream() {
        return null;
    }

    /**
     * Override this method to react on delete action.
     *
     * @param target ajax target
     */
    protected void onDelete(AjaxRequestTarget target) {
    }

    /**
     * Implement this to get access preview image.
     */
    protected abstract File getPreviewImageFile();

    /**
     * Implement this method for provide type name.
     *
     * @return type name value
     */
    protected abstract String getItemType();

    /**
     * Implement this method for provide item name.
     *
     * @return type name value
     */
    protected abstract String getItemName();
}
