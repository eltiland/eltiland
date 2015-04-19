package com.eltiland.ui.course.control.listeners.panel;

import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for editing mail to the course listener.
 *
 * @author Aleksey Plotnikov.
 */
public class ListenerMailPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<String> {

    private IDialogActionProcessor<String> callback;

    private ELTTextArea messageField = new ELTTextArea(
            "message", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), true) {
        @Override
        protected int getInitialHeight() {
            return 150;
        }

        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    public ListenerMailPanel(String id) {
        super(id);
        form.add(messageField);
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
            callback.process(new Model<String>(messageField.getModelObject()), target);
        }
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<String> callback) {
        this.callback = callback;
    }
}
