package com.eltiland.ui.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.forum.panels.ForumGroupPanel;
import com.eltiland.ui.forum.panels.ForumGroupPropertyPanel;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Forum page.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumPage extends TwoColumnPage {

    @SpringBean
    private GenericManager genericManager;

    public static String MOUNT_PATH = "forum";

    private Dialog<ForumGroupPropertyPanel> groupPropertyDialog = new Dialog<ForumGroupPropertyPanel>(
            "groupPropertyDialog", 320) {
        @Override
        public ForumGroupPropertyPanel createDialogPanel(String id) {
            return new ForumGroupPropertyPanel(id);
        }

        @Override
        public void registerCallback(ForumGroupPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ForumGroup>() {
                @Override
                public void process(IModel<ForumGroup> model, AjaxRequestTarget target) {
                    close(target);
                    ELTAlerts.renderOKPopup(getString("groupCreatedMessage"), target);
                    target.add(listContainer);
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ForumGroup>() {
                @Override
                public void process(IModel<ForumGroup> model, AjaxRequestTarget target) {
                    close(target);
                    ELTAlerts.renderOKPopup(getString("groupSavedMessage"), target);
                    target.add(listContainer);
                }
            });
        }
    };

    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    /**
     * Page constrctor.
     *
     * @param parameters page parameters.
     */
    public ForumPage(PageParameters parameters) {
        super(parameters);

        add(listContainer.setOutputMarkupId(true));
        listContainer.add(new ListView<ForumGroup>("groupList", new LoadableDetachableModel<List<? extends ForumGroup>>() {
            @Override
            protected List<? extends ForumGroup> load() {
                return genericManager.getEntityList(ForumGroup.class, "id");
            }
        }) {
            @Override
            protected void populateItem(final ListItem<ForumGroup> item) {
                item.add(new ForumGroupPanel("group", item.getModel()) {
                    @Override
                    protected void onDelete(AjaxRequestTarget target) {
                        EltiStaticAlerts.registerOKPopup(getString("groupDeletedMessage"));
                        throw new RestartResponseException(ForumPage.class);
                    }

                    @Override
                    protected void onEdit(AjaxRequestTarget target) {
                        groupPropertyDialog.getDialogPanel().initEditMode(item.getModelObject());
                        groupPropertyDialog.show(target);
                    }
                });
            }
        });

        add(new EltiAjaxLink("createGroup") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                groupPropertyDialog.getDialogPanel().initCreateMode();
                groupPropertyDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                User currentUser = EltilandSession.get().getCurrentUser();
                return currentUser != null && currentUser.isSuperUser();
            }
        });

        add(groupPropertyDialog);
    }
}
