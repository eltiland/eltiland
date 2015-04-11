package com.eltiland.ui.common.components.button;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxCallDecorator;

/**
 * Decorator shows loading indicator near link.
 */
public class EltiLoadingAjaxDecorator implements IAjaxCallDecorator {

    /**
     * Default constructor
     *
     * @param component link to bind
     */
    public EltiLoadingAjaxDecorator(Component component) {
        component.add(AttributeModifier.append("class", UIConstants.CLASS_INDICATOR_PLACEHOLDER));
    }

    @Override
    public CharSequence decorateScript(Component component, CharSequence script) {
        return String.format("showLoading('#%s', '%s');", component.getMarkupId(), UIConstants.CLASS_INDICATOR)
                + script;
    }

    @Override
    public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
        return String.format("hideLoading('#%s');", component.getMarkupId())
                + script;
    }

    @Override
    public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
        return decorateOnSuccessScript(component, script);
    }
}
