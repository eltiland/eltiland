package com.eltiland.ui.common.components.video;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

/**
 * YouTube video player. Provide video id through model object.
 * Supports model object changing.
 *
 * @author Alexander Litvinenko
 */
public class YoutubeVideoPlayer extends WebComponent {

    /**
     * @see WebComponent#WebComponent(String)
     */
    public YoutubeVideoPlayer(String id) {
        super(id);
    }

    /**
     * Constructor with model.
     *
     * @param id    component's id
     * @param model model with video id
     */
    public YoutubeVideoPlayer(String id, IModel<String> model) {
        super(id, model);
        if (model.getObject() != null && !(model.getObject().isEmpty())) {
            add(new AttributeAppender("title", model));
            add(new AttributeAppender("class", "youtubePlayer"));
        }
    }

    public void initData(IModel<String> model) {
        if (model.getObject() != null && !(model.getObject().isEmpty())) {
            add(new AttributeModifier("title", model));
            add(new AttributeModifier("class", "youtubePlayer"));
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setOutputMarkupId(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript(String.format("createYouTubePlayers()"));
    }
}
