package com.eltiland.ui.common.components.textfield;

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 * Text field with feedback label and Email validator.
 *
 * @author Aleksey Plotnikov
 */
public class ELTTextEmailField extends ELTTextField<String> {
    private static final PatternValidator emailValidator =
            new PatternValidator("^[-a-z0-9!#$%&'*+/=?^_`{|}~]+(?:\\.[-a-z0-9!#$%&'*+/=?^_`{|}~]+)*@(?:[a-z0-9]([-a-z0-9]{0,61}[a-z0-9])?\\.)*(?:aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|[a-z][a-z])$") {
                @Override
                protected String resourceKey() {
                    return "emailValidator";
                }
            };


    public ELTTextEmailField(String id, IModel<String> headerModel, IModel<String> model) {
        super(id, headerModel, model, String.class);
        editorField.add(emailValidator);
    }

    public ELTTextEmailField(String id, IModel<String> headerModel, IModel<String> model, boolean isRequired) {
        super(id, headerModel, model, String.class, isRequired);
        editorField.add(emailValidator);
    }
}
