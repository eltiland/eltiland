package com.eltiland.ui.forum.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumGroupManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.CourseForumGroup;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.forum.panels.table.ForumTablePanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forum page.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ForumGroupPanel extends BaseEltilandPanel<ForumGroup> {
    @SpringBean
    private ForumGroupManager forumGroupManager;
    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForumGroupPanel.class);

    private ForumTablePanel tablePanel = new ForumTablePanel("forumGrid", getModel()) {
        @Override
        public boolean isVisible() {
            ForumGroup forumGroup = ForumGroupPanel.this.getModelObject();
            genericManager.initialize(forumGroup, forumGroup.getForumSet());
            return !(forumGroup.getForumSet().isEmpty());
        }
    };

    private Dialog<ForumPropertyPanel> forumPropertyPanelDialog =
            new Dialog<ForumPropertyPanel>("addForumDialog", 325) {
                @Override
                public ForumPropertyPanel createDialogPanel(String id) {
                    return new ForumPropertyPanel(id, ForumGroupPanel.this.getModel());
                }

                @Override
                public void registerCallback(ForumPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<Forum>() {
                        @Override
                        public void process(IModel<Forum> model, AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("forumCreatedMessage"), target);
                            target.add(tablePanel);
                        }
                    });
                }
            };

    public ForumGroupPanel(String id, final IModel<ForumGroup> forumGroupIModel) {
        super(id, forumGroupIModel);
        add(new Label("name", forumGroupIModel.getObject().getName()));
        add(new ForumGroupActionPanel("iconActionPanel", forumGroupIModel) {
            @Override
            protected void onDelete(AjaxRequestTarget target) {
                try {
                    forumGroupManager.deleteForumGroup(forumGroupIModel.getObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot delete forum group", e);
                    throw new WicketRuntimeException("Cannot delete forum group", e);
                }
                ForumGroupPanel.this.onDelete(target);
            }

            @Override
            protected void onEdit(AjaxRequestTarget target) {
                ForumGroupPanel.this.onEdit(target);
            }

            @Override
            protected void onAdd(AjaxRequestTarget target) {
                forumPropertyPanelDialog.getDialogPanel().initCreateMode();
                forumPropertyPanelDialog.show(target);
            }

            @Override
            protected boolean canBeDeleted() {
                if (isAdminLogged()) {
                    String name = forumGroupIModel.getObject().getName();
                    if (!(name.equals(getString("generalGroup"))) && !(name.equals(getString("courseGroup")))) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected boolean canBeAdded() {
                return isAdminLogged() && !(ForumGroupPanel.this.getModelObject() instanceof CourseForumGroup);
            }
        });

        add(tablePanel.setOutputMarkupId(true));

        add(forumPropertyPanelDialog);
    }

    private boolean isAdminLogged() {
        User currentUser = EltilandSession.get().getCurrentUser();
        return (currentUser != null && currentUser.isSuperUser());
    }

    protected abstract void onDelete(AjaxRequestTarget target);

    protected abstract void onEdit(AjaxRequestTarget target);
}
