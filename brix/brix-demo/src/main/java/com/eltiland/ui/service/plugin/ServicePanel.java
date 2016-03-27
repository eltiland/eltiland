package com.eltiland.ui.service.plugin;

import com.eltiland.bl.*;
import com.eltiland.bl.impl.integration.IconsLoader;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.Property;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

/**
 * Service function panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ServicePanel extends BaseEltilandPanel<Workspace> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ServicePanel.class);

    @SpringBean
    private IconsLoader iconsLoader;
    @SpringBean
    private IndexCreator indexCreator;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseUserDataManager courseUserDataManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    protected ServicePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        Injector.get().inject(this);

        add(new EltiAjaxLink("createCourse") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                User user = userManager.getUserByEmail("aleksey.plotnikov@logicify.com");

                Course course = new Course();
                course.setTraining(true);
                course.setAuthor(user);
                course.setCreationDate(DateUtils.getCurrentDate());
                course.setName("Реализуем ФГОС ДО: новые подходы к организации конструирования в современном образовательном пространстве");
                course.setStatus(false);

                course.setPublished(false);
                try {
                    courseManager.createCourse(course);
                } catch (EltilandManagerException e) {
                    e.printStackTrace();
                }

                CourseSession session = new CourseSession();
                session.setActive(true);
                session.setStartDate(DateUtils.getCurrentDate());
                session.setFinishDate(DateUtils.getCurrentDate());
                session.setPrejoinDate(DateUtils.getCurrentDate());
                session.setCourse(course);

                try {
                    genericManager.saveNew(session);
                } catch (ConstraintException e) {
                    e.printStackTrace();
                }

            }
        }.add(new ConfirmationDialogBehavior()));


        add(new EltiAjaxLink("reloadButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                iconsLoader.reloadIcons();
                ELTAlerts.renderOKPopup(getString("successMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("recreateSearchIndexesButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                indexCreator.doRebuildIndex();
                ELTAlerts.renderOKPopup(getString("recreateSearchIndexesMessage"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        add(new EltiAjaxLink("createStandartUserData") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                for (Course course : genericManager.getEntityList(Course.class, "id")) {
                    try {
                        courseUserDataManager.createStandart(course);
                    } catch (EltilandManagerException e) {
                        e.printStackTrace();
                    }
                }

                ELTAlerts.renderOKPopup(getString("success"), target);
            }
        }.add(new ConfirmationDialogBehavior()));

        final ELTFilePanel filePanel = new ELTFilePanel("file", new GenericDBListModel<File>(File.class));
        add(filePanel);
        add(new EltiAjaxLink("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    List<File> files = filePanel.getFiles(true);
                } catch (FileException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        add(new EltiAjaxLink("webinarSend") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Webinar webinar = genericManager.getObject(Webinar.class, (long) 53140);
                genericManager.initialize(webinar, webinar.getWebinarUserPayments());
                for (WebinarUserPayment payment : webinar.getWebinarUserPayments()) {
                    if (payment.getRole().equals(WebinarUserPayment.Role.MEMBER)) {
                        try {
                            emailMessageManager.sendWebinarInvitationToUser(payment);
                            LOGGER.info(String.format("send letter to %s", payment.getUserEmail()));
                        } catch (EmailException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        add(new EltiAjaxLink("createProperty") {
            @Override
            public void onClick(AjaxRequestTarget target) {
              /*  Course course = new Course();
                course.setName("Реализуем ФГОС ДО: новые подходы к организации конструирования в современном образовательном пространстве");
                course.setStatus(false);
                course.setPublished(false);
                course.setAutoJoin(true);

                try {
                    genericManager.saveNew(course);
                } catch (ConstraintException e) {
                    e.printStackTrace();
                }
          /*      Property property = new Property();
                property.setValue("Любое использование материала, полностью или частично, без разрешения правообладателя, запрещается и влечет наказание в соответствии с Уголовным кодексом РФ (ст. 146, 147, 180)");
                property.setProperty("course_author_warning");

                try {
                    genericManager.saveNew(property);
                    ELTAlerts.renderOKPopup("Created OK", target);
                } catch (ConstraintException e) {
                    e.printStackTrace();
                }*/
            }
        }.add(new ConfirmationDialogBehavior()));

//        Form form = new Form("form");
//        add(form);
//
//        final FileUploadField uploadField = new FileUploadField("filePanel");
//        form.add(uploadField);
//        form.add(new EltiAjaxSubmitLink("submit") {
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                uploadField.getFileUpload().get
//            }
//        });
    }
}
