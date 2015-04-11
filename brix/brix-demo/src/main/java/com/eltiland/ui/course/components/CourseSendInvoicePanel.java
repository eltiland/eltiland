package com.eltiland.ui.course.components;

import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dialog for sending invoice for accesss the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseSendInvoicePanel extends ELTDialogPanel implements IDialogConfirmCallback {

    private IDialogActionProcessor callback;

    public CourseSendInvoicePanel(String id) {
        super(id);
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
            callback.process(target);
        }
    }

    @Override
    public void setConfirmCallback(IDialogActionProcessor callback) {
        this.callback = callback;
    }
}
