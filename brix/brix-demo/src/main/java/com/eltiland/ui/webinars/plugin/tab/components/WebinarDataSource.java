package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.WebinarManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Collections;

/**
 * General data source for announcement and invoice tab.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarDataSource implements IDataSource<Webinar> {

    @SpringBean
    private WebinarManager webinarManager;

    public WebinarDataSource() {
        Injector.get().inject(this);
    }

    @Override
    public void query(IQuery iQuery, IQueryResult<Webinar> webinarIQueryResult) {
        int count = webinarManager.getWebinarCount(true, isApproved(), null);
        webinarIQueryResult.setTotalCount(count);

        if (count < 1) {
            webinarIQueryResult.setItems(Collections.<Webinar>emptyIterator());
        }

        String sortProperty = "startDate";
        boolean isAscending = false;

        if (!iQuery.getSortState().getColumns().isEmpty()) {
            IGridSortState.ISortStateColumn sortingColumn = iQuery.getSortState().getColumns().get(0);
            sortProperty = sortingColumn.getPropertyName();
            isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
        }

        webinarIQueryResult.setItems(webinarManager.getWebinarList(
                iQuery.getFrom(),
                iQuery.getCount(),
                sortProperty,
                isAscending,
                true, isApproved(), null).iterator());
    }

    @Override
    public IModel<Webinar> model(Webinar webinar) {
        return new GenericDBModel<>(Webinar.class, webinar);
    }

    @Override
    public void detach() {
    }

    /**
     * @return value of the approved flag of webinars in list.
     */
    protected boolean isApproved() {
        return false;
    }
}
