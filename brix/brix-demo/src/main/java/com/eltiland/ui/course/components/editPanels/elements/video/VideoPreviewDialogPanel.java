package com.eltiland.ui.course.components.editPanels.elements.video;

import com.eltiland.model.course.CourseVideoItem;
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

    public void initData(IModel<CourseVideoItem> itemModel) {
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
}
