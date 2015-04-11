package com.eltiland.ui.library.admin;

import com.eltiland.model.library.LibraryCollection;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection grid.
 *
 * @author Aleksey Plotnikov.
 */
abstract class CollectionGridTable extends ELTTable<LibraryCollection> {
    /**
     * Panel constructor.
     *
     * @param id      markup id.
     * @param maxRows maximum count of rows to output.
     */
    public CollectionGridTable(String id, int maxRows) {
        super(id, maxRows);
    }

    @Override
    protected List<IColumn<LibraryCollection>> getColumns() {
        List<IColumn<LibraryCollection>> list = new ArrayList<>();
        list.add(new PropertyColumn<LibraryCollection>(new ResourceModel("nameColumn"), "name", "name"));
        list.add(new PropertyColumn<LibraryCollection>(
                new ResourceModel("descriptionColumn"), "description"));
        return list;
    }

    @Override
    protected List<GridAction> getControlActions() {
        return new ArrayList<>(Arrays.asList(GridAction.ADD));
    }

    @Override
    protected String getActionTooltip(GridAction action) {
        switch (action) {
            case EDIT:
                return getString("change");
            case REMOVE:
                return getString("delete");
            case CHILDREN:
                return getString("children");
            default:
                return null;
        }
    }

    @Override
    protected boolean hasConfirmation(GridAction action) {
        return action.equals(GridAction.REMOVE);
    }

    @Override
    protected boolean isSearching() {
        return true;
    }

    @Override
    protected boolean isControlling() {
        return true;
    }

    @Override
    protected String getSearchPlaceHolder() {
        return getString("searchCollection");
    }
}
