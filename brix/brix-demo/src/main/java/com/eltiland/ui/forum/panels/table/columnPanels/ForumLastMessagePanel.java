package com.eltiland.ui.forum.panels.table.columnPanels;

import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumMessage;
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
public class ForumLastMessagePanel extends BaseEltilandPanel<Forum> {
    @SpringBean
    private ForumMessageManager forumMessageManager;

    private IModel<ForumMessage> lastMessageModel = new GenericDBModel<>(ForumMessage.class);

    public ForumLastMessagePanel(String id, IModel<Forum> forumIModel) {
        super(id, forumIModel);

        lastMessageModel.setObject(forumMessageManager.getLastMessageForForum(
                ForumLastMessagePanel.this.getModelObject()));

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

        authorLink.add(new Label("author", lastMessageModel.getObject().getAuthor().getName()));
        add( new Label("date", DateUtils.formatFullDate(lastMessageModel.getObject().getDate())));
    }
}
