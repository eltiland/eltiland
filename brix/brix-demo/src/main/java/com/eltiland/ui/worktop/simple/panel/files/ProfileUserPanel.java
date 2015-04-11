package com.eltiland.ui.worktop.simple.panel.files;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.user.CourseFileAccessManager;
import com.eltiland.bl.user.UserFileAccessManager;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.file.CourseFileAccess;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.panel.files.panels.CourseAccessPanel;
import com.eltiland.ui.worktop.simple.panel.files.panels.UploadFilePanel;
import com.eltiland.ui.worktop.simple.panel.files.panels.UserAccessPanel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for user files.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileUserPanel extends BaseEltilandPanel<User> {

    @SpringBean
    private UserFileManager userFileManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private UserFileAccessManager userFileAccessManager;
    @SpringBean
    private CourseFileAccessManager courseFileAccessManager;
    @SpringBean
    private ELTCourseManager courseManager;

    private ELTTable<UserFile> table;

    private Dialog<UploadFilePanel> uploadDialog = new Dialog<UploadFilePanel>("uploadDialog", 465) {
        @Override
        public UploadFilePanel createDialogPanel(String id) {
            return new UploadFilePanel(id);
        }

        @Override
        public void registerCallback(UploadFilePanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<File>() {
                @Override
                public void process(IModel<File> model, AjaxRequestTarget target) {
                    close(target);
                    UserFile file = new UserFile();
                    file.setOwner(ProfileUserPanel.this.getModelObject());
                    file.setFile(model.getObject());
                    file.setUploadDate(DateUtils.getCurrentDate());
                    try {
                        userFileManager.create(file);
                        target.add(table);
                    } catch (UserException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    private IModel<UserFile> userFileIModel = new GenericDBModel<>(UserFile.class);

    private ELTSelectDialog<User> userSelector = new ELTSelectDialog<User>("userSelector", 890) {
        @Override
        protected int getMaxRows() {
            return 20;
        }

        @Override
        protected String getHeader() {
            return ProfileUserPanel.this.getString("user.header");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            List<Long> existsIds = new ArrayList<>();

            genericManager.initialize(userFileIModel.getObject(), userFileIModel.getObject().getDestinations());
            for (User user : userFileIModel.getObject().getDestinations()) {
                existsIds.add(user.getId());
            }
            for (Long id : selectedIds) {
                if (!(existsIds.contains(id))) {
                    UserFileAccess fileAccess = new UserFileAccess();
                    fileAccess.setFile(userFileIModel.getObject());
                    fileAccess.setClient(genericManager.getObject(User.class, id));
                    try {
                        userFileAccessManager.create(fileAccess);
                    } catch (UserException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            }
            for (Long id : existsIds) {
                if (!(selectedIds.contains(id))) {
                    UserFileAccess fileAccess = userFileAccessManager.getAccessInformation(
                            genericManager.getObject(User.class, id), userFileIModel.getObject());
                    try {
                        userFileAccessManager.delete(fileAccess);
                    } catch (UserException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            }
            close(target);
            target.add(table);
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("user.name.column"), "name", "name"));
            columns.add(new PropertyColumn<User>(new ResourceModel("user.email.column"), "email", "email"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return userManager.getUserSearchList(
                    first, count, getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return userManager.getUserSearchCount(getSearchString());
        }

        @Override
        protected String getSearchPlaceholder() {
            return ProfileUserPanel.this.getString("user.placeholder");
        }
    };

    private ELTSelectDialog<TrainingCourse> courseSelector =
            new ELTSelectDialog<TrainingCourse>("courseSelector", 890) {
                @Override
                protected int getMaxRows() {
                    return 10;
                }

                @Override
                protected String getHeader() {
                    return ProfileUserPanel.this.getString("course.header");
                }

                @Override
                protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
                    List<Long> existsIds = new ArrayList<>();

                    genericManager.initialize(userFileIModel.getObject(), userFileIModel.getObject().getCourses());
                    for (ELTCourse course : userFileIModel.getObject().getCourses()) {
                        existsIds.add(course.getId());
                    }
                    for (Long id : selectedIds) {
                        if (!(existsIds.contains(id))) {
                            CourseFileAccess access = new CourseFileAccess();
                            access.setFile(userFileIModel.getObject());
                            access.setCourse(genericManager.getObject(ELTCourse.class, id));
                            try {
                                courseFileAccessManager.create(access);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    }
                    for (Long id : existsIds) {
                        if (!(selectedIds.contains(id))) {
                            CourseFileAccess fileAccess = courseFileAccessManager.getAccessInformation(
                                    genericManager.getObject(ELTCourse.class, id), userFileIModel.getObject());
                            try {
                                courseFileAccessManager.delete(fileAccess);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    }
                    close(target);
                    target.add(table);
                }

                @Override
                protected List<IColumn<TrainingCourse>> getColumns() {
                    List<IColumn<TrainingCourse>> columns = new ArrayList<>();
                    columns.add(new PropertyColumn<TrainingCourse>(new ResourceModel("course.name.column"), "name"));
                    columns.add(new PropertyColumn<TrainingCourse>(
                            new ResourceModel("course.author.column"), "author.name"));
                    return columns;
                }

                @Override
                protected Iterator getIterator(int first, int count) {
                    return courseManager.getActiveTrainingCourses().iterator();
                }

                @Override
                protected int getSize() {
                    return courseManager.getActiveTrainingCourses().size();
                }

                @Override
                protected boolean isSearching() {
                    return false;
                }
            };

    public ProfileUserPanel(String id, IModel<User> userIModel) {
        super(id, userIModel);

        table = new ELTTable<UserFile>("table", 30) {
            @Override
            protected List<IColumn<UserFile>> getColumns() {
                List<IColumn<UserFile>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<UserFile>(new ResourceModel("name.column"), "file.name", "file.name"));
                columns.add(new PropertyColumn<UserFile>(new ResourceModel("date.column"), "uploadDate", "uploadDate"));
                columns.add(new AbstractColumn<UserFile>(new ResourceModel("users.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<UserFile>> cellItem,
                                             String componentId, IModel<UserFile> rowModel) {
                        cellItem.add(new UserAccessPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<UserFile>(new ResourceModel("courses.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<UserFile>> cellItem,
                                             String componentId, IModel<UserFile> rowModel) {
                        cellItem.add(new CourseAccessPanel(componentId, rowModel));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return userFileManager.getFileSearchList(ProfileUserPanel.this.getModelObject(), first, count,
                        getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return userFileManager.getFileSearchCount(ProfileUserPanel.this.getModelObject(), getSearchString());
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<UserFile> rowModel) {
                return new ArrayList<>(Arrays.asList(
                        GridAction.DOWNLOAD, GridAction.USERS, GridAction.COURSE, GridAction.REMOVE));
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected String getSearchPlaceHolder() {
                return ProfileUserPanel.this.getString("file.placeholder");
            }

            @Override
            protected void onClick(IModel<UserFile> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        uploadDialog.show(target);
                        break;
                    case DOWNLOAD:
                        break;
                    case USERS:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getDestinations());
                        List<Long> ids = new ArrayList<>();
                        for (User user : rowModel.getObject().getDestinations()) {
                            ids.add(user.getId());
                        }
                        userSelector.getDialogPanel().setSelectedIds(ids);
                        userFileIModel.setObject(rowModel.getObject());
                        userSelector.show(target);
                        break;
                    case COURSE:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getCourses());
                        ids = new ArrayList<>();
                        for (ELTCourse course : rowModel.getObject().getCourses()) {
                            ids.add(course.getId());
                        }
                        courseSelector.getDialogPanel().setSelectedIds(ids);
                        userFileIModel.setObject(rowModel.getObject());
                        courseSelector.show(target);
                        break;
                    case REMOVE:
                        List<UserFileAccess> userAccessInfo =
                                userFileAccessManager.getAccessInformation(rowModel.getObject());
                        List<CourseFileAccess> courseAccessInfo =
                                courseFileAccessManager.getAccessInformation(rowModel.getObject());
                        try {
                            for (UserFileAccess access : userAccessInfo) {
                                userFileAccessManager.delete(access);
                            }
                            for (CourseFileAccess access : courseAccessInfo) {
                                courseFileAccessManager.delete(access);
                            }
                            userFileManager.delete(rowModel.getObject());
                            target.add(table);
                        } catch (UserException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected String getFileName(IModel<UserFile> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFile());
                return rowModel.getObject().getFile().getName();
            }

            @Override
            protected InputStream getInputStream(IModel<UserFile> rowModel) throws ResourceStreamNotFoundException {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFile());
                genericManager.initialize(rowModel.getObject().getFile(), rowModel.getObject().getFile().getBody());
                IResourceStream resourceStream = fileUtility.getFileResource(
                        rowModel.getObject().getFile().getBody().getHash());
                return resourceStream.getInputStream();
            }

            @Override
            protected String getNotFoundedMessage() {
                return ProfileUserPanel.this.getString("no.files");
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ADD:
                        return getString("add.tooltip");
                    case DOWNLOAD:
                        return getString("download.tooltip");
                    case USERS:
                        return getString("users.tooltip");
                    case COURSE:
                        return getString("course.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }
        };
        add(table.setOutputMarkupId(true));
        add(uploadDialog);
        add(userSelector);
        add(courseSelector);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
