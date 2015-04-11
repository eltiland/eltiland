package com.eltiland.ui.common.components.interval;

import com.eltiland.model.IWithInterval;
import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Dialog for editing interval of the IWithInterval item.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTIntervalDialog<T extends IWithInterval & Identifiable>
        extends ELTDialogPanel implements IDialogUpdateCallback<T> {

    private IModel<T> dataModel = new LoadableDetachableModel<T>() {
        @Override
        protected T load() {
            return null;
        }
    };

    private IDialogActionProcessor<T> callback;

    private ELTIntervalField intervalField =
            new ELTIntervalField("interval", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<Interval>());

    private ELTAjaxCheckBox resetField = new ELTAjaxCheckBox("reset", new ResourceModel("reset"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            if (getModelObject()) {
                intervalField.setReadOnly(true);
                intervalField.setModelObject(null);
            } else {
                intervalField.setReadOnly(false);
            }
            target.add(intervalField);
        }
    };

    public ELTIntervalDialog(String id) {
        super(id);
        form.add(intervalField.setOutputMarkupId(true));
        form.add(resetField);
        form.setMultiPart(true);
    }

    public void initPanel(IModel<T> blockModel) {
        this.dataModel = blockModel;
        Date startDate = this.dataModel.getObject().getStartDate();
        Date endDate = this.dataModel.getObject().getEndDate();
        if (startDate == null || endDate == null) {
            intervalField.setModelObject(null);
        } else {
            intervalField.setModelObject(new Interval(startDate.getTime(), endDate.getTime()));
        }
        resetField.setModelObject(false);
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            if (intervalField.getModelObject() == null) {
                dataModel.getObject().setStartDate(null);
                dataModel.getObject().setEndDate(null);
            } else {
                dataModel.getObject().setStartDate(intervalField.getModelObject().getStart().toDate());
                dataModel.getObject().setEndDate(intervalField.getModelObject().getEnd().toDate());
            }
            callback.process(dataModel, target);
        }
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<T> callback) {
        this.callback = callback;
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
