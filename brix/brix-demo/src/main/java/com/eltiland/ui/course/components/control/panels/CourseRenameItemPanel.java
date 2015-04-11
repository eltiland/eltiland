package com.eltiland.ui.course.components.control.panels;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

/**
 * Panel for renaming course item.
 *
 * @author Aleksey PLotnikov.
 */
public class CourseRenameItemPanel extends BaseEltilandPanel<String> implements IDialogSimpleUpdateCallback<String> {

    private IDialogActionProcessor<String> callback;

    private ELTTextField<String> elementName =
            new ELTTextField<>("elementName", new ResourceModel("elementNameLabel"),
                    new Model<String>(), String.class, true);

    public CourseRenameItemPanel(String id, IModel<String> stringIModel) {
        super(id, stringIModel);

        elementName.setModelObject(stringIModel.getObject());

        Form form = new Form("form");
        add(form);
        form.add(elementName);
        form.add(new FormRequired("required"));

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                callback.process(new Model<>(elementName.getModelObject()), target);
            }
        });
    }

    @Override
    public void setSimpleUpdateCallback(IDialogActionProcessor<String> callback) {
        this.callback = callback;
    }
}
