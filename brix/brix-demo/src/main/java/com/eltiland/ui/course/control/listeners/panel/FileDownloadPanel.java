package com.eltiland.ui.course.control.listeners.panel;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.CourseFileAccessManager;
import com.eltiland.bl.user.UserFileAccessManager;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.file.CourseFileAccess;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Course File download panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class FileDownloadPanel extends ELTDialogPanel {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserFileManager userFileManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private UserFileAccessManager userFileAccessManager;
    @SpringBean
    private CourseFileAccessManager courseFileAccessManager;

    private boolean isUpload;

    private IModel<ELTCourseListener> listenerModel = new GenericDBModel<>(ELTCourseListener.class);

    private IModel<ELTCourse> courseModel = new LoadableDetachableModel<ELTCourse>() {
        @Override
        protected ELTCourse load() {
            genericManager.initialize(listenerModel.getObject(), listenerModel.getObject().getCourse());
            return listenerModel.getObject().getCourse();
        }
    };

    private ELTFilePanel filePanel = new ELTFilePanel("files") {
        @Override
        protected boolean canBeDeleted() {
            return isUpload;
        }

        @Override
        protected boolean canBeUploaded() {
            return isUpload;
        }

        @Override
        protected int getMaxFiles() {
            return 5;
        }

        @Override
        protected void onDeleteActions(AjaxRequestTarget target, File file) {
            super.onDeleteActions(target, file);

            List<UserFile> userFiles = userFileManager.getFilesForListener(
                    listenerModel.getObject().getListener(), courseModel.getObject());

            UserFile userFile = null;
            for (UserFile tFile : userFiles) {
                genericManager.initialize(tFile, tFile.getFile());
                if (tFile.getFile().getId().equals(file.getId())) {
                    userFile = tFile;
                }
            }
            onDelete(listenerModel.getObject(), userFile, target);
        }

        @Override
        protected void onUploadActions(AjaxRequestTarget target, File file) {
            super.onUploadActions(target, file);
            create(file, target);

            genericManager.initialize(courseModel.getObject(), courseModel.getObject().getAuthor());
            genericManager.initialize(listenerModel.getObject(), listenerModel.getObject().getListener());
            try {
                emailMessageManager.sendFileUploadMessage(courseModel.getObject().getAuthor(),
                        listenerModel.getObject().getListener(), file);
            } catch (EmailException e) {
                ELTAlerts.renderErrorPopup(e.getMessage(), target);
            }
        }
    };

    public FileDownloadPanel(String id) {
        super(id);
        form.add(filePanel);
        form.setMultiPart(true);
    }

    public void initMode(boolean mode) {
        isUpload = mode;
    }

    public void initData(List<File> files, IModel<ELTCourseListener> listenerModel) {
        filePanel.setFiles(files);
        this.listenerModel = listenerModel;
    }

    @Override
    protected String getHeader() {
        return getString(isUpload ? "files.author.header" : "files.user.header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {

    }

    @Override
    public String getVariation() {
        return "styled";
    }

    private void create(File file, AjaxRequestTarget target) {

        try {
            fileManager.saveFile(file);
        } catch (FileException e) {
            ELTAlerts.renderErrorPopup(e.getMessage(), target);
        }

        genericManager.initialize(listenerModel.getObject(), listenerModel.getObject().getListener());
        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getAuthor());

        UserFile userFile = new UserFile();
        userFile.setOwner(courseModel.getObject().getAuthor());
        userFile.setFile(file);
        userFile.setUploadDate(DateUtils.getCurrentDate());
        try {
            userFileManager.create(userFile);
        } catch (UserException e) {
            ELTAlerts.renderErrorPopup(e.getMessage(), target);
        }

        UserFileAccess userFileAccess = new UserFileAccess();
        CourseFileAccess courseFileAccess = new CourseFileAccess();
        userFileAccess.setFile(userFile);
        userFileAccess.setClient(listenerModel.getObject().getListener());
        courseFileAccess.setFile(userFile);
        courseFileAccess.setCourse(courseModel.getObject());
        try {
            userFileAccessManager.create(userFileAccess);
            courseFileAccessManager.create(courseFileAccess);
        } catch (UserException e) {
            ELTAlerts.renderErrorPopup(e.getMessage(), target);
        }
    }

    protected abstract void onDelete(ELTCourseListener listener, UserFile userFile, AjaxRequestTarget target);
}
