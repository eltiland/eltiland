package com.eltiland.ui.common.components.video;

import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;

/**
 * YouTube video player with field-link
 *
 * @author Aleksey Plotnikov
 */
public class YoutubeLinkVideoPlayer extends FormComponentPanel<String> {

    // Youtube links.
    private static final String YOUTUBE_HTTPS_URL = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_HTTP_URL = "http://www.youtube.com/watch?v=";
    private static final String YOUTUBE_SHORT_URL = "http://youtu.be/";

    private static final int MAX_LEN = 1024;

    private ELTTextField<String> linkField =
            new ELTTextField<String>("link", getHeaderModel(), new Model<String>(), String.class, isRequiredField()) {
                @Override
                protected int getInitialWidth() {
                    return YoutubeLinkVideoPlayer.this.getInitialWidth();
                }
            };

    private IModel<String> linkModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            String object = linkField.getModelObject();
            if (object == null) {
                return null;
            } else {
                if (object.contains(YOUTUBE_HTTPS_URL)) {
                    return object.substring(YOUTUBE_HTTPS_URL.length());
                } else if (object.contains(YOUTUBE_HTTP_URL)) {
                    return object.substring(YOUTUBE_HTTP_URL.length());
                } else if (object.contains(YOUTUBE_SHORT_URL)) {
                    return object.substring(YOUTUBE_SHORT_URL.length());
                } else {
                    return null;
                }
            }
        }
    };

    private YoutubeVideoPlayer playerField = new YoutubeVideoPlayer("player", linkModel) {
        @Override
        protected void onBeforeRender() {
            super.onBeforeRender();
            linkModel.detach();
            if (linkModel.getObject() != null && !(linkModel.getObject().isEmpty())) {
                add(new AttributeModifier("title", linkModel.getObject()));
                add(new AttributeModifier("class", "youtubePlayer"));
            } else {
                add(new AttributeModifier("title", ""));
                add(new AttributeModifier("class", ""));
            }
        }
    };

    /**
     * Default constructor.
     *
     * @param id markup id.
     */
    public YoutubeLinkVideoPlayer(String id) {
        super(id);
        addComponents();
    }

    /**
     * Constructor with model.
     *
     * @param id    markup id.
     * @param model panel model.
     */
    public YoutubeLinkVideoPlayer(String id, IModel<String> model) {
        super(id, model);
        addComponents();
    }

    @Override
    protected void onModelChanged() {
        String modelObject = getModelObject();
        if (modelObject != null) {
            linkField.setModelObject(YOUTUBE_HTTP_URL + modelObject);
        } else {
            linkField.setModelObject(null);
        }
        linkModel.detach();
        super.onModelChanged();
    }

    @Override
    protected void convertInput() {
        setConvertedInput((String) playerField.getDefaultModelObject());
    }

    /**
     * @return model of header for the link field
     */
    protected IModel<String> getHeaderModel() {
        return new ResourceModel("linkHeader");
    }

    /**
     * @return TRUE, if field is required.
     */
    protected boolean isRequiredField() {
        return false;
    }

    private void addComponents() {
        add(linkField);
        add(playerField);

        linkField.registerEditorBehaviour(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(playerField);
            }
        });

        linkField.add(new IValidator<String>() {
            @Override
            public void validate(IValidatable<String> validatable) {
                String link = linkField.getConvertedInput();

                if (!(link.startsWith(YOUTUBE_HTTPS_URL) ||
                        link.startsWith(YOUTUBE_HTTP_URL) || link.startsWith(YOUTUBE_SHORT_URL))) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("linkError");
                        }
                    });
                }
            }
        });

        linkField.addMaxLengthValidator(MAX_LEN);
    }

    public String getVideoObject() {
        return (String) playerField.getDefaultModelObject();
    }

    protected int getInitialWidth() {
        return 0;
    }
}
