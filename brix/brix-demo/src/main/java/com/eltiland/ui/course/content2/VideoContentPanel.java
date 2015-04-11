package com.eltiland.ui.course.content2;

import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.ui.course.edit.video.VideoListPreviewPanel;
import org.apache.wicket.model.IModel;

/**
 * Panel for output video of course.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoContentPanel extends AbstractCourseContentPanel<ELTVideoCourseItem> {

    /**
     * Panel constructor.
     *
     * @param id                    markup id.
     * @param videoCourseItemIModel video model.
     */
    public VideoContentPanel(String id, IModel<ELTVideoCourseItem> videoCourseItemIModel) {
        super(id, videoCourseItemIModel);
        add(new VideoListPreviewPanel("videoList", getModel()));
    }
}
