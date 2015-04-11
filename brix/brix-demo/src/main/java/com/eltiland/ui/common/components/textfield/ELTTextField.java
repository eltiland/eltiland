package com.eltiland.ui.common.components.textfield;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.ConversionException;

/**
 * Text field with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public class ELTTextField<T> extends AbstractTextField<T> {


    public ELTTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type) {
        super(id, headerModel, model, type);
    }

    public ELTTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type, boolean isRequired) {
        super(id, headerModel, model, type, isRequired);
    }

    @Override
    protected FormComponent<T> createEditor(IModel<T> parentModel, Class<T> type) {
        return new TextField<>("textField", parentModel, type);
    }
}
