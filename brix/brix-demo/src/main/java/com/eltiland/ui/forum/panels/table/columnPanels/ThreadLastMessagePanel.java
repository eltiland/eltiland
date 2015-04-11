package com.eltiland.ui.forum.panels.table.columnPanels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output last message for forum.
 *
 * @author Aleksey Plotnikov.
 */
public class ThreadLastMessagePanel extends BaseEltilandPanel<ForumThread> {
    @SpringBean
    private ForumMessageManager forumMessageManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<ForumMessage> lastMessageModel = new GenericDBModel<>(ForumMessage.class);

    public ThreadLastMessagePanel(String id, IModel<ForumThread> forumIModel) {
        super(id, forumIModel);

        lastMessageModel.setObject(forumMessageManager.getLastMessageForThread(getModelObject()));

        EltiAjaxLink authorLink = new EltiAjaxLink("authorLink") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                throw new RestartResponseException(ProfileViewPage.class,
                        new PageParameters().add(ProfileViewPage.PARAM_ID,
                                lastMessageModel.getObject().getAuthor().getId()));
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };
        add(authorLink);

        genericManager.initialize(lastMessageModel.getObject(), lastMessageModel.getObject().getAuthor());

        authorLink.add(new Label("author", lastMessageModel.getObject().getAuthor().getName()));
        add(new Label("date", DateUtils.formatFullDate(lastMessageModel.getObject().getDate())));
    }
}
