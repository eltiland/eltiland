package com.eltiland.ui.common.components.datagrid.styled;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;

/**
 * Abstract data table class.
 *
 * @author Aleksey Plotnikov
 */
public abstract class DataTablePanel<T> extends Panel {
    /**
     * DataTable source.
     */
    private DataTable<T> table;
    /**
     * Header toolbar.
     */
    private DataTableHeader headerToolbar;

    /**
     * Default constructor. Will show all users.
     *
     * @param id panel's id.
     */
    public DataTablePanel(String id, ISortableDataProvider<T> dataProvider, int maxRows) {
        super(id);

        setOutputMarkupId(true);

        //create table component and attach to panel view
        table = new DataTable<>("dataTable", getColumns(), dataProvider, maxRows);
        table.setOutputMarkupId(true);
        add(table);

        //create sortable header toolbar and and attach to table
        headerToolbar = new DataTableHeader(this, dataProvider);
        table.addTopToolbar(headerToolbar);

        table.addBottomToolbar(new NavigationToolbar(table));
    }

    /**
     * You must override it to create columns of data table.
     *
     * @return array of the columns.
     */
    protected abstract List<IColumn<T>> getColumns();

    /**
     * @return data table inside control.
     */
    protected DataTable getTable() {
        return table;
    }

    /**
     * @return data table header panel.
     */
    protected DataTableHeader getTableHeader() {
        return headerToolbar;
    }

}
