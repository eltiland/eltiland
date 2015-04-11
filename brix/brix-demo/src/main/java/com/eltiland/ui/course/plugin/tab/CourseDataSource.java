package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CourseManager;
import com.eltiland.model.course.Course;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.model.IModel;

import java.util.Collections;

/**
 * Course data source.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseDataSource implements IDataSource<Course> {
    @Override
    public void query(IQuery iQuery, IQueryResult iQueryResult) {
        int count = getManager().getCourseListCount(getCourseStatus());
        iQueryResult.setTotalCount(count);

        if (count < 1) {
            iQueryResult.setItems(Collections.<Course>emptyIterator());
        }

        String sortProperty = "creationDate";
        boolean isAscending = false;

        if (!iQuery.getSortState().getColumns().isEmpty()) {
            IGridSortState.ISortStateColumn sortingColumn = iQuery.getSortState().getColumns().get(0);
            sortProperty = sortingColumn.getPropertyName();
            isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
        }

        iQueryResult.setItems(getManager().getCourseList(getCourseStatus(), iQuery.getFrom(),
                iQuery.getCount(), sortProperty, isAscending).iterator());
    }

    @Override
    public IModel<Course> model(Course course) {
        return new GenericDBModel<>(Course.class, course);
    }

    @Override
    public void detach() {
    }

    public abstract boolean getCourseStatus();

    public abstract CourseManager getManager();
}
