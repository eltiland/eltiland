package com.eltiland.ui.common.components.behavior;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * TODO: add docs
 */
public class TooltipBehavior extends Behavior {
    private String cssClass = UIConstants.CSS_TOOLTIP_NORMAL;

    public TooltipBehavior() {
    }

    public TooltipBehavior(String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.renderOnDomReadyJavaScript(String.format("processTitles('#%s')",
                component.getMarkupId()));
        response.renderOnDomReadyJavaScript(String.format("addTextTooltipBehavior('#%s', '%s')",
                component.getMarkupId(), cssClass));
    }
}
