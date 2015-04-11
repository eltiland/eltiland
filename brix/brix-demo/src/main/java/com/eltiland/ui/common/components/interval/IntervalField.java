package com.eltiland.ui.common.components.interval;

import com.eltiland.utils.DateUtils;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Control for interval input/output.
 *
 * @author Aleksey Plotnikov
 */
class IntervalField extends FormComponentPanel<IntervalData> {

    private IntervalPartField beginField = new IntervalPartField("beginField",
            new ResourceModel("beginLabel"), new Model<Date>());
    private IntervalPartField endField = new IntervalPartField("endField",
            new ResourceModel("endLabel"), new Model<Date>());

    public IntervalField(String id) {
        super(id);
        addComponents();
    }

    public IntervalField(String id, IModel<IntervalData> model) {
        super(id, model);
        addComponents();
    }

    @Override
    protected void convertInput() {
        // check if data are entered and start day is a present day.
        // then we must set start from current moment.
        if (beginField.getConvertedInput() != null) {
            Calendar nowCalendar = Calendar.getInstance();
            Calendar dateCalendar = Calendar.getInstance();
            nowCalendar.setTime(DateUtils.getCurrentDate());
            dateCalendar.setTime(beginField.getConvertedInput());

            if ((nowCalendar.get(Calendar.DAY_OF_YEAR) == dateCalendar.get(Calendar.DAY_OF_YEAR))
                    && dateCalendar.before(nowCalendar)) {
                beginField.setConvertedInput(DateUtils.getCurrentDate());
            }
        }

        setConvertedInput(new IntervalData(beginField.getConvertedInput(), endField.getConvertedInput()));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
    }

    public void setLimitStartDate(final Date startLimit) {
        add(new AbstractValidator<Interval>() {
            @Override
            protected void onValidate(IValidatable<Interval> validatable) {
                Date beginDate = beginField.getConvertedInput();

                if (beginDate != null && beginDate.before(startLimit)) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return String.format(getString("intervalBeforeNowError"), DateUtils.formatDate(startLimit));
                        }
                    });
                }

            }
        });
    }

    private void addComponents() {
        add(beginField);
        add(endField);

        add(new AbstractValidator<Interval>() {
            @Override
            protected void onValidate(IValidatable<Interval> validatable) {
                Date beginDate = beginField.getConvertedInput();
                Date endDate = endField.getConvertedInput();

                if ((beginDate != null) && (endDate != null)) {
                    if (beginDate.after(endDate)) {
                        validatable.error(new IValidationError() {
                            @Override
                            public String getErrorMessage(IErrorMessageSource messageSource) {
                                return getString("intervalError");
                            }
                        });
                    }
                }
                if ((beginDate == null && endDate != null) || (beginDate != null && endDate == null)) {
                    validatable.error(new IValidationError() {
                        @Override
                        public String getErrorMessage(IErrorMessageSource messageSource) {
                            return getString("intervalEmptyError");
                        }
                    });
                }
            }
        });
    }

    public void setInitialStartDate(Date startDate) {
        beginField.setModelObject(startDate);
    }

    @Override
    protected void onModelChanged() {
        IntervalData modelObject = getModelObject();
        if (modelObject != null) {
            beginField.setModelObject(modelObject.getBeginDate());
            endField.setModelObject(modelObject.getEndDate());
        } else {
            beginField.setModelObject(null);
            endField.setModelObject(null);
        }
        super.onModelChanged();
    }

    public void setStartReadOnly(boolean isReadOnly) {
        beginField.setReadOnly(isReadOnly);
    }

    public void setEndReadOnly(boolean isReadOnly) {
        endField.setReadOnly(isReadOnly);
    }
}
