package com.eltiland.ui.course.edit.video;

import com.eltiland.model.course2.content.video.ELTVideoItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.video.YoutubeVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Video preview panel.
 *
 * @author Aleksey Plotnikov.
 */
class VideoPreviewPanel extends BaseEltilandPanel<ELTVideoItem> {

    private IModel<ELTVideoItem> courseVideoItemIModel = new GenericDBModel<>(ELTVideoItem.class);

    private Label headerlabel = new Label("header", new Model<String>());
    private YoutubeVideoPlayer videoPlayer;
    private MultiLineLabel description = new MultiLineLabel("description", new Model<String>());

    public VideoPreviewPanel(String id) {
        super(id);
        videoPlayer = new YoutubeVideoPlayer("video", new Model<String>());
        addComponents();
    }

    public VideoPreviewPanel(String id, IModel<ELTVideoItem> courseVideoItemIModel) {
        super(id, courseVideoItemIModel);
        this.courseVideoItemIModel = getModel();
        videoPlayer = new YoutubeVideoPlayer("video", new Model<>(courseVideoItemIModel.getObject().getLink()));
        addComponents();
        presetFields();
    }

    public void initData(IModel<ELTVideoItem> itemModel) {
        courseVideoItemIModel = itemModel;
        videoPlayer.initData(new Model<>(courseVideoItemIModel.getObject().getLink()));
        presetFields();
    }

    private void addComponents() {
        add(headerlabel);
        add(videoPlayer.setOutputMarkupId(true));
        add(description.setEscapeModelStrings(false));
    }

    private void presetFields() {
        headerlabel.setDefaultModelObject(courseVideoItemIModel.getObject().getName());
        videoPlayer.setDefaultModelObject(courseVideoItemIModel.getObject().getLink());
        description.setDefaultModelObject(courseVideoItemIModel.getObject().getDescription());
    }
}
