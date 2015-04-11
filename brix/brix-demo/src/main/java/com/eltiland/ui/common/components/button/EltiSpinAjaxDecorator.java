package com.eltiland.ui.common.components.button;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxCallDecorator;

/**
 * Decorator shows loading indicator near link.
 */
public class EltiSpinAjaxDecorator implements IAjaxCallDecorator {

    private String componentId;

    /**
     * Default constructor
     *
     * @param component link to bind
     */
    public EltiSpinAjaxDecorator(Component component) {
        this.componentId = component.getMarkupId();
    }

    @Override
    public CharSequence decorateScript(Component component, CharSequence script) {
        return String.format("indicatorShow('%s');", componentId) + script;
    }

    @Override
    public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
        return "indicatorHide();" + script;
    }

    @Override
    public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
        return decorateOnSuccessScript(component, script);
    }
}
