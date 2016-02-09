package com.eltiland.ui.worktop.simple.panel.course;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.interval.ELTIntervalField;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTDateField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.Interval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Panel for creating invoice for the course.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseInvoicePanel extends ELTDialogPanel {
    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private ELTCourseManager courseManager;

    private boolean isTraining;

    private ELTTextArea nameField = new ELTTextArea("name", new ResourceModel("name"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };
    private PriceField priceField = new PriceField("price", new ResourceModel("price"), new Model<BigDecimal>());
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
            return isTraining;
        }
    };

    public CourseInvoicePanel(String id) {
        super(id);
        form.add(nameField);
        form.add(priceField);
        trainingContainer.add(intervalField);
        trainingContainer.add(joinField);
        form.add(trainingContainer);
        nameField.addMaxLengthValidator(255);
    }

    public void initCourseKind(boolean training) {
        isTraining = training;
    }

    @Override
    protected String getHeader() {
        return getString(isTraining ? "invoiceTHeader" : "invoiceAHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        ELTCourse course = (isTraining) ? (new TrainingCourse()) : (new AuthorCourse());
        course.setName(nameField.getModelObject());
        course.setAuthor(EltilandSession.get().getCurrentUser());
        course.setCreationDate(DateUtils.getCurrentDate());
        course.setStatus(CourseStatus.NEW);
        course.setNeedConfirm(isTraining);

        BigDecimal price = priceField.getModelObject();
        if (price != null && !price.equals(BigDecimal.ZERO)) {
            course.setPrice(price);
        }

        if (course instanceof TrainingCourse) {
            ((TrainingCourse) course).setJoinDate(joinField.getModelObject());
            ((TrainingCourse) course).setStartDate(intervalField.getModelObject().getStart().toDate());
            ((TrainingCourse) course).setFinishDate(intervalField.getModelObject().getEnd().toDate());
        } else {
            ((AuthorCourse) course).setIndex(-1);
            ((AuthorCourse) course).setModule(isModule());
        }

        try {
            courseManager.create(course);
            onCreate(course, target);
        } catch (CourseException e) {
            ELTAlerts.renderErrorPopup(e.getMessage(), target);
        }
    }

    protected abstract void onCreate(ELTCourse course, AjaxRequestTarget target);

    protected abstract boolean isModule();

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }
}
