package com.eltiland.ui.course.components.control.panels;

import com.eltiland.model.course.paidservice.CoursePaidTerm;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.googlecode.wicket.jquery.ui.form.slider.Slider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for setting term of the using for paid course element.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseTermPanel extends ELTDialogPanel implements IDialogUpdateCallback<CoursePaidTerm> {

    private IModel<CoursePaidTerm> termModel = new GenericDBModel(CoursePaidTerm.class);

    private final String CSS = "static/css/panels/course_term.css";

    private TextField<Integer> yearField = new TextField<>("year", new Model<Integer>());
    private TextField<Integer> monthField = new TextField<>("month", new Model<Integer>());
    private TextField<Integer> dayField = new TextField<>("day", new Model<Integer>());
    private Slider sliderYear = new Slider("sliderYear", new Model<Integer>(), yearField);
    private Slider sliderMonth = new Slider("sliderMonth", new Model<Integer>(), monthField);
    private Slider sliderDay = new Slider("sliderDay", new Model<Integer>(), dayField);

    private IDialogActionProcessor<CoursePaidTerm> updateCallback;

    public CourseTermPanel(String id) {
        super(id);

        form.add(yearField);
        form.add(monthField);
        form.add(dayField);
        form.add(sliderYear);
        form.add(sliderMonth);
        form.add(sliderDay);

        sliderYear.setMin(0);
        sliderYear.setMax(10);
        sliderYear.setStep(1);

        sliderMonth.setMin(0);
        sliderMonth.setMax(12);
        sliderMonth.setStep(1);

        sliderDay.setMin(0);
        sliderDay.setMax(31);
        sliderDay.setStep(1);
    }

    public void initCreateMode() {
        this.termModel.setObject(new CoursePaidTerm());
        yearField.setModelObject(0);
        monthField.setModelObject(0);
        dayField.setModelObject(0);
        sliderYear.setModelObject(0);
        sliderMonth.setModelObject(0);
        sliderDay.setModelObject(0);
    }

    public void initEditMode(IModel<CoursePaidTerm> termModel) {
        this.termModel = termModel;

        int year = termModel.getObject().getYears();
        int month = termModel.getObject().getMonths();
        int day = termModel.getObject().getDays();

        yearField.setModelObject(year);
        monthField.setModelObject(month);
        dayField.setModelObject(day);
        sliderYear.setModelObject(year);
        sliderMonth.setModelObject(month);
        sliderDay.setModelObject(day);
    }

    @Override
    protected String getHeader() {
        return getString("headerLabel");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            termModel.getObject().setYears(Integer.parseInt((String) ((Object) yearField.getModelObject())));
            termModel.getObject().setMonths(Integer.parseInt((String) ((Object) monthField.getModelObject())));
            termModel.getObject().setDays(Integer.parseInt((String) ((Object) dayField.getModelObject())));
            updateCallback.process(termModel, target);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
    }


    @Override
    public void setUpdateCallback(IDialogActionProcessor<CoursePaidTerm> callback) {
        this.updateCallback = callback;
    }
}
