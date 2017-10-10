package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Webinar subscripion property panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WSPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<WebinarSubscription>, IDialogUpdateCallback<WebinarSubscription> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WSPropertyPanel.class);

    private boolean mode;

    private IDialogUpdateCallback.IDialogActionProcessor<WebinarSubscription> updateCallback;
    private IDialogNewCallback.IDialogActionProcessor<WebinarSubscription> newCallback;

    public WSPropertyPanel(String id) {
        super(id);
    }

    public void initCreateMode() {
        mode = false;
    }

    public void initEditMode(WebinarSubscription webinarSubscription) {
        mode = true;
    }

    @Override
    protected String getHeader() {
        return getString(mode ? "editHeader" : "createHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {

    }

    @Override
    protected boolean actionSelector(EVENT event) {
        if (event.equals(EVENT.Create)) {
            return !mode;
        } else {
            return mode;
        }
    }


    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<WebinarSubscription> callback) {
        this.newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<WebinarSubscription> callback) {
        this.updateCallback = callback;
    }
}
