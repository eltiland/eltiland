package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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

    private ELTTextField<String> nameField = new ELTTextField<>(
            "name", new ResourceModel("nameLabel"), new Model<String>(), String.class, true);

    private ELTTextArea descField = new ELTTextArea("description", new ResourceModel("descLabel"), new Model<String>()) {
        @Override
        protected int getMaxLength() {
            return 370;
        }

        @Override
        protected int getInitialHeight() {
            return 60;
        }
    };

    private PriceField priceField = new PriceField("price", new ResourceModel("priceLabel"), new Model<BigDecimal>());

    public WSPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(descField);
        form.add(priceField);
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

    @Override
    public String getVariation() {
        return "styled";
    }
}
