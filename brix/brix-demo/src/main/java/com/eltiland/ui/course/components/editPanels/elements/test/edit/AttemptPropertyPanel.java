package com.eltiland.ui.course.components.editPanels.elements.test.edit;

import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for editing attempts limit for test.
 *
 * @author Aleksey PLotnikov.
 */
public class AttemptPropertyPanel extends ELTDialogPanel implements IDialogSimpleUpdateCallback<Integer> {

    private IDialogActionProcessor<Integer> updateCallback;

    private ELTTextField<Integer> limitField = new ELTTextField<>(
            "limitField", new ResourceModel("limitField"), new Model<Integer>(), Integer.class, true);

    private ELTAjaxCheckBox resetField = new ELTAjaxCheckBox(
            "resetLimitField", new ResourceModel("resetLimit"), new Model<Boolean>()) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            boolean value = getModelObject();

            if (value) {
                limitField.setModelObject(0);
            }
            limitField.setReadonly(value);
            target.add(limitField);
        }
    };

    public AttemptPropertyPanel(String id) {
        super(id);
        form.add(limitField.setOutputMarkupId(true));
        form.add(resetField);
    }

    public void initData(int value) {
        resetField.setModelObject(value == 0);
        limitField.setReadonly(value == 0);
        limitField.setModelObject(value);
    }

    @Override
    protected String getHeader() {
        return getString("attemptHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            updateCallback.process(new Model<>(limitField.getModelObject()), target);
        }
    }

    @Override
    public void setSimpleUpdateCallback(IDialogActionProcessor<Integer> callback) {
        this.updateCallback = callback;
    }
}
