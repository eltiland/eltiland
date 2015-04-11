package com.eltiland.ui.tags.components.filter;

import com.eltiland.bl.tags.TagManager;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.tags.components.general.TagPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output tag category
 *
 * @author Aleksey Plotnikov.
 */
abstract class TagCategoryPanel extends BaseEltilandPanel<TagCategory> {
    @SpringBean
    private TagManager tagManager;

    protected TagCategoryPanel(String id, IModel<TagCategory> tagCategoryIModel) {
        super(id, tagCategoryIModel);

        add(new Label("name", getModelObject().getName()) {
            @Override
            public boolean isVisible() {
                return !(tagManager.getTagList(TagCategoryPanel.this.getModelObject(), false, false).isEmpty());
            }
        });
        add(new ListView<Tag>("items", tagManager.getTagList(getModelObject(), false, false)) {
            @Override
            protected void populateItem(final ListItem<Tag> components) {
                components.add(new TagPanel(
                        "tag", components.getModel(), isTagSelected(components.getModelObject()), true) {
                    @Override
                    protected void onClick(AjaxRequestTarget target, boolean value) {
                        TagCategoryPanel.this.onClick(target, components.getModelObject(), value);
                    }
                });
            }
        });
    }

    protected abstract void onClick(AjaxRequestTarget target, Tag tag, boolean value);

    protected abstract boolean isTagSelected(Tag tag);
}
