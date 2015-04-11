package com.eltiland.ui.common.components.label;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Default implementation of {@link com.eltiland.ui.common.components.label.AbstractEditableLabel}. It uses {@link org.apache.wicket.markup.html.form.TextField} as editor.
 */
public class EmailEditableLabel extends EditableLabel<String> {
    private static final PatternValidator emailValidator =
            new PatternValidator("^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$") {
                @Override
                protected String resourceKey() {
                    return "emailValidator";
                }
            };


    /**
     * Default constructor.
     *
     * @param id
     * @param headerModel
     * @param model
     */
    public EmailEditableLabel(String id, IModel<String> headerModel, IModel<String> model) {
        super(id, headerModel, model, String.class);
    }

    @Override
    protected FormComponent<String> createEditor(IModel<String> parentModel, Class<String> type) {
        TextField<String> textField = new TextField<String>("titleField", parentModel, String.class) {
            @Override
            protected void convertInput() {
                super.convertInput();

                String convertedInput = getConvertedInput();
                if (convertedInput != null) {
                    setConvertedInput(convertEmailInput(getConvertedInput()));
                }
            }
        };
        textField.add(emailValidator);
        return textField;
    }

    public static String convertEmailInput(String email) {
        return email.toLowerCase();
    }
}
