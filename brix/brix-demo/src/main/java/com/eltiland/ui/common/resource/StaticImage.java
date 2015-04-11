package com.eltiland.ui.common.resource;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.UrlUtils;

/**
 * @author knorr
 * @version 1.0
 * @since 8/16/12
 */
public class StaticImage extends WebComponent {

    private StaticImage(String id) {
        super(id);
    }


    public StaticImage(String id, String url) {
        super(id, new Model<String>());
        // required here to rewrite correctly to the normal path.
        setImageUrl(url);
    }

    public StaticImage(String id, Long imageFileId, boolean isPreview) {
        super(id);
        setDefaultModel(new Model<String>());
        setImageParams(imageFileId, isPreview);
    }

    public void setImageUrl(String url) {
        setDefaultModelObject(UrlUtils.rewriteToContextRelative(url, RequestCycle.get()));
    }

    public void setImageParams(Long imageId, boolean isPreview) {
        PageParameters parameters = new PageParameters();
        parameters.add(ImageResource.IMAGE_ID_URL_PARAMETER, imageId);
        parameters.add(ImageResource.IMAGE_PREVIEW_INDICATOR, isPreview);
        CharSequence urlForImage = RequestCycle.get().urlFor(new ImageResourceReference(), parameters);
        setDefaultModelObject(urlForImage.toString());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", getDefaultModelObject().toString());
    }

}