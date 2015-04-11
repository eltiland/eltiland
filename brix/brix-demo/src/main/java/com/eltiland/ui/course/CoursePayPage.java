package com.eltiland.ui.course;

import com.eltiland.bl.CourseInvoiceManager;
import com.eltiland.bl.CourseManager;
import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.paybuttons.CoursePayButton;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Page for paying for the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePayPage extends BaseEltilandPage {

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private CourseInvoiceManager courseInvoiceManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoursePayPage.class);

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/coursePay";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    private IModel<User> currentUserModel = new GenericDBModel<>(User.class);

    public CoursePayPage(PageParameters parameters) {
        super(parameters);

        currentUserModel.setObject(EltilandSession.get().getCurrentUser());

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final Course course = courseManager.getCourseById(parameters.get(PARAM_ID).toLong());
        if (course == null) {
            String errMsg = String.format("Cannot locate course by given ID");
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        if (currentUserModel.getObject() == null) {
            throw new RestartResponseException(
                    CoursePage.class, new PageParameters().add(PARAM_ID, parameters.get(PARAM_ID)));
        }

        if (!(courseInvoiceManager.checkAccessToCourse(course, currentUserModel.getObject()))) {
            throw new RestartResponseException(
                    CoursePage.class, new PageParameters().add(PARAM_ID, parameters.get(PARAM_ID)));
        }

        add(new Label("courseName", course.getName()));

        WebMarkupContainer fullContainer = new WebMarkupContainer("fullPayContainer");
        WebMarkupContainer partContainer = new WebMarkupContainer("partPayContainer");

        add(fullContainer);
        add(partContainer);

        fullContainer.setVisible(coursePaidInvoiceManager.isCoursePaid(course));

        CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(course, null);
        CoursePayment coursePayment = null;
        if (invoice != null) {
            try {
                coursePayment = coursePaymentManager.getPayment(currentUserModel.getObject(), invoice, true);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get payment", e);
                throw new WicketRuntimeException("Cannot get payment", e);
            }
        }

        CoursePayButton payButton = new CoursePayButton("payButton");
        Label priceLabel = new Label("price", new Model<String>());

        if (invoice != null) {
            payButton.setPaymentData(coursePayment);
            priceLabel.setDefaultModelObject(String.format(getString("payValue"), coursePayment.getPrice().toString()));
        }
        fullContainer.add(payButton);
        fullContainer.add(priceLabel);

        Label partHeaderLabel = new Label("partLabel", new Model<String>());
        partContainer.add(partHeaderLabel);

        partHeaderLabel.setDefaultModelObject(getString((invoice == null) ? "partMessageWithoutFull" : "partMessage"));

        IModel<List<FolderCourseItem>> foldersModel = new LoadableDetachableModel<List<FolderCourseItem>>() {
            @Override
            protected List<FolderCourseItem> load() {
                return coursePaidInvoiceManager.getPaidBlocksForCourse(course, currentUserModel.getObject());
            }
        };
        partContainer.setVisible(!(foldersModel.getObject().isEmpty()));

        partContainer.add(new ListView<FolderCourseItem>("partList", foldersModel) {
            @Override
            protected void populateItem(ListItem<FolderCourseItem> item) {
                item.add(new FolderPanel("partPanel", item.getModel()));
            }
        });

        add(new EltiAjaxLink("backButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CoursePage.class,
                        new PageParameters().add(PARAM_ID, course.getId()));
            }
        });
    }

    private class FolderPanel extends BaseEltilandPanel<FolderCourseItem> {
        private FolderPanel(String id, IModel<FolderCourseItem> folderCourseItemIModel) {
            super(id, folderCourseItemIModel);
            add(new Label("folderName", String.format(getString("folderName"), getModelObject().getName())));

            CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(
                    getModelObject().getCourse(), getModelObject());

            CoursePayment payment;
            try {
                payment = coursePaymentManager.getPayment(currentUserModel.getObject(), invoice, true);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get payment", e);
                throw new WicketRuntimeException("Cannot get payment", e);
            }

            CoursePayButton payButton = new CoursePayButton("payButton");
            payButton.setPaymentData(payment);
            add(payButton);
            add(new Label("price", String.format(getString("payValue"), payment.getPrice().toString())));
        }
    }
}