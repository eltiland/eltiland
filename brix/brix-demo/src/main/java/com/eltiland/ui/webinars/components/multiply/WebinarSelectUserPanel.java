package com.eltiland.ui.webinars.components.multiply;

import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import com.eltiland.ui.common.components.user_selector.SelectUserPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;
import java.util.List;

/**
 * Panel for selecting webinar user for adding to webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarSelectUserPanel extends SelectUserPanel implements IDialogSelectCallback<User> {

    @SpringBean
    private UserManager userManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    public WebinarSelectUserPanel(String id) {
        super(id);
    }

    public void initData(Webinar webinar) {
        webinarIModel.setObject(webinar);
    }

    @Override
    protected void query(IDataSource.IQuery query, IDataSource.IQueryResult<User> result) {
        //truncate search string to max len
        String searchString = searchField.getConvertedInput();
        if (searchString != null && searchString.length() > MAX_SEARCH_LEN) {
            searchString = searchString.substring(0, MAX_SEARCH_LEN);
        }

        String sortProperty = "name";
        boolean isAscending = true;

        int count = 0;
        if (searchString != null) {
            count = userManager.getUserCountAvailableToWebinar(searchString, webinarIModel.getObject());
        }
        result.setTotalCount(count);
        if (count < 1) {
            result.setItems(Collections.<User>emptyIterator());
        } else {
            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }
            List<User> users = userManager.getUserListAvailableToWebinar(
                    searchString, query.getFrom(), query.getCount(), sortProperty, isAscending, webinarIModel.getObject());
            result.setItems(users.iterator());
        }
    }
}
