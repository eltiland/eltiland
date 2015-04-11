package com.eltiland.ui.course.control.data.panel.item;

import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Internal type element.
 *
 * @author Aleksey Plotnikov.
 */
abstract class TypeItem extends BaseEltilandPanel {

    private WebMarkupContainer container = new WebMarkupContainer("container");

    private boolean selected = false;

    /**
     * Ctor.
     *
     * @param id    markup id.
     * @param clazz type class.
     */
    public TypeItem(String id, Class<? extends ELTCourseItem> clazz) {
        super(id);
        add(container);
        add(new Label("caption", getString(clazz.getSimpleName() + ".class")));
        add(container.setOutputMarkupId(true));
        container.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                selected = !selected;
                container.add(new AttributeModifier("class", selected ? "checkBox selected" : "checkBox unselected"));
                target.add(container);
                onClick(target, selected);
            }
        });
        WebMarkupContainer image = new WebMarkupContainer("image");
        add(image.setOutputMarkupId(true));
        image.add(new AttributeModifier("class", "image " + getImage()));
    }

    protected abstract void onClick(AjaxRequestTarget target, boolean newValue);

    protected abstract String getImage();

    public void reset(AjaxRequestTarget target) {
        container.add(new AttributeModifier("class", "checkBox unselected"));
        target.add(container);
    }
}
