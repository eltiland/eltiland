package com.eltiland.ui.library.panels.filter.type;

import com.eltiland.model.library.*;
import com.eltiland.ui.library.SearchData;
import com.eltiland.ui.library.panels.filter.AbstractFilterPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel with filter by record class.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class TypeFilterPanel extends AbstractFilterPanel {

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public TypeFilterPanel(String id, IModel<SearchData> searchDataIModel) {
        super(id, searchDataIModel);
    }

    @Override
    protected IModel<String> getHeader() {
        return new ResourceModel("header");
    }

    @Override
    protected ListView getList() {
        return new ListView<Class<? extends LibraryRecord>>(
                "list", new LoadableDetachableModel<List<? extends Class<? extends LibraryRecord>>>() {
            @Override
            protected List<? extends Class<? extends LibraryRecord>> load() {
                return new ArrayList<>(Arrays.asList(
                        LibraryRecord.class,
                        LibraryDocumentRecord.class,
                        LibraryPresentationRecord.class,
                        LibraryVideoRecord.class,
                        LibraryImageRecord.class,
                        LibraryArchiveRecord.class));
            }
        }) {

            @Override
            protected void populateItem(final ListItem<Class<? extends LibraryRecord>> item) {
                item.add(new TypeFilterItemPanel("filterItem", item.getModelObject(),
                        ((SearchData)searchModel.getObject()).getClazz().equals(item.getModelObject())) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        ((SearchData)searchModel.getObject()).setSearchString(getCurrentSearch());
                        ((SearchData)searchModel.getObject()).setClazz(item.getModelObject());
                        ((SearchData)searchModel.getObject()).redirect();
                    }

                    @Override
                    protected void onClean(AjaxRequestTarget target) {
                        ((SearchData)searchModel.getObject()).setSearchString(getCurrentSearch());
                        ((SearchData)searchModel.getObject()).setClazz(LibraryRecord.class);
                        ((SearchData)searchModel.getObject()).redirect();
                    }
                });
            }
        };
    }
}
