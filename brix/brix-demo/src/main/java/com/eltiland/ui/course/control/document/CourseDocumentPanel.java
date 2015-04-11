package com.eltiland.ui.course.control.document;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Document management panel for training courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseDocumentPanel extends BaseEltilandPanel<TrainingCourse> {

    private final String CSS = "static/css/panels/course_document.css";

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager courseManager;

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public CourseDocumentPanel(String id, IModel<TrainingCourse> courseIModel) {
        super(id, courseIModel);

        genericManager.initialize(getModelObject(), getModelObject().getPhysicalDoc());
        genericManager.initialize(getModelObject(), getModelObject().getLegalDoc());

        Form form = new Form("form");
        add(form);

        final ELTFilePanel physFile = new ELTFilePanel("phys_file") {
            @Override
            protected String getCaption() {
                return CourseDocumentPanel.this.getString("physical");
            }

            @Override
            protected boolean canBeDeleted() {
                return true;
            }

            @Override
            protected void onDeleteActions(AjaxRequestTarget target, File file) {
                CourseDocumentPanel.this.getModelObject().setPhysicalDoc(null);
                try {
                    courseManager.update(CourseDocumentPanel.this.getModelObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                super.onDeleteActions(target, file);
            }
        };
        final ELTFilePanel legalFile = new ELTFilePanel("legal_file") {
            @Override
            protected String getCaption() {
                return CourseDocumentPanel.this.getString("legal");
            }

            @Override
            protected boolean canBeDeleted() {
                return true;
            }

            @Override
            protected void onDeleteActions(AjaxRequestTarget target, File file) {
                CourseDocumentPanel.this.getModelObject().setLegalDoc(null);
                try {
                    courseManager.update(CourseDocumentPanel.this.getModelObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                super.onDeleteActions(target, file);
            }
        };

        final ELTTextArea requisites = new ELTTextArea(
                "requisites", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
            @Override
            protected boolean isFillToWidth() {
                return true;
            }
        };

        requisites.setModelObject(getModelObject().getRequisites());

        File phys_file = getModelObject().getPhysicalDoc();
        File legal_file = getModelObject().getLegalDoc();
        if (phys_file != null) {
            physFile.setFiles(new ArrayList<>(Arrays.asList(phys_file)));
        }
        if (legal_file != null) {
            legalFile.setFiles(new ArrayList<>(Arrays.asList(legal_file)));
        }

        form.add(physFile);
        form.add(legalFile);
        form.add(requisites);

        form.add(new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    List<File> files = physFile.getFiles(true);
                    List<File> legalfiles = legalFile.getFiles(true);
                    if (!files.isEmpty()) {
                        File physicalFile = files.get(0);
                        CourseDocumentPanel.this.getModelObject().setPhysicalDoc(physicalFile);
                    }
                    if (!legalfiles.isEmpty()) {
                        File legalFile = legalfiles.get(0);
                        CourseDocumentPanel.this.getModelObject().setLegalDoc(legalFile);
                    }
                    CourseDocumentPanel.this.getModelObject().setRequisites(requisites.getModelObject());
                    courseManager.update(CourseDocumentPanel.this.getModelObject());
                } catch (FileException | CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        });

        form.setMultiPart(true);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
