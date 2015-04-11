package com.eltiland.ui.course.components.editPanels.elements.video;

import com.eltiland.bl.CourseVideoItemManager;
import com.eltiland.model.course.CourseVideoItem;
import com.eltiland.model.course.VideoCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * List of the video preview panel.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoListPreviewPanel extends BaseEltilandPanel<VideoCourseItem> {

    @SpringBean
    private CourseVideoItemManager courseVideoItemManager;

    private IModel<List<CourseVideoItem>> listModel = new LoadableDetachableModel<List<CourseVideoItem>>() {
        @Override
        protected List<CourseVideoItem> load() {
            int count = courseVideoItemManager.getItemCount(VideoListPreviewPanel.this.getModelObject());
            return courseVideoItemManager.getItemList(
                    VideoListPreviewPanel.this.getModelObject(), 0, count, "index", true);
        }
    };

    public VideoListPreviewPanel(String id, IModel<VideoCourseItem> videoCourseItemIModel) {
        super(id, videoCourseItemIModel);

        add(new ListView<CourseVideoItem>("list", listModel) {
            @Override
            protected void populateItem(ListItem<CourseVideoItem> item) {
                item.add(new VideoPreviewPanel("panel", item.getModel()));
            }
        });
    }
}
