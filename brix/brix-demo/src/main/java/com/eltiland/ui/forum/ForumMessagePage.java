package com.eltiland.ui.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.forum.panels.message.ForumCreateMessagePanel;
import com.eltiland.ui.forum.panels.message.ForumMessagePanel;
import com.eltiland.ui.login.LoginPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page of forum messages of given thread.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumMessagePage extends TwoColumnPage {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ForumMessageManager forumMessageManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumMessagePage.class);

    public static String MOUNT_PATH = "fmessage";

    /**
     * Forum thread id page parameter.
     */
    public static final String PARAM_ID = "id";

    private ForumCreateMessagePanel createMessagePanel = new ForumCreateMessagePanel("createPanel") {
        @Override
        protected void onClose(AjaxRequestTarget target) {
            throw new RestartResponseException(ForumMessagePage.class,
                    new PageParameters().add(PARAM_ID, forumThreadIModel.getObject().getId()));
        }

        @Override
        protected void onCreate(ForumMessage message, AjaxRequestTarget target) {
            message.setThread(forumThreadIModel.getObject());
            try {
                forumMessageManager.createForumMessage(message);
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot create forum message", e);
                throw new WicketRuntimeException("Cannot create forum message", e);
            }
            throw new RestartResponseException(ForumMessagePage.class,
                    new PageParameters().add(PARAM_ID, forumThreadIModel.getObject().getId()));
        }
    };

    private IModel<ForumThread> forumThreadIModel = new GenericDBModel<>(ForumThread.class);

    public ForumMessagePage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        ForumThread thread = genericManager.getObject(ForumThread.class, parameters.get(PARAM_ID).toLong());
        forumThreadIModel.setObject(thread);
        if (thread == null) {
            String errMsg = String.format("Forum thread with given ID not found");
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        add(new Label("header", thread.getName()));

        add(new EltiAjaxLink("enterLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(LoginPage.class);
            }

            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser == null;
            }
        });

        WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        add(listContainer);

        listContainer.add(new PageableListView<ForumMessage>(
                "messageList", new GenericDBListModel<>(ForumMessage.class,
                forumMessageManager.getAllTopLevelMessages(thread)), 20) {
            @Override
            protected void populateItem(ListItem<ForumMessage> item) {
                item.add(new ForumMessagePanel("message",
                        new GenericDBModel<>(ForumMessage.class, item.getModelObject())));
            }
        });

        EltiAjaxLink createButton = new EltiAjaxLink("createButton") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                createMessagePanel.setVisible(true);
                ajaxRequestTarget.add(createMessagePanel);
            }

            @Override
            public boolean isVisible() {
                User user = EltilandSession.get().getCurrentUser();
                return user != null;
            }
        };

        add(createButton);

        add(createMessagePanel.setOutputMarkupPlaceholderTag(true));
        createMessagePanel.setVisible(false);
    }
}
