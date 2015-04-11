package com.eltiland.ui.common.components.interval;

import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.Interval;

import java.util.Date;

/**
 * Control for interval input/output.
 *
 * @author Aleksey Plotnikov
 */
public class ELTIntervalField extends FormComponentPanel<Interval> {
    private Label label;
    private IntervalField intervalField = new IntervalField("intervalField", new Model<IntervalData>());

    public ELTIntervalField(String id, IModel<String> headerModel) {
        super(id);
        addComponents(headerModel);
    }

    public ELTIntervalField(String id, IModel<String> headerModel, IModel<Interval> model) {
        super(id, model);
        addComponents(headerModel);
    }

    @Override
    protected void convertInput() {
        if (intervalField.getConvertedInput().getBeginDate() == null ||
                intervalField.getConvertedInput().getEndDate() == null) {
            setConvertedInput(null);
        } else {
            setConvertedInput(new Interval(intervalField.getConvertedInput().getBeginDate().getTime(),
                    intervalField.getConvertedInput().getEndDate().getTime()));
        }
    }

    private void addComponents(IModel<String> headerModel) {
        label = new ELTFeedbackLabel("label", headerModel, intervalField);
        add(label);
        add(intervalField);
    }

    public void setInitialStartDate(Date startDate) {
        intervalField.setInitialStartDate(startDate);
    }

    public void setLimitStartDate(final Date startLimit) {
        intervalField.setLimitStartDate(startLimit);
    }

    public void setStartReadOnly(boolean isReadOnly) {
        intervalField.setStartReadOnly(isReadOnly);
    }

    public void setReadOnly(boolean isReadOnly) {
        intervalField.setStartReadOnly(isReadOnly);
        intervalField.setEndReadOnly(isReadOnly);
    }

    @Override
    protected void onModelChanged() {
        Interval modelObject = getModelObject();
        if (modelObject != null) {
            intervalField.setModelObject(new IntervalData(modelObject.getStart().toDate(),
                    modelObject.getEnd().toDate()));
        } else {
            intervalField.setModelObject(null);
        }
        super.onModelChanged();
    }
}
