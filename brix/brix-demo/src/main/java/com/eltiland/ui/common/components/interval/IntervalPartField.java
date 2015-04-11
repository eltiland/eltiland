package com.eltiland.ui.common.components.interval;

import com.eltiland.ui.common.components.datepicker.DatePickerField;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Date;

/**
 * Control for interval part (begin/end) input/output.
 *
 * @author Aleksey Plotnikov
 */
class IntervalPartField extends FormComponentPanel<Date> {

    private WebMarkupContainer requiredAsterisk = new WebMarkupContainer("asteriskRequired");
    private DatePickerField dateField = new DatePickerField("dateField", new Model<Date>());

    public IntervalPartField(String id, IModel<String> headerModel) {
        super(id);
        addComponents(headerModel);
    }

    public IntervalPartField(String id, IModel<String> headerModel, IModel<Date> model) {
        super(id, model);
        addComponents(headerModel);
    }

    @Override
    protected void convertInput() {
        setConvertedInput(dateField.getConvertedInput());
    }

    private void addComponents(IModel<String> headerModel) {
//        add(requiredAsterisk);
        add(dateField);
        add(new ELTFeedbackLabel("label", headerModel, dateField));
    }

    @Override
    protected void onModelChanged() {
        Date modelObject = getModelObject();
        //if (modelObject != null) {
            dateField.setDefaultModelObject(modelObject);
      //  }
        super.onModelChanged();
    }

    public void setReadOnly(boolean isReadOnly) {
        dateField.setReadOnly(isReadOnly);
    }
}
