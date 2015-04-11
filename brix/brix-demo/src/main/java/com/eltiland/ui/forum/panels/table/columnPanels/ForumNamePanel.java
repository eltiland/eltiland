package com.eltiland.ui.forum.panels.table.columnPanels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumManager;
import com.eltiland.bl.impl.ProxyUtils;
import com.eltiland.model.course.Course;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.GeneralForumGroup;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.forum.ForumThreadPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output forum name and description.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumNamePanel extends BaseEltilandPanel<Forum> {
    @SpringBean
    private GenericManager genericManager;

    public ForumNamePanel(String id, IModel<Forum> forumIModel) {
        super(id, forumIModel);

        EltiAjaxLink threadLink = new EltiAjaxLink("threadLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (!checkAccess()) {
                    ELTAlerts.renderErrorPopup(getString("accessDeniedCourseMessage"), target);
                } else {
                    throw new RestartResponseException(
                            ForumThreadPage.class, new PageParameters().add(
                            ForumThreadPage.PARAM_ID, ForumNamePanel.this.getModelObject().getId()));
                }
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        add(threadLink);
        threadLink.add(new Label("name", getModelObject().getName()));

        add(new Label("description", getModelObject().getDescription()));
    }

    private boolean checkAccess() {
        genericManager.initialize(getModelObject(), getModelObject().getForumgroup());
        if (ProxyUtils.unproxy(getModelObject().getForumgroup()) instanceof GeneralForumGroup) {
            return true;
        }

        User user = EltilandSession.get().getCurrentUser();
        if (user == null) {
            return false;
        }

        Course course = getModelObject().getCourse();
        if (course == null) {
            return true;
        }

        genericManager.initialize(user, user.getCourses());
        genericManager.initialize(course, course.getAuthor());

        return (user.isSuperUser()) ||
                (user.getId().equals(course.getAuthor().getId())) || (user.getCourses().contains(course));
    }
}
