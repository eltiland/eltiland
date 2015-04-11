package com.eltiland.ui.common.components.label;

import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * This label can edit it's text.
 *
 * @see AbstractEditableLabel#createEditor(org.apache.wicket.model.IModel, Class)
 * @see AbstractEditableLabel#getDisplayValue(Object)
 */
public abstract class AbstractEditableLabel<T> extends FormComponentPanel<T> {

    /**
     * This CSS class definition is used for correct processing onBlur events
     * for components with enabled wicket autocomplete feature.
     */
    public static final String AUTOCOMPLETE_CSS = "jsAutocompleteCss";

    private boolean readonly = false;
    private String defaultLabelValue = "";

    private final WebMarkupContainer editButton;
    protected final Label titleLabel;
    protected final FormComponent<T> editor;
    protected WebMarkupContainer editorContainer;
    protected WebMarkupContainer titleContainer;

    // would open by default the Editor, instead of
    private boolean isDefaultEditMode = false;
    private WebMarkupContainer requiredAsterisk;
    private ELTFeedbackLabel feedbackLabel;

    // markup variation names
    protected enum MarkupVariation {
        inline("inline"),
        normal("block");

        private String displayStyle;

        private MarkupVariation(String displayStyle) {
            this.displayStyle = displayStyle;
        }

        @Override
        public String toString() {
            if (this == normal) {
                return null;
            }
            return super.toString();
        }

        public String getDisplayStyle() {
            return displayStyle;
        }
    }

    private MarkupVariation markupVariation = MarkupVariation.normal;

    /**
     * Default constructor.
     *
     * @param id          wicket id
     * @param headerModel header label model
     * @param model       {@link AbstractEditableLabel} model
     */
    public AbstractEditableLabel(String id, IModel<String> headerModel, IModel<T> model) {
        this(id, headerModel, model, null);
    }

    /**
     * Constructor with type parameter.
     *
     * @param id          wicket id
     * @param headerModel header label model
     * @param model       {@link AbstractEditableLabel} model
     * @param type        provide this parameter if label generalized with type other than {@link String}
     */
    public AbstractEditableLabel(String id, IModel<String> headerModel, IModel<T> model, Class<T> type) {
        super(id, model);
        add(AttributeAppender.append("class", UIConstants.CLASS_EDITABLE_LABEL));

        editButton = new WebMarkupContainer("editButton");
        add(editButton.setOutputMarkupId(true));

        titleContainer = new WebMarkupContainer("titleContainer");
        titleLabel = new Label("titleLabel", new Model<String>("TITLE"));
        titleContainer.add(titleLabel.setOutputMarkupId(true));
        add(titleContainer.setOutputMarkupId(true));

        requiredAsterisk = new WebMarkupContainer("asteriskRequired");
        add(requiredAsterisk);

        // form and form components
        editor = createEditor(model, type);
        editorContainer = new WebMarkupContainer("editorContainer");
        editorContainer.add(editor.setOutputMarkupId(true));
        add(editorContainer);
        editorContainer.setOutputMarkupId(true);

        add(feedbackLabel = new ELTFeedbackLabel("headerLabel", headerModel, editor));
        feedbackLabel.setOutputMarkupId(true);
    }

    /**
     * Register custom behaviours on editor component.
     *
     * @param behavior new behaviour
     */
    public void registerEditorBehaviour(Behavior behavior) {
        editor.add(behavior);
    }

    @Override
    protected void convertInput() {
        if (isReadonly()) {
            setConvertedInput(getModelObject());
        } else {
            setConvertedInput(editor.getConvertedInput());
        }
    }

    @Override
    protected void onBeforeRender() {
        //redistribute component state to inner components to proper rendering
        editButton.setVisible(!isReadonly());
        editor.setVisible(!isReadonly());

        if (isReadonly()) {
            editor.setConvertedInput(editor.getModelObject());
        }

        if (isEnabled() && (
                !editor.isValid() || isDefaultEditMode() && editor.getConvertedInput() == null)) {
            titleLabel.add(new AttributeModifier("style", "display:none;"));
            editorContainer.add(new AttributeModifier("style", String.format("display:%s;",
                    markupVariation.getDisplayStyle())));
        } else {
            titleLabel.add(new AttributeModifier("style", String.format("display:%s;",
                    markupVariation.getDisplayStyle())));
            editorContainer.add(new AttributeModifier("style", "display:none;"));
        }

        // redistribute 'required' logic
        editor.setRequired(isRequired());
        requiredAsterisk.setVisible(isRequired());

        editor.setModelObject(getModelObject());
        titleLabel.setDefaultModelObject(getDisplayValue(getModelObject()));

        //strongly recommended call at the end override (see javadoc)
        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (isReadonly() || !isEnabled()) {
            // don't contribute javascript if component is readonly
            return;
        }
        // component processor behavior
        response.renderOnDomReadyJavaScript(
                String.format("abstractEditableLabelProcessor('%s','%s','%s',%s,'%s', %s, '%s');",
                        editButton.getMarkupId(),
                        titleLabel.getMarkupId(),
                        editor.getMarkupId(),
                        editor instanceof PasswordTextField,
                        AUTOCOMPLETE_CSS,
                        isRequired(),
                        defaultLabelValue)
        );
    }

    @Override
    public final String getVariation() {
        if (this.markupVariation != null && this.markupVariation.toString() != null) {
            return this.markupVariation.toString();
        }
        return null;
    }

    /**
     * Use this method to set custom markup.
     *
     * @param markupVariation markup variation
     * @see MarkupVariation
     */
    protected void setMarkupVariation(MarkupVariation markupVariation) {
        this.markupVariation = markupVariation;
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
     * Set this component to readonly state. This means that label will be shown only.
     *
     * @param readonly is readonly
     * @return {@code this}
     */
    public AbstractEditableLabel setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * Override to get custom display value.
     *
     * @param modelObject model object
     * @return string representation of model object
     */
    protected String getDisplayValue(T modelObject) {
        return Strings.toString(modelObject);
    }

    /**
     * @return {@code true} if label is showed user in edit mode by default, {@code false} otherwise.
     */
    public boolean isDefaultEditMode() {
        return isDefaultEditMode;
    }

    /**
     * Make this label be showed in edit mode by default.
     *
     * @param defaultEditMode default edit mode, if {@code true} then label will be showed in edit mode
     * @return {@code this}
     */
    public AbstractEditableLabel<T> setDefaultEditMode(boolean defaultEditMode) {
        isDefaultEditMode = defaultEditMode;
        return this;
    }

    /**
     * Change header model of component.
     *
     * @param headerModel new header label model.
     */
    public void setHeaderModel(IModel<String> headerModel) {
        feedbackLabel.setDefaultModel(headerModel);
    }

    /**
     * Set default label value for displaying if component is not required ("Any")
     *
     * @param defaultLabelValue default label value
     */
    public void setDefaultLabelValue(String defaultLabelValue) {
        this.defaultLabelValue = defaultLabelValue;
    }

    /**
     * Adding maximum length validation.
     *
     * @param maxLength maximum data length
     */
    public void addMaxLengthValidator(final int maxLength) {
        editor.add(new StringValidator() {
            @Override
            protected void onValidate(IValidatable<String> stringIValidatable) {
                if (stringIValidatable.getValue().length() > maxLength) {
                    editor.error(new IValidationError() {
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
     * Override to create custom editor component.
     *
     * @param parentModel parent model
     * @param type        provide this parameter if label is generalized with type other than {@link String}
     * @return editor instance
     */
    protected abstract FormComponent<T> createEditor(IModel<T> parentModel, Class<T> type);
}
