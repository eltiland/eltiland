package com.eltiland.ui.common.components.textfield.styled;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Styled panel realization for email field.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTEmailPanel extends ELTStyledPanel<String> {
    private static final PatternValidator emailValidator =
            new PatternValidator("^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$") {
                @Override
                protected String resourceKey() {
                    return "emailValidator";
                }
            };

    public ELTEmailPanel(String id) {
        super(id);
    }

    @Override
    protected FormComponent<String> getEditor(String markupId) {
        TextField textField = new TextField<>(markupId, new Model<String>());
        textField.add(emailValidator);
        return textField;
    }
}
