package com.eltiland.ui.common.components.textfield.suggest;

import com.eltiland.ui.common.components.textfield.AbstractTextField;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * Suggest text field.
 *
 * @author Aleksey Plotnikov
 */
public abstract class SuggestTextField<T> extends AbstractTextField<T> {
    /**
     * This CSS class definition is used for correct processing onBlur events
     * for components with enabled wicket autocomplete feature.
     */
    public static final String AUTOCOMPLETE_CSS = "jsAutocompleteCss";

    public SuggestTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type) {
        super(id, headerModel, model, type);
    }

    public SuggestTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type, boolean isRequired) {
        super(id, headerModel, model, type, isRequired);
    }

    @Override
    protected FormComponent<T> createEditor(IModel<T> parentModel, Class<T> type) {
        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setCssClassName(AUTOCOMPLETE_CSS);
        return new AutoCompleteTextField<T>("textField", parentModel, autoCompleteSettings) {
            @Override
            protected Iterator<T> getChoices(String input) {
                return SuggestTextField.this.getChoices(input);
            }
        };
    }

    public abstract Iterator<T> getChoices(String input);
}
