package com.eltiland.ui.common.components.datatable;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;

import java.util.List;

/**
 * DataTable with {@link AjaxNavigationToolbar} and {@link AjaxFallbackHeadersToolbar}.
 *
 * @param <T> The model object type
 */
public class EltiDataTable<T> extends DataTable<T> {
    private boolean isVisibleAllowedNoRecordsToolbar = false;

    /**
     * Default constructor. Uses {@link UIConstants#ROWS_PER_PAGE} value to set quantity of showed rows.
     *
     * @param id           wicket component id
     * @param columns      columns
     * @param dataProvider data provider
     */
    public EltiDataTable(String id, final List<IColumn<T>> columns,
                         ISortableDataProvider<T> dataProvider) {
        this(id, columns, dataProvider, UIConstants.ROWS_PER_PAGE);
    }

    /**
     * Constructor with 'rows per page' parameter.
     *
     * @param id           wicket component id
     * @param columns      columns
     * @param dataProvider data provider
     * @param rowsPerPage  rows per page
     */
    public EltiDataTable(String id, final List<IColumn<T>> columns,
                         ISortableDataProvider<T> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
        setOutputMarkupId(true);
        add(AttributeModifier.append("class", UIConstants.CLASS_DATATABLE));

        // add toolbars
        addBottomToolbar(new NoRecordsToolbar(this) {
            @Override
            public boolean isVisible() {
                return super.isVisible() && isVisibleAllowedNoRecordsToolbar;
            }
        });
        addBottomToolbar(new AjaxNavigationToolbar(this));
    }

    /**
     * Set enable (allow visibility) for NoRecordsToolbar.
     *
     * @param isEnable
     */
    public void setEnableNoRecordsToolbar(boolean isEnable) {
        isVisibleAllowedNoRecordsToolbar = isEnable;
    }
}
