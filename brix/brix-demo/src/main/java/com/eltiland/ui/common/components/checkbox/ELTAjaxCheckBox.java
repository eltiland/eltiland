package com.eltiland.ui.common.components.checkbox;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Ajax check box with caption label.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTAjaxCheckBox extends FormComponentPanel<Boolean> {

    /**
     * Ajax check box component.
     */
    private AjaxCheckBox checkbox = new AjaxCheckBox("checkbox", new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            ELTAjaxCheckBox.this.setModelObject(getModelObject());
            ELTAjaxCheckBox.this.onUpdate(target);
        }
    };

    /**
     * Caption label component.
     */
    private Label caption = new Label("caption", new Model<String>());

    /**
     * Component constructor.
     *
     * @param id           markup id.
     * @param captionModel string model of the caption.
     * @param model        boolean value model.
     */
    public ELTAjaxCheckBox(String id, IModel<String> captionModel, IModel<Boolean> model) {
        super(id, model);
        addComponents();
        caption.setDefaultModel(captionModel);
        checkbox.setModelObject(model.getObject());
    }

    @Override
    protected void onModelChanged() {
        super.onModelChanged();
        checkbox.setModelObject(getModelObject());
    }

    @Override
    protected void convertInput() {
        super.convertInput();
        setConvertedInput(checkbox.getConvertedInput());
    }

    /**
     * OnUpdate callback.
     */
    protected abstract void onUpdate(AjaxRequestTarget target);


    private void addComponents() {
        add(checkbox);
        add(caption);
    }
}
