package com.eltiland.ui.course.control.general;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.video.YoutubeLinkVideoPlayer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for editing start video.
 *
 * @author Aleksey Plotnikov.
 */
class VideoTab extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;

    private YoutubeLinkVideoPlayer player = new YoutubeLinkVideoPlayer("video", new Model<String>());

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public VideoTab(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);

        Form form = new Form("form");
        player.setModelObject(getModelObject().getVideo());
        form.add(player.setOutputMarkupPlaceholderTag(true));

        form.add(new EltiAjaxSubmitLink("submitButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                ELTCourse course = VideoTab.this.getModelObject();
                course.setVideo((String) player.getDefaultModelObject());
                try {
                    courseManager.update(course);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }

                ELTAlerts.renderOKPopup(getString("saveMessage"), target);
            }
        });


        add(form);
    }
}
