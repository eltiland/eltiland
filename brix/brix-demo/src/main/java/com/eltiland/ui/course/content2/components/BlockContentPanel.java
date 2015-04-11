package com.eltiland.ui.course.content2.components;

import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel for block information on content page.
 *
 * @author Aleksey Plotnikov.
 */
public class BlockContentPanel extends BaseEltilandPanel<ELTCourseBlock> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;

    public BlockContentPanel(String id, IModel<ELTCourseBlock> eltCourseBlockIModel) {
        super(id, eltCourseBlockIModel);

        add(new Label("name", getModelObject().getName()));
        add(new ListView<ELTCourseItem>("items", new LoadableDetachableModel<List<? extends ELTCourseItem>>() {
            @Override
            protected List<? extends ELTCourseItem> load() {
                return courseItemManager.getItems(BlockContentPanel.this.getModelObject());
            }
        }) {
            @Override
            protected void populateItem(ListItem<ELTCourseItem> item) {
                item.add(new ItemContentPanel("item", item.getModel()));
            }
        });
    }
}
