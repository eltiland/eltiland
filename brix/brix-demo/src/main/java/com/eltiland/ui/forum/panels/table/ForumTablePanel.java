package com.eltiland.ui.forum.panels.table;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.forum.panels.table.columnPanels.ForumIconPanel;
import com.eltiland.ui.forum.panels.table.columnPanels.ForumLastMessagePanel;
import com.eltiland.ui.forum.panels.table.columnPanels.ForumNamePanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Forum table panel.
 *
 * @author Aleksey Plotnikov.
 */
public class ForumTablePanel extends BaseEltilandPanel<ForumGroup> {
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ForumMessageManager forumMessageManager;

    /**
     * DataTable source.
     */
    private ForumTable table;

    /**
     * Table constructor.
     *
     * @param id               markup id.
     * @param forumGroupIModel forum group model.
     */
    public ForumTablePanel(String id, IModel<ForumGroup> forumGroupIModel) {
        super(id, forumGroupIModel);

        ArrayList<IColumn<Forum>> forumList = new ArrayList<>();
        forumList.add(new AbstractColumn<Forum>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<Forum>> cellItem, String componentId, IModel<Forum> rowModel) {
                cellItem.add(new ForumIconPanel(componentId));
            }

            @Override
            public String getCssClass() {
                return "iconColumn";
            }
        });

        forumList.add(new AbstractColumn<Forum>(new ResourceModel("forumHeader")) {
            @Override
            public void populateItem(Item<ICellPopulator<Forum>> cellItem, String componentId, IModel<Forum> rowModel) {
                cellItem.add(new ForumNamePanel(componentId, rowModel));
            }

            @Override
            public String getCssClass() {
                return "forumColumn";
            }
        });

        forumList.add(new AbstractColumn<Forum>(new ResourceModel("themesHeader")) {
            @Override
            public String getCssClass() {
                return "themesColumn";
            }

            @Override
            public void populateItem(Item<ICellPopulator<Forum>> cellItem, String componentId, IModel<Forum> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getThreads());
                cellItem.add(new Label(componentId, String.valueOf(rowModel.getObject().getThreads().size())));
            }
        });

        forumList.add(new AbstractColumn<Forum>(new ResourceModel("messageHeader")) {
            @Override
            public String getCssClass() {
                return "messagesColumn";
            }

            @Override
            public void populateItem(Item<ICellPopulator<Forum>> cellItem, String componentId, IModel<Forum> rowModel) {
                cellItem.add(new Label(componentId,
                        String.valueOf(forumMessageManager.getMessageCountForForum(rowModel.getObject()))));
            }
        });

        forumList.add(new AbstractColumn<Forum>(new ResourceModel("lastMessageHeader")) {
            @Override
            public String getCssClass() {
                return "lastMessagesColumn";
            }

            @Override
            public void populateItem(Item<ICellPopulator<Forum>> cellItem, String componentId, IModel<Forum> rowModel) {
                if (forumMessageManager.getMessageCountForForum(rowModel.getObject()) == 0) {
                    cellItem.add(new Label(componentId, "..."));
                } else {
                    cellItem.add(new ForumLastMessagePanel(componentId, rowModel));
                }
            }
        });

        genericManager.initialize(getModelObject(), getModelObject().getForumSet());

        table = new ForumTable("table", forumList, new EltiDataProviderBase<Forum>() {
            @Override
            public Iterator iterator(int first, int count) {
                return ForumTablePanel.this.getModelObject().getForumSet().iterator();
            }

            @Override
            public int size() {
                return ForumTablePanel.this.getModelObject().getForumSet().size();
            }
        }, 10);
        add(table);

        // add toolbars
        table.addTopToolbar(new HeadersToolbar(table, null));
        table.addBottomToolbar(new NavigationToolbar(table));
    }
}
