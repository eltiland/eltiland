package com.eltiland.ui.library.panels.filter.tag;

import com.eltiland.bl.tags.TagManager;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.panels.filter.AbstractFilterPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Tag Filter Panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TagFilterPanel extends AbstractFilterPanel<TagCategory> {

    @SpringBean
    private TagManager tagManager;

    /**
     * Panel constrctor.
     *
     * @param id markup id.
     */
    public TagFilterPanel(String id, IModel<TagCategory> categoryModel, IModel<SearchData> searchDataIModel) {
        super(id, categoryModel, searchDataIModel);
    }

    @Override
    protected IModel<String> getHeader() {
        return new Model<>(getModelObject().getName());
    }

    @Override
    protected ListView getList() {
        return new ListView<Tag>("list", new LoadableDetachableModel<List<? extends Tag>>() {
            @Override
            protected List<? extends Tag> load() {
                return tagManager.getTagList(TagFilterPanel.this.getModelObject(), true, true);
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Tag> item) {
                item.add(new TagFilterItemPanel("filterItem", item.getModelObject(),
                        searchModel.getObject().getTags().contains(item.getModelObject())) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        searchModel.getObject().getTags().add(item.getModelObject());
                        searchModel.getObject().setSearchString(getCurrentSearch());
                        searchModel.getObject().redirect();
                    }

                    @Override
                    protected void onClean(AjaxRequestTarget target) {
                        searchModel.getObject().getTags().remove(item.getModelObject());
                        searchModel.getObject().setSearchString(getCurrentSearch());
                        searchModel.getObject().redirect();
                    }
                });
            }
        };
    }
}