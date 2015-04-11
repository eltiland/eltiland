package com.eltiland.ui.course.content;

import com.eltiland.model.course.VideoCourseItem;
import com.eltiland.ui.course.components.editPanels.elements.video.VideoListPreviewPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

/**
 * Panel for output video of course.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoContentPanel extends CourseContentPanel<VideoCourseItem> {

    /**
     * Panel constructor.
     *
     * @param id                    markup id.
     * @param videoCourseItemIModel video model.
     */
    public VideoContentPanel(String id, IModel<VideoCourseItem> videoCourseItemIModel) {
        super(id, videoCourseItemIModel);
    }

    @Override
    protected WebMarkupContainer getContent() {
        WebMarkupContainer content = new WebMarkupContainer("content");
        content.add(new VideoListPreviewPanel("videoList", VideoContentPanel.this.getModel()));
        return content;
    }
}
