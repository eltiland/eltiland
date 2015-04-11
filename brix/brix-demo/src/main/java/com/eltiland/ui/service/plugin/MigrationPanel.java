package com.eltiland.ui.service.plugin;

import com.eltiland.bl.CourseListenerManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseUserDataManager;
import com.eltiland.bl.user.CourseFileAccessManager;
import com.eltiland.bl.user.UserFileAccessManager;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.course2.listeners.ListenerType;
import com.eltiland.model.file.CourseFileAccess;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Service function panel.
 *
 * @author Aleksey Plotnikov.
 */
public class MigrationPanel extends BaseEltilandPanel<Workspace> {

    private ELTTable<Course> table;

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseListenerManager courseListenerManager;
    @SpringBean
    private UserFileManager userFileManager;
    @SpringBean
    private UserFileAccessManager userFileAccessManager;
    @SpringBean
    private CourseFileAccessManager courseFileAccessManager;
    @SpringBean
    private ELTCourseUserDataManager courseUserDataManager;



    protected MigrationPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        Injector.get().inject(this);

        add(new ELTTable<Course>("table", 30) {
            @Override
            protected List<IColumn<Course>> getColumns() {
                List<IColumn<Course>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<Course>(new ResourceModel("name.column"), "name", "name"));
                columns.add(new AbstractColumn<Course>(new ResourceModel("status.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<Course>> cellItem, String componentId, IModel<Course> rowModel) {
                        cellItem.add(new Label(componentId,
                                getString(rowModel.getObject().isMigrated() ? "status.yes" : "status.no")));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return genericManager.getEntityList(Course.class, "name").iterator();
            }

            @Override
            protected int getSize() {
                return genericManager.getEntityCount(Course.class, "", "");
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Course> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.APPLY));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Course> rowModel) {
                return action.equals(GridAction.APPLY) && !(rowModel.getObject().isMigrated());
            }

            @Override
            protected void onClick(IModel<Course> rowModel, GridAction action, AjaxRequestTarget target) {
                Long id = rowModel.getObject().getId();
                if (id == 47900) {
                    TrainingCourse course = new TrainingCourse();
                    course.setName("Образовательные решения Lego Education для детей от 3 до 7 лет");
                    course.setAuthor(genericManager.getObject(User.class, (long) 39055));
                    course.setCreationDate(DateUtils.getCurrentDate());
                    course.setStartDate(new DateTime(2015, 4, 13, 0, 0).toDate());
                    course.setFinishDate(new DateTime(2015, 4, 24, 0, 0).toDate());
                    course.setJoinDate(new DateTime(2015, 3, 20, 0, 0).toDate());
                    course.setRequisites("ЗАО \"ЭЛТИ-КУДИЦ\"  \n" +
                            "Адрес: 115551, Москва г, Домодедовская ул, д. 20, кор. 3, тел.: (495) 646-01-40, (495) 392-7895, ф.(495) 392-81-27\n" +
                            "РЕКВИЗИТЫ ПОСТАВЩИКА: \n" +
                            "ИНН/КПП:  7724112008/772401001\n" +
                            "р/с 40702810500110060321 \n" +
                            "в АКБ \"РОСЕВРОБАНК\"(ОАО) Г.МОСКВА\n" +
                            "К/с 30101810800000000777\n" +
                            "БИК 044585777");
                    course.setLegalDoc(genericManager.getObject(File.class, (long) 48728));
                    course.setPhysicalDoc(genericManager.getObject(File.class, (long) 48726));
                    course.setOpen(true);
                    course.setStartPage(genericManager.getObject(GoogleDriveFile.class, (long) 48618));
                    course.setSupportEmail("kursy@vdm.ru");
                    course.setStatus(CourseStatus.PUBLISHED);
                    course.setIcon(genericManager.getObject(File.class, (long) 47981));
                    course.setNeedConfirm(true);
                    course.setPrice(new BigDecimal(6000));

                    try {
                        course = genericManager.saveNew(course);
                    } catch (ConstraintException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    try {
                        courseUserDataManager.createStandart(course);
                    } catch (CourseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    List<CourseListener> listeners = courseListenerManager.getListeners(
                            genericManager.getObject(CourseSession.class, (long) 47920), null);
                    for (CourseListener listener : listeners) {
                        genericManager.initialize(listener, listener.getListener());
                        genericManager.initialize(listener, listener.getUsers());
                        ELTCourseListener listener1 = new ELTCourseListener();
                        listener1.setRequisites(course.getRequisites());
                        if (listener.getStatus().equals(CourseListener.Status.NEW)) {
                            listener1.setStatus(PaidStatus.NEW);
                        } else {
                            if (listener.getStatus().equals(CourseListener.Status.APPROVED)) {
                                listener1.setStatus(PaidStatus.APPROVED);
                            }
                            if (listener.getStatus().equals(CourseListener.Status.CONFIRMED)) {
                                listener1.setStatus(PaidStatus.CONFIRMED);
                            }
                            if (listener.getStatus().equals(CourseListener.Status.PAYS)) {
                                listener1.setStatus(PaidStatus.PAYS);
                            }
                            UserFile userFile = new UserFile();
                            userFile.setOwner(course.getAuthor());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());

                            if( listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                                userFile.setFile(course.getPhysicalDoc());
                            } else {
                                userFile.setFile(course.getLegalDoc());
                            }
                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                            UserFileAccess fileAccess = new UserFileAccess();
                            fileAccess.setFile(userFile);
                            fileAccess.setClient(listener.getListener());
                            try {
                                userFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                            }
                        }

                        genericManager.initialize(listener, listener.getAuthorDocument());
                        if( listener.getAuthorDocument() != null ) {
                            UserFile userFile = new UserFile();
                            userFile.setOwner(course.getAuthor());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());
                            userFile.setFile(listener.getAuthorDocument());
                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                            CourseFileAccess fileAccess = new CourseFileAccess();
                            fileAccess.setFile(userFile);
                            fileAccess.setCourse(course);
                            try {
                                courseFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                            }
                        }

                        genericManager.initialize(listener, listener.getDocument());
                        if( listener.getDocument() != null ) {
                            UserFile userFile = new UserFile();
                            userFile.setOwner(listener.getListener());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());
                            userFile.setFile(listener.getDocument());

                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                        }

                        listener1.setCompleted(false);
                        listener1.setCourse(course);
                        listener1.setPrice(new BigDecimal(6000));
                        listener1.setListener(listener.getListener());
                        listener1.setOffer(listener.getOffer());
                        if (listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                            listener1.setType(ListenerType.PHYSICAL);
                        }
                        if (listener.getKind().equals(CourseListener.Kind.MOSCOW)) {
                            listener1.setType(ListenerType.MOSCOW);
                        }
                        if (listener.getKind().equals(CourseListener.Kind.LEGAL)) {
                            listener1.setType(ListenerType.LEGAL);
                        }
                        try {
                            listener1 = genericManager.saveNew(listener1);
                        } catch (ConstraintException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        if (!(listener.getUsers().isEmpty())) {
                            for (User user : listener.getUsers()) {
                                ELTCourseListener listener2 = new ELTCourseListener();
                                listener2.setParent(listener1);
                                listener2.setRequisites(course.getRequisites());
                                if (listener.getStatus().equals(CourseListener.Status.NEW)) {
                                    listener2.setStatus(PaidStatus.NEW);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.APPROVED)) {
                                    listener2.setStatus(PaidStatus.APPROVED);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.CONFIRMED)) {
                                    listener2.setStatus(PaidStatus.CONFIRMED);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.PAYS)) {
                                    listener2.setStatus(PaidStatus.PAYS);
                                }
                                listener2.setCompleted(false);
                                listener2.setCourse(course);
                                listener2.setPrice(new BigDecimal(6000));
                                listener2.setListener(user);
                                listener2.setOffer(listener.getOffer());
                                listener2.setType(ListenerType.PHYSICAL);
                                try {
                                    listener2 = genericManager.saveNew(listener2);
                                } catch (ConstraintException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                            }
                        }
                    }
                }


                if (id == 48351) {
                    TrainingCourse course = new TrainingCourse();
                    course.setName("Примерная образовательная программа «Детство» в условиях реализации ФГОС ДО");
                    course.setAuthor(genericManager.getObject(User.class, (long) 39055));
                    course.setCreationDate(DateUtils.getCurrentDate());
                    course.setStartDate(new DateTime(2015, 5, 19, 0, 0).toDate());
                    course.setFinishDate(new DateTime(2015, 5, 30, 0, 0).toDate());
                    course.setJoinDate(new DateTime(2015, 3, 27, 0, 0).toDate());
                    course.setRequisites("ЗАО \"ЭЛТИ-КУДИЦ\"  \n" +
                            "Адрес: 115551, Москва г, Домодедовская ул, д. 20, кор. 3, тел.: (495) 646-01-40, (495) 392-7895, ф.(495) 392-81-27\n" +
                            "РЕКВИЗИТЫ ПОСТАВЩИКА: \n" +
                            "ИНН/КПП:  7724112008/772401001\n" +
                            "р/с 40702810500110060321 \n" +
                            "в АКБ \"РОСЕВРОБАНК\"(ОАО) Г.МОСКВА\n" +
                            "К/с 30101810800000000777\n" +
                            "БИК 044585777");
                    course.setLegalDoc(genericManager.getObject(File.class, (long) 48417));
                    course.setPhysicalDoc(genericManager.getObject(File.class, (long) 48415));
                    course.setOpen(true);
                    course.setStartPage(genericManager.getObject(GoogleDriveFile.class, (long) 48372));
                    course.setSupportEmail("kursy@vdm.ru");
                    course.setStatus(CourseStatus.PUBLISHED);
                    course.setIcon(null);
                    course.setNeedConfirm(true);
                    course.setPrice(new BigDecimal(2700));

                    try {
                        course = genericManager.saveNew(course);
                    } catch (ConstraintException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    try {
                        courseUserDataManager.createStandart(course);
                    } catch (CourseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    List<CourseListener> listeners = courseListenerManager.getListeners(
                            genericManager.getObject(CourseSession.class, (long) 48390), null);
                    for (CourseListener listener : listeners) {
                        genericManager.initialize(listener, listener.getListener());
                        genericManager.initialize(listener, listener.getUsers());
                        ELTCourseListener listener1 = new ELTCourseListener();
                        listener1.setRequisites(course.getRequisites());
                        if (listener.getStatus().equals(CourseListener.Status.NEW)) {
                            listener1.setStatus(PaidStatus.NEW);
                        } else {
                            if (listener.getStatus().equals(CourseListener.Status.APPROVED)) {
                                listener1.setStatus(PaidStatus.APPROVED);
                            }
                            if (listener.getStatus().equals(CourseListener.Status.CONFIRMED)) {
                                listener1.setStatus(PaidStatus.CONFIRMED);
                            }
                            if (listener.getStatus().equals(CourseListener.Status.PAYS)) {
                                listener1.setStatus(PaidStatus.PAYS);
                            }
                            UserFile userFile = new UserFile();
                            userFile.setOwner(course.getAuthor());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());

                            if( listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                                userFile.setFile(course.getPhysicalDoc());
                            } else {
                                userFile.setFile(course.getLegalDoc());
                            }
                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                            UserFileAccess fileAccess = new UserFileAccess();
                            fileAccess.setFile(userFile);
                            fileAccess.setClient(listener.getListener());
                            try {
                                userFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                            }
                        }

                        genericManager.initialize(listener, listener.getAuthorDocument());
                        if( listener.getAuthorDocument() != null ) {
                            UserFile userFile = new UserFile();
                            userFile.setOwner(course.getAuthor());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());
                            userFile.setFile(listener.getAuthorDocument());
                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                            CourseFileAccess fileAccess = new CourseFileAccess();
                            fileAccess.setFile(userFile);
                            fileAccess.setCourse(course);
                            try {
                                courseFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                            }
                        }

                        genericManager.initialize(listener, listener.getDocument());
                        if( listener.getDocument() != null ) {
                            UserFile userFile = new UserFile();
                            userFile.setOwner(listener.getListener());
                            userFile.getCourses().add(course);
                            userFile.setUploadDate(DateUtils.getCurrentDate());
                            userFile.setFile(listener.getDocument());

                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                            }
                        }

                        listener1.setCompleted(false);
                        listener1.setCourse(course);
                        listener1.setPrice(new BigDecimal(2700));
                        listener1.setListener(listener.getListener());
                        listener1.setOffer(listener.getOffer());
                        if (listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                            listener1.setType(ListenerType.PHYSICAL);
                        }
                        if (listener.getKind().equals(CourseListener.Kind.MOSCOW)) {
                            listener1.setType(ListenerType.MOSCOW);
                        }
                        if (listener.getKind().equals(CourseListener.Kind.LEGAL)) {
                            listener1.setType(ListenerType.LEGAL);
                        }
                        try {
                            listener1 = genericManager.saveNew(listener1);
                        } catch (ConstraintException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                        if (!(listener.getUsers().isEmpty())) {
                            for (User user : listener.getUsers()) {
                                ELTCourseListener listener2 = new ELTCourseListener();
                                listener2.setParent(listener1);
                                listener2.setRequisites(course.getRequisites());
                                if (listener.getStatus().equals(CourseListener.Status.NEW)) {
                                    listener2.setStatus(PaidStatus.NEW);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.APPROVED)) {
                                    listener2.setStatus(PaidStatus.APPROVED);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.CONFIRMED)) {
                                    listener2.setStatus(PaidStatus.CONFIRMED);
                                }
                                if (listener.getStatus().equals(CourseListener.Status.PAYS)) {
                                    listener2.setStatus(PaidStatus.PAYS);
                                }
                                listener2.setCompleted(false);
                                listener2.setCourse(course);
                                listener2.setPrice(new BigDecimal(2700));
                                listener2.setListener(user);
                                listener2.setOffer(listener.getOffer());
                                listener2.setType(ListenerType.PHYSICAL);
                                try {
                                    listener2 = genericManager.saveNew(listener2);
                                } catch (ConstraintException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
