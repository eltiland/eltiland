package com.eltiland.ui.video.components;

import com.eltiland.bl.VideoManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Video;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for administration for video page.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VideoPageAdminPanel extends BaseEltilandPanel {

    protected static final Logger LOGGER = LoggerFactory.getLogger(VideoPageAdminPanel.class);

    @SpringBean
    private VideoManager videoManager;

    private Dialog<VideoPropertyPanel> addVideoDialog = new Dialog<VideoPropertyPanel>("addVideoDialog", 740) {
        @Override
        public VideoPropertyPanel createDialogPanel(String id) {
            return new VideoPropertyPanel(id);
        }

        @Override
        public void registerCallback(VideoPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Video>() {
                @Override
                public void process(IModel<Video> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        videoManager.createVideo(model.getObject());
                        videoManager.fillAdditionalInfo(model.getObject());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create new Video item", e);
                        throw new WicketRuntimeException(e);
                    }
                    ELTAlerts.renderOKPopup(getString("addVideoMessage"), target);
                    VideoPageAdminPanel.this.onUpdateList(target);
                }
            });
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            onUpdateList(target);
        }
    };

    private Dialog<VideoPagePropertyPanel> pagePropertyPanelDialog =
            new Dialog<VideoPagePropertyPanel>("propertyVideoDialog", 330) {
                @Override
                public VideoPagePropertyPanel createDialogPanel(String id) {
                    return new VideoPagePropertyPanel(id) {
                        @Override
                        protected void onSave(AjaxRequestTarget target) {
                            close(target);
                            onUpdateList(target);
                        }
                    };
                }
            };

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public VideoPageAdminPanel(String id) {
        super(id);

        add(new EltiAjaxLink("add") {
            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                addVideoDialog.show(target);
            }
        });

        add(new EltiAjaxLink("properties") {
            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                pagePropertyPanelDialog.show(target);
            }
        });

        add(addVideoDialog);
        add(pagePropertyPanelDialog);
    }

    protected abstract void onUpdateList(AjaxRequestTarget target);
}
