package com.eltiland.ui.common.components.textfield;

import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * Abstract Text field with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public abstract class AbstractTextField<T> extends FormComponentPanel<T> {

    protected FormComponent<T> editorField;
    private ELTFeedbackLabel feedbackLabel;
    private WebMarkupContainer asterisk;
    private boolean readonly;

    /**
     * Component panel constructor.
     *
     * @param id          markup id.
     * @param headerModel header model for feedback label.
     * @param model       data model for text field.
     */
    public AbstractTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type) {
        super(id, model);

        editorField = createEditor(model, type);
        add(editorField);

        int width = getInitialWidth();
        if (width != 0) {
            editorField.add(new AttributeAppender("style", new Model<>(String.format("width:%dpx", width)), ";"));
        }

        feedbackLabel = new ELTFeedbackLabel("feedbackLabel", headerModel, editorField);
        add(feedbackLabel);

        asterisk = new WebMarkupContainer("asteriskRequired");
        add(asterisk.setVisible(false));
    }

    /**
     * Component panel constructor.
     *
     * @param id          markup id.
     * @param headerModel header model for feedback label.
     * @param model       data model for text field.
     * @param isRequired  is value required
     */
    public AbstractTextField(String id, IModel<String> headerModel, IModel<T> model, Class<T> type, boolean isRequired) {
        this(id, headerModel, model, type);
        editorField.setRequired(isRequired);
        asterisk.setVisible(isRequired && showAsterisk());
    }

    /**
     * Makes component read-only.
     *
     * @param value read-only value.
     */
    public void setReadonly(boolean value) {
        readonly = value;
        editorField.setEnabled(!value);
        if (value) {
            editorField.add(new AttributeModifier("class", new Model<>("textField readOnly")));
        } else {
            editorField.add(new AttributeModifier("class", new Model<>("textField")));
        }
    }

    @Override
    protected void convertInput() {
        if (isReadonly()) {
            setConvertedInput(getModelObject());
        } else {
            setConvertedInput(editorField.getConvertedInput());
        }
    }

    @Override
    public void error(IValidationError error) {
        super.error(error);
        editorField.error(error);
    }

    /**
     * Adding maximum length validation.
     *
     * @param maxLength maximum data length
     */
    public void addMaxLengthValidator(final int maxLength) {
        editorField.add(new StringValidator() {
            @Override
            protected void onValidate(IValidatable<String> stringIValidatable) {
                if (stringIValidatable.getValue().length() > maxLength) {
                    editorField.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource iErrorMessageSource) {
                            return String.format(getString("maxLengthError"), maxLength);
                        }
                    });
                }
            }
        });
    }

    /**
     * Adding abstract validation.
     *
     * @param validator validator to add.
     */
    public void addValidator(IValidator<T> validator) {
        editorField.add(validator);
    }

    /**
     * Whether this component is readonly.
     *
     * @return true if component is readonly, else otherwise.
     */
    public boolean isReadonly() {
        return readonly;
    }

    /**
     * Set required flag for editor.
     *
     * @param value if TRUE - field became required.
     */
    public void setValueRequired(boolean value) {
        editorField.setRequired(value);
        asterisk.setVisible(value);
    }

    /**
     * Override to create custom editor component.
     *
     * @param parentModel parent model
     * @return editor instance
     */
    protected abstract FormComponent<T> createEditor(IModel<T> parentModel, Class<T> type);

    /**
     * Register custom behaviours on editor component.
     *
     * @param behavior new behaviour
     */
    public void registerEditorBehaviour(Behavior behavior) {
        editorField.add(behavior);
    }

    @Override
    protected T convertValue(String[] value) throws ConversionException {
        return super.convertValue(value);
    }

    /**
     * Override it to set initial width.
     */
    protected int getInitialWidth() {
        return 0;
    }

    /**
     * Override it to set your own logic of showing asterisk.
     */
    protected boolean showAsterisk() {
        return true;
    }

    /**
     * Set header label.
     *
     * @param headerLabel header label model.
     */
    public void setHeaderLabel(IModel<String> headerLabel) {
        feedbackLabel.setDefaultModel(headerLabel);
    }
}
