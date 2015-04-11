package com.eltiland.ui.course.control.data;

import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.course.control.data.components.VersionSwitch;
import com.eltiland.ui.course.control.data.panel.ContentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * Course content edit panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseContentPanel extends BaseEltilandPanel<ELTCourse> {

    private ContentPanel contentPanel = new ContentPanel("contentPanel", getModel(),
            (getModelObject() instanceof TrainingCourse) ? ContentStatus.FULL : ContentStatus.DEMO);

    private VersionSwitch versionSwitch = new VersionSwitch("versionSwitch",
            (getModelObject() instanceof TrainingCourse) ? ContentStatus.FULL : ContentStatus.DEMO) {
        @Override
        protected void onClick(AjaxRequestTarget target, ContentStatus newStatus) {
            contentPanel.setStatus(newStatus);
            target.add(contentPanel);
        }

        @Override
        public boolean isVisible() {
            return CourseContentPanel.this.getModelObject() instanceof AuthorCourse;
        }
    };

    /**
     * Panel ctor.
     *
     * @param id              markup id.
     * @param eltCourseIModel course model.
     */
    public CourseContentPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);
        add(versionSwitch);
        add(contentPanel.setOutputMarkupId(true));
    }
}
