package com.eltiland.ui.common.components.label;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidationError;

/**
 * Default implementation of {@link AbstractEditableLabel}. It uses {@link TextField} as editor.
 */
public class EditableLabel<T> extends AbstractEditableLabel<T> {
    /**
     * Default constructor.
     *
     * @param id          wicket id
     * @param headerModel header label model
     * @param model       {@link AbstractEditableLabel} model
     */
    public EditableLabel(String id, IModel<String> headerModel, IModel<T> model) {
        super(id, headerModel, model);
    }

    /**
     * Constructor with type parameter.
     *
     * @param id          wicket id
     * @param headerModel header label model
     * @param model       {@link AbstractEditableLabel} model
     * @param type        provide this parameter if label generalized with type other than {@link String}
     */
    public EditableLabel(String id, IModel<String> headerModel, IModel<T> model, Class<T> type) {
        super(id, headerModel, model, type);
    }

    @Override
    protected FormComponent<T> createEditor(IModel<T> parentModel, Class<T> type) {
        return new TextField<T>("titleField", parentModel, type);
    }

    @Override
    public void error(IValidationError error) {
        editor.error(error);
    }

    @Override
    protected String getDisplayValue(T modelObject) {
        if (editor.getConvertedInput() != null) {
            return Strings.toString(editor.getConvertedInput());
        } else {
            return Strings.toString(editor.getModelObject());
        }
    }
}
