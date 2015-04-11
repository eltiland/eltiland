package com.eltiland.ui.common.components.textfield;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Text area with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public class ELTTextArea extends AbstractTextField<String> {
    public ELTTextArea(String id, IModel<String> headerModel, IModel<String> model) {
        super(id, headerModel, model, String.class);
    }

    public ELTTextArea(String id, IModel<String> headerModel, IModel<String> model, boolean isRequired) {
        super(id, headerModel, model, String.class, isRequired);
    }

    @Override
    protected FormComponent<String> createEditor(IModel<String> parentModel, Class<String> type) {
        TextArea area = new TextArea<>("textField", parentModel);
        if (isFillToWidth()) {
            area.add(new AttributeAppender("style", new Model<>("width:95%"), ";"));
        }
        if (getMaxLength() != 0) {
            area.add(new AttributeAppender("style",
                    new Model<>(String.format("max-width: %dpx", getMaxLength())), ";"));
        }
        int height = (getInitialHeight() == 0) ? 50 : getInitialHeight();
        area.add(new AttributeAppender("style", new Model<>(String.format("height: %dpx", height)), ";"));

        return area;
    }

    protected boolean isFillToWidth() {
        return false;
    }

    protected int getMaxLength() {
        return 0;
    }

    protected int getInitialHeight() {
        return 0;
    }
}
