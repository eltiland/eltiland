package com.eltiland.ui.faq.plugin.components;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.StringValidator;

import java.util.Arrays;
import java.util.List;

/**
 * Panel for editing string data.
 *
 * @author Aleksey PLotnikov.
 */
public class EditQAPanel extends ELTDialogPanel implements IDialogSimpleUpdateCallback<String> {

    private IDialogSimpleUpdateCallback.IDialogActionProcessor callback;

    private ELTTextArea qaEditor = new ELTTextArea("qaEditor", new ResourceModel("text"), new Model<String>(), true);

    /**
     * Panel constructor.
     *
     * @param id           panel's ID.
     */
    public EditQAPanel(String id) {
        super(id);

        form.setMultiPart(true);

        qaEditor.addMaxLengthValidator(2048);
        form.add(qaEditor);
    }

    public void setData(IModel<String> data) {
        qaEditor.setModelObject(data.getObject());
    }

    @Override
    public void setSimpleUpdateCallback(IDialogActionProcessor<String> callback) {
        this.callback = callback;
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Save);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        switch (event) {
            case Save:
                callback.process(new Model<>(qaEditor.getModelObject()), target);
                break;
        }
    }
}
