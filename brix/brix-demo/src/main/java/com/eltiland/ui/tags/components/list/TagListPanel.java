package com.eltiland.ui.tags.components.list;

import com.eltiland.bl.tags.TagEntityManager;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.tags.Tag;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.tags.components.general.TagPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel for output list of tags for tagable entity.
 *
 * @author Aleksey Plotnikov.
 */
public class TagListPanel extends BaseEltilandPanel {
    @SpringBean
    private TagEntityManager tagEntityManager;

    public TagListPanel(String id, final IModel<? extends ITagable> iModel) {
        super(id);

        add(new ListView<Tag>("list", new LoadableDetachableModel<List<? extends Tag>>() {
            @Override
            protected List<? extends Tag> load() {
                return tagEntityManager.getEntityTags(iModel.getObject().getId());
            }
        }) {
            @Override
            protected void populateItem(ListItem<Tag> components) {
                components.add(new TagPanel("tagPanel", components.getModel(), false, false));
            }
        });
    }
}
