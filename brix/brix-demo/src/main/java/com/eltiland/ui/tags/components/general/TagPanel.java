package com.eltiland.ui.tags.components.general;

import com.eltiland.model.tags.Tag;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Panel for output one tag.
 *
 * @author Aleksey Plotnikov.
 */
public class TagPanel extends BaseEltilandPanel<Tag> {

    private boolean value;
    private Label name = new Label("name", getModelObject().getName());

    public TagPanel(String id, IModel<Tag> tagIModel, boolean value, boolean isClickable) {
        super(id, tagIModel);
        this.value = value;

        add(name);
        changeSelection(value);

        if (isClickable) {
            name.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget ajaxRequestTarget) {
                    TagPanel.this.value = !TagPanel.this.value;
                    changeSelection(TagPanel.this.value);
                    ajaxRequestTarget.add(name);
                    onClick(ajaxRequestTarget, TagPanel.this.value);
                }
            });
        }
    }

    private void changeSelection(boolean selected) {
        name.add(new AttributeModifier("class", new Model<>(selected ? "name selected" : "name")));
    }

    protected void onClick(AjaxRequestTarget target, boolean value) {

    }
}
