package com.eltiland.ui.course.edit.video;

import com.eltiland.bl.course.video.ELTVideoItemManager;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoItem;
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
public class VideoListPreviewPanel extends BaseEltilandPanel<ELTVideoCourseItem> {

    @SpringBean
    private ELTVideoItemManager videoItemManager;

    private IModel<List<ELTVideoItem>> listModel = new LoadableDetachableModel<List<ELTVideoItem>>() {
        @Override
        protected List<ELTVideoItem> load() {
            int count = videoItemManager.getCount(VideoListPreviewPanel.this.getModelObject());
            return videoItemManager.getItems(VideoListPreviewPanel.this.getModelObject(), 0, count, "index", true);
        }
    };

    public VideoListPreviewPanel(String id, IModel<ELTVideoCourseItem> videoCourseItemIModel) {
        super(id, videoCourseItemIModel);

        add(new ListView<ELTVideoItem>("list", listModel) {
            @Override
            protected void populateItem(ListItem<ELTVideoItem> item) {
                item.add(new VideoPreviewPanel("panel", item.getModel()));
            }
        });
    }
}
