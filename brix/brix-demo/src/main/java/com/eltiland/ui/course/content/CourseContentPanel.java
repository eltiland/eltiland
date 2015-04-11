package com.eltiland.ui.course.content;

import com.eltiland.bl.*;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.paybuttons.CoursePayButton;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Abstract content panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseContentPanel<T extends CourseItem> extends BaseEltilandPanel<T> {
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseItemManager courseItemManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentPanel.class);

    private IModel<User> currentUserModel = new GenericDBModel<>(User.class);

    protected CourseContentPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);

        currentUserModel.setObject(EltilandSession.get().getCurrentUser());

        final WebMarkupContainer contentContainer = new WebMarkupContainer("contentContainer");
        WebMarkupContainer paidContainer = new WebMarkupContainer("paidContainer");
        WebMarkupContainer fileContainer = new WebMarkupContainer("fileContainer");
        WebMarkupContainer accessContainer = new WebMarkupContainer("accessContainer");

        add(contentContainer);
        add(paidContainer);
        add(accessContainer);
        contentContainer.add(fileContainer);

        Date start = getModelObject().getAccessStartDate();
        Date end = getModelObject().getAccessEndDate();
        boolean access = start == null || end == null ||
                start.before(DateUtils.getCurrentDate()) && end.after(DateUtils.getCurrentDate());
        accessContainer.setVisible(!access);

        if (!access) {
            accessContainer.add(new Label("interval", String.format(getString("interval"),
                    DateUtils.formatFullDate(start), DateUtils.formatFullDate(end))));
        }

        List<File> files = fileManager.getFilesOfCourseItem(
                genericManager.getObject(CourseItem.class, tiModel.getObject().getId()));
        fileContainer.setVisible(isAttachShown() && !files.isEmpty());

        ELTUploadComponent filePanel = new ELTUploadComponent("filePanel") {
            @Override
            protected boolean showMaximumWarning() {
                return false;
            }

            @Override
            protected boolean showLabel() {
                return false;
            }
        };
        fileContainer.add(filePanel);
        filePanel.setUploadedFiles(files);
        filePanel.setReadonly(true);

        WebMarkupContainer content = getContent();
        if (content != null) {
            contentContainer.add(content);
        }

        boolean contentShown = isContentShown();
        contentContainer.setVisible(contentShown && access);
        paidContainer.setVisible(!contentShown && access);

        WebMarkupContainer blockPayContainer = new WebMarkupContainer("blockPayContainer");
        WebMarkupContainer fullContainer = new WebMarkupContainer("fullPayContainer");

        paidContainer.add(blockPayContainer);
        paidContainer.add(fullContainer);

        FolderCourseItem folderItem = getPaidFolder();
        blockPayContainer.setVisible(folderItem != null);
        Label blockLabel = new Label("blockLabel", new Model<String>());
        blockPayContainer.add(blockLabel);
        if (folderItem != null) {
            blockLabel.setDefaultModelObject(String.format(getString("payBlockAccess"), folderItem.getName()));
        }

        CoursePayButton partButton = new CoursePayButton("button");
        Label partPrice = new Label("price", new Model<String>());
        blockPayContainer.add(partButton);
        blockPayContainer.add(partPrice);
        if (folderItem != null) {
            CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(getCourse(), folderItem);
            CoursePayment payment;
            try {
                payment = coursePaymentManager.getPayment(currentUserModel.getObject(), invoice, true);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get payment", e);
                throw new WicketRuntimeException("Cannot get payment", e);
            }
            partButton.setPaymentData(payment);
            partPrice.setDefaultModelObject(String.format(getString("payValue"), payment.getPrice().toString()));
        }

        boolean isCoursePaid = coursePaidInvoiceManager.isCoursePaid(getCourse());
        fullContainer.setVisible(isCoursePaid);

        CoursePayButton fullButton = new CoursePayButton("buttonFull");
        Label fullPrice = new Label("priceFull", new Model<String>());
        fullContainer.add(fullButton);
        fullContainer.add(fullPrice);
        if (isCoursePaid) {
            CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(getCourse(), null);
            CoursePayment payment;
            try {
                payment = coursePaymentManager.getPayment(currentUserModel.getObject(), invoice, true);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot get payment", e);
                throw new WicketRuntimeException("Cannot get payment", e);
            }
            fullButton.setPaymentData(payment);
            fullPrice.setDefaultModelObject(String.format(getString("payValue"), payment.getPrice().toString()));
        }

        contentContainer.setVisible(!(getModelObject().isControl()));
        contentContainer.setOutputMarkupPlaceholderTag(true);
        final WebMarkupContainer controlContainer = new WebMarkupContainer("controlContainer");
        controlContainer.setVisible(CourseContentPanel.this.getModelObject().isControl() && access);
        controlContainer.add(new EltiAjaxLink("showButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                increaseAttempt();
                contentContainer.setVisible(true);
                controlContainer.setVisible(false);
                target.add(contentContainer);
                target.add(controlContainer);
            }
        });

        add(controlContainer.setOutputMarkupPlaceholderTag(true));
    }

    protected abstract WebMarkupContainer getContent();

    protected void increaseAttempt() {

    }

    // return TRUE if content container will be shown.
    private boolean isContentShown() {
        // prepare necessary data
        CourseItem item = getModelObject();

        Course course = getCourse();
        if (course == null) {
            return true; // this is demo version.
        }

        genericManager.initialize(course, course.getAuthor());
        if (currentUserModel.getObject().getId().equals(course.getAuthor().getId())) {
            return true; // current user are the author of this course.
        }

        if (currentUserModel.getObject().isSuperUser()) {
            return true; // current user are the portal admin.
        }

        // check if course is not paid and current block also not paid
        if (!coursePaidInvoiceManager.isCoursePaid(course) && (getPaidFolder() == null)) {
            return true;
        }

        // check if user has paid entire course
        CoursePaidInvoice courseInvoice = coursePaidInvoiceManager.getActualInvoice(course, null);
        if (courseInvoice != null) {
            if (coursePaymentManager.isEntityPaidByUser(currentUserModel.getObject(), courseInvoice)) {
                return true;
            }
        }

        // check if user has paid given block.
        if (item instanceof FolderCourseItem) {
            if (coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) item)) {
                CoursePaidInvoice blockInvoice =
                        coursePaidInvoiceManager.getActualInvoice(course, (FolderCourseItem) item);
                return coursePaymentManager.isEntityPaidByUser(currentUserModel.getObject(), blockInvoice);
            }
        }

        // check if user has paid block, which is parent of the given block
        CourseItem parent = null;
        do { // find first block which have price
            parent = item.getParentItem();
            if (parent == null) {
                break;
            }
            item = parent;
        } while (!coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) parent));

        if (parent == null) { // block or item has not personal price, but course is paid.
            return courseInvoice == null;
        } else {  // block has personal price.
            CoursePaidInvoice blockInvoice =
                    coursePaidInvoiceManager.getActualInvoice(course, (FolderCourseItem) parent);
            return coursePaymentManager.isEntityPaidByUser(currentUserModel.getObject(), blockInvoice);
        }
    }

    private FolderCourseItem getPaidFolder() {
        CourseItem item = getModelObject();
        if ((item instanceof FolderCourseItem) && (coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) item))) {
            return (FolderCourseItem) item;
        } else {
            CourseItem parent = null;
            do { // find first block which have price
                parent = item.getParentItem();
                if (parent == null) {
                    break;
                }
                item = courseItemManager.getCourseItemById(parent.getId());
            } while (!coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) parent));
            return (parent == null) ? null : (FolderCourseItem) parent;
        }
    }

    private Course getCourse() {
        CourseItem item = getModelObject();

        genericManager.initialize(item, item.getParentItem());
        CourseItem parent = item.getParentItem();
        while (parent != null) {
            item = parent;
            genericManager.initialize(item, item.getParentItem());
            parent = item.getParentItem();
        }

        genericManager.initialize(item, item.getCourseFull());
        return item.getCourseFull();
    }

    protected boolean isAttachShown() {
        return true;
    }
}
