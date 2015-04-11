package com.eltiland.ui.library.panels.filter;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Abstract filter item panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractFilterItemPanel<T> extends BaseEltilandPanel {
    /**
     * Panel constrctor.
     *
     * @param id     markup id.
     * @param object filter object.
     * @param status status (checked/not)
     */
    public AbstractFilterItemPanel(String id, T object, final boolean status) {
        super(id);

        WebMarkupContainer notCheckedContainer = new WebMarkupContainer("notCheckedContainer") {
            @Override
            public boolean isVisible() {
                return !status;
            }
        };
        WebMarkupContainer checkedContainer = new WebMarkupContainer("checkedContainer") {
            @Override
            public boolean isVisible() {
                return status;
            }
        };

        notCheckedContainer.add(new Label("name", getName(object)));
        notCheckedContainer.add(new Label("count", String.valueOf(getCount(object))));

        notCheckedContainer.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onClick(target);
            }
        });

        checkedContainer.add(new Label("name", getName(object)));

        checkedContainer.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onClean(target);
            }
        });

        add(notCheckedContainer.setOutputMarkupPlaceholderTag(true));
        add(checkedContainer.setOutputMarkupPlaceholderTag(true));
    }

    /**
     * @return name of filter item.
     */
    protected abstract IModel<String> getName(T object);

    /**
     * @return count of items, corresponding to given filter.
     */
    protected abstract int getCount(T object);

    /**
     * onClick handler.
     */
    protected abstract void onClick(AjaxRequestTarget target);
    protected abstract void onClean(AjaxRequestTarget target);
}
