package com.eltiland.ui.course.control.users.panel;

import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for organization information for listener.
 *
 * @author Aleksey Plotnikov.
 */
public class MessagePanel extends ELTDialogPanel implements IDialogSimpleNewCallback<String> {

    private IDialogActionProcessor<String> callback;

    private ELTTextArea textField = new ELTTextArea("text", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
        @Override
        protected int getInitialHeight() {
            return 300;
        }
    };

    public MessagePanel(String id) {
        super(id);
        form.add(textField);
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Send));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Send)) {
            if (textField.getModelObject() == null || textField.getModelObject().isEmpty()) {
                ELTAlerts.renderErrorPopup(getString("error"), target);
            } else {
                callback.process(new Model<>(textField.getModelObject()), target);
            }
        }
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<String> callback) {
        this.callback = callback;
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }
}
