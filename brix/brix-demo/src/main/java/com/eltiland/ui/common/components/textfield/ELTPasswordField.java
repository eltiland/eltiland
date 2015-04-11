package com.eltiland.ui.common.components.textfield;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Text field with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public class ELTPasswordField extends AbstractTextField<String> {


    public ELTPasswordField(String id, IModel<String> headerModel, IModel<String> model) {
        super(id, headerModel, model, String.class);
    }

    public ELTPasswordField(String id, IModel<String> headerModel, IModel<String> model, boolean isRequired) {
        super(id, headerModel, model, String.class, isRequired);
    }

    @Override
    protected FormComponent<String> createEditor(IModel<String> parentModel, Class<String> type) {
        return new PasswordTextField("textField", new Model<>(parentModel.getObject()));
    }
}
