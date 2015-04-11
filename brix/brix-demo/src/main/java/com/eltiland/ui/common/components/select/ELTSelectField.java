package com.eltiland.ui.common.components.select;

import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidationError;

import java.util.List;

/**
 * Drop down field with feedback label.
 *
 * @author Aleksey Plotnikov
 */
public abstract class ELTSelectField<T> extends FormComponentPanel<T> {

    protected DropDownChoice<T> dropDownChoice;
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
    public ELTSelectField(String id, IModel<String> headerModel, IModel<T> model) {
        super(id, model);

        dropDownChoice = new DropDownChoice<>("textField", model, getChoiceListModel(), getChoiceRenderer());
        dropDownChoice.setNullValid(true);
        add(dropDownChoice);
        feedbackLabel = new ELTFeedbackLabel("feedbackLabel", headerModel, dropDownChoice);
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
    public ELTSelectField(String id, IModel<String> headerModel, IModel<T> model, boolean isRequired) {
        this(id, headerModel, model);
        dropDownChoice.setRequired(isRequired);
        asterisk.setVisible(isRequired);
    }

    /**
     * Makes component read-only.
     *
     * @param value read-only value.
     */
    public void setReadonly(boolean value) {
        this.readonly = value;
        dropDownChoice.setEnabled(!value);
        if (value) {
            dropDownChoice.add(new AttributeModifier("class", new Model<>("textField readOnly")));
        } else {
            dropDownChoice.add(new AttributeModifier("class", new Model<>("textField")));
        }
    }

    @Override
    protected void convertInput() {
        if (isReadonly()) {
            setConvertedInput(getModelObject());
        } else {
            setConvertedInput(dropDownChoice.getConvertedInput());
        }
    }

    @Override
    public void error(IValidationError error) {
        super.error(error);
        dropDownChoice.error(error);
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
     * Register custom behaviours on editor component.
     *
     * @param behavior new behaviour
     */
    public void registerEditorBehaviour(Behavior behavior) {
        dropDownChoice.add(behavior);
    }

    public List<? extends T> getChoices() {
        return dropDownChoice.getChoices();
    }

    public void setChoices(List<T> choices) {
        dropDownChoice.setChoices(choices);
    }

    public void setNullValid(boolean isNullValid) {
        dropDownChoice.setNullValid(isNullValid);
    }

    protected abstract IModel<List<T>> getChoiceListModel();

    protected abstract IChoiceRenderer<T> getChoiceRenderer();
}
