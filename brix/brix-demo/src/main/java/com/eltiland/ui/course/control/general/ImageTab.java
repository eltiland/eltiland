package com.eltiland.ui.course.control.general;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.File;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.avatar.CourseIconEditPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing image of the course.
 *
 * @author Aleksey Plotnikov.
 */
class ImageTab extends BaseEltilandPanel<ELTCourse> {

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
    public ImageTab(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);

        Form form = new Form("form");
        add(form);
        form.setMultiPart(true);

        genericManager.initialize(getModelObject(), getModelObject().getIcon());
        final CourseIconEditPanel editPanel = new CourseIconEditPanel("iconPanel",
                new GenericDBModel<>(File.class, getModelObject().getIcon()), new Model<>(getModelObject().getName()));
        form.add(editPanel);
        form.add(new EltiAjaxSubmitLink("submitButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                ELTCourse course = ImageTab.this.getModelObject();
                course.setIcon(editPanel.getIconFile());
                try {
                    courseManager.update(course);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }

                ELTAlerts.renderOKPopup(getString("saveMessage"), target);
            }
        });
    }
}
