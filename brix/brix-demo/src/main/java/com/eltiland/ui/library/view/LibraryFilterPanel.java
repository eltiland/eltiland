package com.eltiland.ui.library.view;

import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.tags.TagCategory;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.panels.filter.collection.CollectionFilterPanel;
import com.eltiland.ui.library.panels.filter.tag.TagFilterPanel;
import com.eltiland.ui.library.panels.filter.type.TypeFilterPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel with filters for library.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class LibraryFilterPanel extends BaseEltilandPanel<SearchData> {

    @SpringBean
    private TagCategoryManager tagCategoryManager;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public LibraryFilterPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);

        add(new TypeFilterPanel("typeFilter", getModel()) {
            @Override
            protected String getCurrentSearch() {
                return LibraryFilterPanel.this.getCurrentSearch();
            }
        });

        IModel<List<TagCategory>> categoryListModel = new LoadableDetachableModel<List<TagCategory>>() {
            @Override
            protected List<TagCategory> load() {
                return tagCategoryManager.getCategoryList(LibraryRecord.class.getSimpleName(), true, true);
            }
        };

        add(new ListView<TagCategory>("filterCategoryList", categoryListModel) {
            @Override
            protected void populateItem(ListItem<TagCategory> item) {
                item.add(new TagFilterPanel("filterPanel", item.getModel(), LibraryFilterPanel.this.getModel()) {
                    @Override
                    protected String getCurrentSearch() {
                        return LibraryFilterPanel.this.getCurrentSearch();
                    }
                });
            }
        });

        add(new CollectionFilterPanel("collectionFilter", getModel()) {
            @Override
            protected String getCurrentSearch() {
                return LibraryFilterPanel.this.getCurrentSearch();
            }
        });
    }

    protected abstract String getCurrentSearch();
}
