package com.eltiland.ui.google.components;

import com.eltiland.bl.drive.GooglePageManager;
import com.eltiland.model.google.GooglePage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.List;

/**
 * Google page data source.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class GoogleDataSource implements IDataSource<GooglePage> {
    @SpringBean
    private GooglePageManager googlePageManager;

    public GoogleDataSource() {
        Injector.get().inject(this);
    }

    @Override
    public void query(IQuery query, IQueryResult<GooglePage> result) {

        String searchString = (getSearchField() != null) ? (String) getSearchField().getModelObject() : null;

        String sortProperty = "name";
        boolean isAscending = true;

        int count = googlePageManager.getPagesCount(searchString);
        result.setTotalCount(count);
        if (count < 1) {
            result.setItems(Collections.<GooglePage>emptyIterator());
        } else {
            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }
            List<GooglePage> pages = googlePageManager.getPagesList(
                    searchString, query.getFrom(), query.getCount(), sortProperty, isAscending);
            result.setItems(pages.iterator());
        }
    }

    @Override
    public IModel<GooglePage> model(GooglePage object) {
        return new GenericDBModel<>(GooglePage.class, object);
    }

    @Override
    public void detach() {
    }

    /**
     * @return search field.
     */
    public abstract TextField getSearchField();
}
