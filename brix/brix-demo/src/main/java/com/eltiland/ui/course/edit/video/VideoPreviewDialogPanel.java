package com.eltiland.ui.course.edit.video;

import com.eltiland.model.course2.content.video.ELTVideoItem;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Video preview dialog panel.
 *
 * @author Aleksey Plotnikov.
 */
public class VideoPreviewDialogPanel extends ELTDialogPanel {

    private VideoPreviewPanel previewPanel = new VideoPreviewPanel("video_panel");

    public VideoPreviewDialogPanel(String id) {
        super(id);
        form.add(previewPanel);
    }

    public void initData(IModel<ELTVideoItem> itemModel) {
        previewPanel.initData(itemModel);
    }

    @Override
    protected String getHeader() {
        return getString("headerVideo");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
