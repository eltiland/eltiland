package com.eltiland.ui.course.control.general;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing general information.
 *
 * @author Aleksey Plotnikov.
 */
class StartTab extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private ELTCourseManager eltCourseManager;

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public StartTab(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);
        genericManager.initialize(getModelObject(), getModelObject().getStartPage());
        final ELTGoogleDriveEditor content = new ELTGoogleDriveEditor("startContent",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getStartPage()),
                ELTGoogleDriveEditor.MODE.EDIT, GoogleDriveFile.TYPE.DOCUMENT) {
            @Override
            protected void onUpload(GoogleDriveFile file) {
                StartTab.this.getModelObject().setStartPage(file);
                try {
                    eltCourseManager.update(StartTab.this.getModelObject());
                } catch (CourseException e) {
                    EltiStaticAlerts.registerErrorPopup(e.getMessage());
                }
            }
        };

        add(content);

        EltiAjaxLink saveButton = new EltiAjaxLink("save") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    genericManager.initialize(StartTab.this.getModelObject(),
                            StartTab.this.getModelObject().getStartPage());
                    googleDriveManager.publishDocument(StartTab.this.getModelObject().getStartPage());
                    googleDriveManager.insertPermission(StartTab.this.getModelObject().getStartPage(),
                            new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.ANYONE));
                    googleDriveManager.cacheFile(StartTab.this.getModelObject().getStartPage());
                } catch (GoogleDriveException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                ELTAlerts.renderOKPopup(getString("saveMessage"), target);
            }
        };
        add(saveButton);
    }
}
