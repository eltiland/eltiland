package com.eltiland.ui.users.plugin.tab;

import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.Collections;
import java.util.List;

/**
 * General data source for user tables.
 */
public abstract class UserDataSource implements IDataSource<User> {

    private static final int MAX_SEARCH_LEN = 1024;

    @Override
    public void detach() {
    }

    @Override
    public void query(IQuery iQuery, IQueryResult<User> result) {

        // truncate search string to max len
        String searchString = (String) getSearchField().getModelObject();
        if (searchString != null && searchString.length() > MAX_SEARCH_LEN) {
            searchString = searchString.substring(0, MAX_SEARCH_LEN);
        }

        String sortProperty = "name";
        boolean isAscending = true;

        int count = getManager().getUserSearchCount(searchString);
        result.setTotalCount(count);
        if (count < 1) {
            result.setItems(Collections.<User>emptyIterator());
        } else {
            if (!iQuery.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = iQuery.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }
            List<User> teachers = getManager().getUserSearchList(iQuery.getFrom(), iQuery.getCount(),
                    searchString, sortProperty, isAscending);
            result.setItems(teachers.iterator());
        }
    }

    @Override
    public IModel<User> model(User user) {
        return new GenericDBModel<>(User.class, user);
    }

    /**
     * @return manager for get values.
     */
    public abstract UserManager getManager();

    /**
     * @return search field.
     */
    public abstract TextField getSearchField();
}
