package com.eltiland.ui.course.control.general;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.interval.ELTIntervalField;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTDateField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Panel for editing general information.
 *
 * @author Aleksey Plotnikov.
 */
class DataTab extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;

    ELTTextArea nameField = new ELTTextArea("nameField", new ResourceModel("name.label"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };
    ELTTextEmailField supportField = new ELTTextEmailField(
            "supportEmail", new ResourceModel("support.label"), new Model<String>()) {
        @Override
        protected int getInitialWidth() {
            return 477;
        }
    };
    PriceField priceField = new PriceField("priceField", new ResourceModel("price.label"), new Model<BigDecimal>());

    ELTTextField<Long> daysField = new ELTTextField<Long>(
            "daysField", new ResourceModel("days"), new Model<Long>(), Long.class) {
        @Override
        protected int getInitialWidth() {
            return 477;
        }
    };

    private ELTIntervalField intervalField =
            new ELTIntervalField("interval", new ResourceModel("interval"), new Model<Interval>());

    private ELTDateField joinField = new ELTDateField("join", new ResourceModel("join"), new Model<Date>()) {
        @Override
        protected int getInitialWidth() {
            return 440;
        }
    };

    private WebMarkupContainer trainingContainer = new WebMarkupContainer("trainingContainer") {
        @Override
        public boolean isVisible() {
            return getModelObject() instanceof TrainingCourse;
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public DataTab(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);

        Form form = new Form("form");

        form.add(nameField);
        form.add(supportField);
        form.add(priceField);
        form.add(daysField);
        form.add(trainingContainer);
        trainingContainer.add(intervalField);
        trainingContainer.add(joinField);

        nameField.addMaxLengthValidator(1024);
        supportField.addMaxLengthValidator(128);

        form.add(new EltiAjaxSubmitLink("submitButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                ELTCourse course = DataTab.this.getModelObject();
                course.setName(nameField.getModelObject());
                course.setSupportEmail(supportField.getModelObject());
                course.setPrice(priceField.getPriceValue());
                course.setDays(daysField.getModelObject());

                if (course instanceof TrainingCourse) {
                    ((TrainingCourse) course).setStartDate(intervalField.getModelObject().getStart().toDate());
                    ((TrainingCourse) course).setFinishDate(intervalField.getModelObject().getEnd().toDate());
                    ((TrainingCourse) course).setJoinDate(joinField.getModelObject());
                }

                try {
                    courseManager.update(course);
                    ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        });

        add(form);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        nameField.setModelObject(getModelObject().getName());
        supportField.setModelObject(getModelObject().getSupportEmail());
        priceField.setValue(getModelObject().getPrice());
        daysField.setModelObject(getModelObject().getDays());

        if (getModelObject() instanceof TrainingCourse) {
            intervalField.setModelObject(new Interval(((TrainingCourse) getModelObject()).getStartDate().getTime(),
                    ((TrainingCourse) getModelObject()).getFinishDate().getTime()));
            joinField.setModelObject(((TrainingCourse) getModelObject()).getJoinDate());
        }
    }
}
