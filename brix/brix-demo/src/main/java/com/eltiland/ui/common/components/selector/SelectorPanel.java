package com.eltiland.ui.common.components.selector;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Selector link panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SelectorPanel extends WebMarkupContainer {
    public SelectorPanel(String id) {
        super(id);

        this.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                changeSelection(target);
            }
        });
        setOutputMarkupId(true);
    }

    public abstract void changeSelection(AjaxRequestTarget target);
}
