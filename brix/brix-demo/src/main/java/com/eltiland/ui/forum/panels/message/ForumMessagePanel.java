package com.eltiland.ui.forum.panels.message;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.forum.ForumMessagePage;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for output forum message.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumMessagePanel extends BaseEltilandPanel<ForumMessage> {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ForumMessageManager forumMessageManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumMessagePanel.class);

    private Dialog<ForumEditMessagePanel> forumEditMessagePanelDialog =
            new Dialog<ForumEditMessagePanel>("editMessagePanel", 740) {
                @Override
                public ForumEditMessagePanel createDialogPanel(String id) {
                    return new ForumEditMessagePanel(id, forumEditMessagePanelDialog);
                }

                @Override
                public void registerCallback(ForumEditMessagePanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ForumMessage>() {
                        @Override
                        public void process(IModel<ForumMessage> model, AjaxRequestTarget target) {
                            close(target);
                            throw new RestartResponseException(ForumMessagePage.class,
                                    new PageParameters().add(ForumMessagePage.PARAM_ID,
                                            model.getObject().getThread().getId()));
                        }
                    });
                }

                @Override
                protected void onClose(AjaxRequestTarget target) {
                    super.onClose(target);
                    throw new RestartResponseException(ForumMessagePage.class,
                            new PageParameters().add(ForumMessagePage.PARAM_ID,
                                    ForumMessagePanel.this.getModelObject().getThread().getId()));
                }
            };

    private ForumCreateMessagePanel messagePanel = new ForumCreateMessagePanel("createMessagePanel") {
        @Override
        protected void onClose(AjaxRequestTarget target) {
            ForumMessage message = ForumMessagePanel.this.getModelObject();
            genericManager.initialize(message, message.getThread());
            throw new RestartResponseException(ForumMessagePage.class,
                    new PageParameters().add(ForumMessagePage.PARAM_ID, message.getThread().getId()));
        }

        @Override
        protected void onCreate(ForumMessage message, AjaxRequestTarget target) {
            ForumMessage parent = ForumMessagePanel.this.getModelObject();
            genericManager.initialize(message, message.getThread());
            message.setParent(parent);
            message.setThread(parent.getThread());

            try {
                forumMessageManager.createForumMessage(message);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot create Message", e);
                throw new WicketRuntimeException("Cannot create Message", e);
            }

            throw new RestartResponseException(ForumMessagePage.class,
                    new PageParameters().add(ForumMessagePage.PARAM_ID, message.getThread().getId()));
        }
    };

    public ForumMessagePanel(String id, IModel<ForumMessage> forumMessageIModel) {
        super(id, forumMessageIModel);

        int depth = forumMessageManager.getDepthLevel(getModelObject());
        if (depth != 0) {
            String margin = String.format("margin-left:%dpx", depth * 20);
            this.add(new AttributeAppender("style", margin));
        }

        genericManager.initialize(getModelObject(), getModelObject().getAuthor());

        add(new Label("date", DateUtils.formatFullDate(getModelObject().getDate())));

        EltiAjaxLink authorLink = new EltiAjaxLink("authorLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                throw new RestartResponseException(ProfileViewPage.class,
                        new PageParameters().add(ProfileViewPage.PARAM_ID,
                                ForumMessagePanel.this.getModelObject().getAuthor().getId()));
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };
        add(authorLink);

        authorLink.add(new Label("author", getModelObject().getAuthor().getName()));

        add(new Label("header", getModelObject().getHeader()));
        add(new MultiLineLabel("content", getModelObject().getContent()).setEscapeModelStrings(false));

        add(new EltiAjaxLink("replyButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                messagePanel.setVisible(true);
                target.add(messagePanel);
            }

            @Override
            public boolean isVisible() {
                User user = EltilandSession.get().getCurrentUser();
                return user != null;
            }
        });

        add(new EltiAjaxLink("editButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                forumEditMessagePanelDialog.getDialogPanel().initEditMode(ForumMessagePanel.this.getModelObject());
                forumEditMessagePanelDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeEditAndDelete();
            }
        });

        add(new EltiAjaxLink("removeButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("deleteConfirmation")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    forumMessageManager.deleteForumMessage(ForumMessagePanel.this.getModelObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot delete Message", e);
                    throw new WicketRuntimeException("Cannot delete Message", e);
                }
                throw new RestartResponseException(ForumMessagePage.class,
                        new PageParameters().add(ForumMessagePage.PARAM_ID,
                                ForumMessagePanel.this.getModelObject().getThread().getId()));
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeEditAndDelete();
            }
        });

        add(messagePanel.setOutputMarkupPlaceholderTag(true));
        messagePanel.setVisible(false);

        WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        add(listContainer);

        listContainer.add(new ListView<ForumMessage>("messageList",
                new GenericDBListModel<>(ForumMessage.class, forumMessageManager.getChildMessages(getModelObject()))) {
            @Override
            protected void populateItem(ListItem<ForumMessage> item) {
                item.add(new ForumMessagePanel("message", item.getModel()));
            }
        });

        add(forumEditMessagePanelDialog);
    }

    private boolean canBeEditAndDelete() {
        User user = EltilandSession.get().getCurrentUser();
        return (user != null) &&
                (user.isSuperUser() ||
                        ForumMessagePanel.this.getModelObject().getAuthor().getId().equals(user.getId()));
    }
}
