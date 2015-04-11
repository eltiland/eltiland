package com.eltiland.ui.video.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.VideoManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Video;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.video.YoutubeVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.tags.components.list.TagListPanel;
import com.eltiland.ui.tags.components.selector.TagSelectPanel;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for output video and it's information.
 */
public abstract class VideoInfoPanel extends BaseEltilandPanel<Video> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(VideoInfoPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private VideoManager videoManager;
    @SpringBean
    private TagManager tagManager;

    private Dialog<VideoPropertyPanel> videoPropertyPanelDialog = new Dialog<VideoPropertyPanel>("editDialog", 740) {
        @Override
        public VideoPropertyPanel createDialogPanel(String id) {
            return new VideoPropertyPanel(id, new GenericDBModel<>(Video.class, VideoInfoPanel.this.getModelObject()));
        }

        @Override
        public void registerCallback(VideoPropertyPanel panel) {
            super.registerCallback(panel);

            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Video>() {
                @Override
                public void process(IModel<Video> model, AjaxRequestTarget target) {
                    close(target);

                    try {
                        videoManager.updateVideo(model.getObject());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot update new Video item", e);
                        throw new WicketRuntimeException(e);
                    }

                    updateList(target);
                    ELTAlerts.renderOKPopup(getString("saveVideoMessage"), target);
                }
            });
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            updateList(target);
        }
    };

    private Dialog<TagSelectPanel> tagSelectPanelDialog = new Dialog<TagSelectPanel>("tagEditDialog", 300) {
        @Override
        public TagSelectPanel createDialogPanel(String id) {
            return new TagSelectPanel(id);
        }

        @Override
        public void registerCallback(TagSelectPanel panel) {
            super.registerCallback(panel);
            panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    EltiStaticAlerts.registerOKPopup(getString("tagMessageSaved"));
                    updateList(target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id markup
     */
    public VideoInfoPanel(String id, final IModel<Video> videoIModel) {
        super(id, videoIModel);

        add(new Label("name", videoIModel.getObject().getName()));
        add(new MultiLineLabel("description", videoIModel.getObject().getDescription()).setEscapeModelStrings(false));

        YoutubeVideoPlayer player = new YoutubeVideoPlayer("player", new Model<>(videoIModel.getObject().getLink()));
        add(player.setOutputMarkupId(true));

        genericManager.initialize(videoIModel.getObject(), videoIModel.getObject().getAuthor());

        WebMarkupContainer authorContainer = new WebMarkupContainer("authorContainer") {
            @Override
            public boolean isVisible() {
                return videoIModel.getObject().getAuthor() != null;
            }
        };

        add(authorContainer);

        if (videoIModel.getObject().getAuthor() != null) {
            EltiAjaxLink authorLink = new EltiAjaxLink("authorLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    setResponsePage(ProfileViewPage.class, new PageParameters().add(
                            ProfileViewPage.PARAM_ID, videoIModel.getObject().getAuthor().getId()));
                }
            };
            authorLink.add(new Label("author", videoIModel.getObject().getAuthor().getName()));
            authorContainer.add(authorLink);
        }

        WebMarkupContainer controlContainer = new WebMarkupContainer("controlPanel") {
            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser != null && currentUser.isSuperUser();
            }
        };

        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                videoPropertyPanelDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink tagButton = new EltiAjaxLink("tagButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                boolean isActive = tagManager.isEntityHasAnyTag(Video.class.getSimpleName());

                if (isActive) {
                    tagSelectPanelDialog.getDialogPanel().initPanel(VideoInfoPanel.this.getModel());
                    tagSelectPanelDialog.show(target);
                } else {
                    ELTAlerts.renderErrorPopup(getString("noTagsForEntity"), target);
                }
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink removeButton = new EltiAjaxLink("removeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    videoManager.deleteVideo(videoIModel.getObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot remove Video item", e);
                    throw new WicketRuntimeException(e);
                }
                updateList(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        controlContainer.add(removeButton);
        controlContainer.add(editButton);
        controlContainer.add(tagButton);

        editButton.add(new AttributeModifier("title", new ResourceModel("editTooltip")));
        removeButton.add(new AttributeModifier("title", new ResourceModel("removeTooltip")));
        tagButton.add(new AttributeModifier("title", new ResourceModel("tagTooltip")));
        editButton.add(new TooltipBehavior());
        removeButton.add(new TooltipBehavior());
        tagButton.add(new TooltipBehavior());

        add(controlContainer);

        add(videoPropertyPanelDialog);
        add(tagSelectPanelDialog);

        add(new TagListPanel("tagListPanel", VideoInfoPanel.this.getModel()));
        setOutputMarkupId(true);
    }

    protected abstract void updateList(AjaxRequestTarget target);

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_TAGS);
    }
}
