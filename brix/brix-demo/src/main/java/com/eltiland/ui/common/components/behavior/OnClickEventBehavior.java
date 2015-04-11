package com.eltiland.ui.common.components.behavior;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.behavior.AttributeAppender;

/**
 * Ajax onclick behaviour with mouse pointer and cursor pointer highlighter styles
 *
 * @author Igor Cherednichenko
 */
public abstract class OnClickEventBehavior extends AjaxEventBehavior {

    public OnClickEventBehavior() {
        super("onclick");
    }

    @Override
    protected void onBind() {
        super.onBind();
        getComponent().add(AttributeAppender.append("class", UIConstants.CLASS_CLICKABLE));
    }
}
