package com.eltiland.ui.forum.panels.table.columnPanels;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.forum.ForumMessagePage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output thread name and author.
 *
 * @author Aleksey Plotnikov.
 */
public class ThreadNamePanel extends BaseEltilandPanel<ForumThread> {
    @SpringBean
    private GenericManager genericManager;

    public ThreadNamePanel(String id, IModel<ForumThread> forumThreadIModel) {
        super(id, forumThreadIModel);

        EltiAjaxLink threadLink = new EltiAjaxLink("threadLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(
                        ForumMessagePage.class, new PageParameters().add(
                        ForumMessagePage.PARAM_ID, ThreadNamePanel.this.getModelObject().getId()));
            }
        };

        threadLink.add(new Label("name", getModelObject().getName()));
        add(threadLink);

        genericManager.initialize(getModelObject(), getModelObject().getAuthor());
        add(new Label("authorDate",
                String.format(getString("authorLabel"),
                        getModelObject().getAuthor().getName(), DateUtils.formatFullDate(getModelObject().getDate()))));
    }
}
