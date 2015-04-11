package com.eltiland.ui.common.components.button;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * Ajax indicator replacing src of the 16x16 given image (used in buttons) with the spinner while the ajax call is
 * active.
 */
public class ImageReplacerDecorator implements IAjaxCallDecorator {

    private String imageMarkupId;

    /**
     * Constructor.
     *
     * @param imageMarkupId markup (DOM) id of the image element to which replace SRC.
     */
    public ImageReplacerDecorator(String imageMarkupId) {
        this.imageMarkupId = imageMarkupId;
    }

    @Override
    public CharSequence decorateScript(Component component, CharSequence script) {
        return String.format(
                "this.disabled = true; "
                        + "var e = $('#%s');e.hide();e.after('<img class=\"tooltip-loading-marker-class\" src=\"%s\">'); ",
                imageMarkupId,
                RequestCycle.get().urlFor(AbstractDefaultAjaxBehavior.INDICATOR, null)) + script;
    }

    @Override
    public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
        return String.format(
                "this.disabled = false; "
                        + "var e = $('#%s');e.siblings('.tooltip-loading-marker-class').remove();e.show(); ",
                imageMarkupId) + script;
    }

    @Override
    public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
        return decorateOnSuccessScript(component, script);
    }
}
