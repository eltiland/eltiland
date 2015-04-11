package com.eltiland.ui.worktop.simple.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Selector link for simple user panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class SelectorLink extends BaseEltilandPanel {
    /**
     * Link constructor.
     *
     * @param id markup id.
     */
    public SelectorLink(String id) {
        super(id);

        add(new Label("label", getLabelText()));

        WebMarkupContainer iconContainer = new WebMarkupContainer("icon");
        add(iconContainer);
        iconContainer.add(new AttributeAppender("class", getIconModelClass(), " "));

        this.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                changeSelection(target);
            }
        });
        setOutputMarkupId(true);
    }

    public abstract void changeSelection(AjaxRequestTarget target);

    public abstract String getLabelText();

    public abstract IModel<String> getIconModelClass();
}
