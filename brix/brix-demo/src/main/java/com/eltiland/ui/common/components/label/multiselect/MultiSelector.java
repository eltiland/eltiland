package com.eltiland.ui.common.components.label.multiselect;

import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import java.util.Collection;
import java.util.List;

/**
 * TODO : add docs
 */
public class MultiSelector<T> extends FormComponentPanel<Collection<T>> {

    private boolean readonly = false;
    private String defaultLabelValue = "";

    private final ListMultipleChoice<T> internalSelector;
    private final WebMarkupContainer choiceContainer;
    private final WebMarkupContainer dropDownAddElement;
    private final WebMarkupContainer editButton = new WebMarkupContainer("editButton") {
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(!readonly);
        }
    };


    public MultiSelector(String id,
                         IModel<String> labelModel,
                         IModel<? extends Collection<T>> selectedModel, IModel<List<T>> choiceModel, IChoiceRenderer choiceRenderer) {
        super(id, (IModel<Collection<T>>) selectedModel);
        add(AttributeAppender.append("class", UIConstants.CLASS_EDITABLE_LABEL));
        add(editButton.setOutputMarkupId(true));

        internalSelector = new ListMultipleChoice<T>("internalSelector", selectedModel, choiceModel, choiceRenderer);
        internalSelector.setOutputMarkupId(true);


        add(new ELTFeedbackLabel("titleLabel", labelModel, internalSelector));

        choiceContainer = new WebMarkupContainer("choiceContainer");
        choiceContainer.setOutputMarkupId(true);
        add(choiceContainer);

        dropDownAddElement = new WebMarkupContainer("dropDownAddElement");
        dropDownAddElement.setOutputMarkupId(true);

        add(dropDownAddElement);
        add(internalSelector);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (!isEnabled()) {
            // don't contribute javascript if component is readonly
            return;
        }
        internalSelector.clearInput();         //TODO: possible lost raw data.
        response.renderOnDomReadyJavaScript(String.format("addMultiselectBehavior('%s', '%s', '%s','%s',%s,%s,'%s');",
                internalSelector.getMarkupId(),
                choiceContainer.getMarkupId(),
                dropDownAddElement.getMarkupId(),
                getString("selectDropdownText"),
                isReadonly(),
                isRequired(),
                defaultLabelValue));

        if (isReadonly()) {
            // don't contribute javascript if component is readonly
            return;
        }

        response.renderOnDomReadyJavaScript(String.format("hideDropBoxComponent('%s')",
                dropDownAddElement.getMarkupId()));
        response.renderOnDomReadyJavaScript(String.format("showDropBoxComponent('%s','%s')",
                editButton.getMarkupId(),
                dropDownAddElement.getMarkupId()));

    }

    @Override
    protected void convertInput() {
        if (isReadonly()) {
            setConvertedInput(getModelObject());
        } else {
            setConvertedInput(internalSelector.getConvertedInput());
        }
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        internalSelector.setRequired(isRequired());
    }

    @Override
    protected void onBeforeRender() {
        internalSelector.setModelObject(getModelObject());

        super.onBeforeRender();
    }

    @Override
    public void updateModel() {
        setModelObject(internalSelector.getModelObject());
    }

    public ListMultipleChoice<T> getInternalSelector() {
        return internalSelector;
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
    public MultiSelector<T> setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    /**
     * Set default label value for displaying if component is not required ("Any")
     *
     * @param defaultLabelValue default label value
     */
    public void setDefaultLabelValue(String defaultLabelValue) {
        this.defaultLabelValue = defaultLabelValue;
    }
}
