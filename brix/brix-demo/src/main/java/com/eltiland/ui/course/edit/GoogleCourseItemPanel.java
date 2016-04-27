package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing course element, based of google.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleCourseItemPanel extends AbstractCourseItemPanel<ELTGoogleCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;

    public GoogleCourseItemPanel(String id, IModel<ELTGoogleCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        GoogleDriveFile.TYPE type = getModelObject() instanceof ELTDocumentCourseItem ?
                GoogleDriveFile.TYPE.DOCUMENT : GoogleDriveFile.TYPE.PRESENTATION;

        genericManager.initialize(getModelObject(), getModelObject().getItem());

        ELTGoogleDriveEditor content = new ELTGoogleDriveEditor("content",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem()),
                ELTGoogleDriveEditor.MODE.EDIT, type) {
            @Override
            protected void onUpload(GoogleDriveFile file) {
                GoogleCourseItemPanel.this.getModelObject().setItem(file);
                try {
                    courseItemManager.update(GoogleCourseItemPanel.this.getModelObject());
                } catch (CourseException e) {
                    EltiStaticAlerts.registerErrorPopup(e.getMessage());
                }
            }

            @Override
            protected Panel getAdditionalPanel(String markupId) {
                return new AbstractDocActionPanel(markupId, GoogleCourseItemPanel.this.getModel()) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        try {
                            genericManager.initialize(GoogleCourseItemPanel.this.getModelObject(),
                                    GoogleCourseItemPanel.this.getModelObject().getItem());
                            googleDriveManager.publishDocument(GoogleCourseItemPanel.this.getModelObject().getItem());
                            googleDriveManager.insertPermission(GoogleCourseItemPanel.this.getModelObject().getItem(),
                                    new ELTGooglePermissions(ELTGooglePermissions.ROLE.WRITER,
                                            ELTGooglePermissions.TYPE.ANYONE));
                        } catch (GoogleDriveException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                    }

                    @Override
                    protected boolean isForm() {
                        return false;
                    }
                };
            }
        };
        add(content);
    }
}
