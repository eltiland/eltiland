package com.eltiland.ui.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.forum.panels.ForumThreadPropertyPanel;
import com.eltiland.ui.forum.panels.table.ThreadTablePanel;
import com.eltiland.ui.login.LoginPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page of forum thread.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumThreadPage extends TwoColumnPage {
    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumThreadPage.class);

    public static String MOUNT_PATH = "fthread";

    private IModel<Forum> forumModel = new GenericDBModel<>(Forum.class);

    private ThreadTablePanel tablePanel;

    private Dialog<ForumThreadPropertyPanel> threadPropertyPanelDialog =
            new Dialog<ForumThreadPropertyPanel>("createThreadDialog", 740) {
                @Override
                public ForumThreadPropertyPanel createDialogPanel(String id) {
                    return new ForumThreadPropertyPanel(id, forumModel, threadPropertyPanelDialog);
                }

                @Override
                public void registerCallback(ForumThreadPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ForumThread>() {
                        @Override
                        public void process(IModel<ForumThread> model, AjaxRequestTarget target) {
                            close(target);
                            EltiStaticAlerts.registerOKPopup(getString("createdThreadMessage"));
                            throw new RestartResponseException(ForumThreadPage.class,
                                    new PageParameters().add(ForumThreadPage.PARAM_ID, forumModel.getObject().getId()));
                        }
                    });
                }
            };

    /**
     * Forum id page parameter.
     */
    public static final String PARAM_ID = "id";

    public ForumThreadPage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        Forum forum = genericManager.getObject(Forum.class, parameters.get(PARAM_ID).toLong());
        if (forum == null) {
            String errMsg = String.format("Forum with given ID not found");
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }
        forumModel.setObject(forum);

        add(new Label("header", forum.getName()));
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

        tablePanel = new ThreadTablePanel("threadGrid", forumModel) {
            @Override
            public boolean isVisible() {
                Forum forum = forumModel.getObject();
                genericManager.initialize(forum, forum.getThreads());
                return !(forum.getThreads().isEmpty());
            }
        };

        add(tablePanel.setOutputMarkupId(true));

        add(new EltiAjaxLink("createButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                threadPropertyPanelDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser != null;
            }
        });

        add(threadPropertyPanelDialog);
    }
}
