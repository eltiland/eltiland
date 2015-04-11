package com.eltiland.ui.common.components.button;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

/**
 * Implementation of {@link AjaxLink} supported indicator.
 *
 * @param <T> type of model object
 */
public abstract class EltiAjaxLink<T> extends AjaxLink<T> {

    private EltiLoadingAjaxDecorator decorator = new EltiLoadingAjaxDecorator(this);

    /**
     * Default constructor.
     *
     * @param id component id
     */
    public EltiAjaxLink(String id) {
        super(id);
        add(AttributeModifier.append("class", UIConstants.CLASS_LINKBUTTON));
    }

    /**
     * Constructor with model.
     *
     * @param id component id
     */
    public EltiAjaxLink(String id, IModel<T> model) {
        this(id);
        setModel(model);
    }

    @Override
    protected IAjaxCallDecorator getAjaxCallDecorator() {
        return decorator;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        if (tag.getName().equals("a")) {
            tag.getAttributes().put("href", "javascript:");
        }
    }
}
