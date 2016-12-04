package com.eltiland.ui.course.control.data.panel.item;

import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTDateTimeField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Panel for creating webinar item of course.
 *
 * @author Alex Plotnikov
 */
public class WebinarItemPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<WebinarData> {

    private IDialogSimpleNewCallback.IDialogActionProcessor<WebinarData> newCallback;

    private ELTTextField<String> nameField =
            new ELTTextField<String>(
                    "name", new ResourceModel("webinar.item.name"), new Model<String>(), String.class, true) {
                @Override
                protected int getInitialWidth() {
                    return 350;
                }
            };

    private ELTDateTimeField dateField = new ELTDateTimeField("date",
            new ResourceModel("webinar.item.date"), new Model<Date>(), Date.class, true);

    public WebinarItemPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(dateField);
        form.setMultiPart(true);
    }

    @Override
    protected String getHeader() {
        return getString("webinar.header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            WebinarData data = new WebinarData(nameField.getModelObject(), dateField.getModelObject());
            newCallback.process(new Model<>(data), target);
        }
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<WebinarData> callback) {
        newCallback  = callback;
    }

    public String getVariation() {
        return "styled";
    }
}
