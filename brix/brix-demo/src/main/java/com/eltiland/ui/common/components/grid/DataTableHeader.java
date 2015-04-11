package com.eltiland.ui.common.components.grid;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;

import java.util.List;

/**
 * General data table header toolbar.
 *
 * @author Aleksey Plotnikov
 */
public class DataTableHeader extends AbstractToolbar {
    /**
     * Constructor
     *
     * @param <T>          the column data type
     * @param stateLocator locator for the ISortState implementation used by sortable headers
     * @param panel        DataTablePanel instance
     */
    public <T> DataTableHeader(final ELTTable panel, final ISortStateLocator stateLocator) {
        super(panel.getTable());

        RepeatingView headers = new RepeatingView("headers");
        headers.setOutputMarkupId(true);
        add(headers);

        final List<IColumn<T>> columns = panel.getTable().getColumns();
        for (final IColumn<T> column : columns) {
            WebMarkupContainer item = new WebMarkupContainer(headers.newChildId());
            headers.add(item);

            WebMarkupContainer header;
            if (column.isSortable()) {
                header = new ImagedOrderByBorder(panel, "header", column.getSortProperty(), stateLocator);
            } else {
                header = new DivBorder("header");
            }

            item.add(header);
            item.setRenderBodyOnly(true);
            header.add(column.getHeader("label"));
        }
    }
}
