package com.eltiland.ui.common.components.datagrid;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.toolbar.AbstractToolbar;
import com.inmethod.grid.toolbar.paging.PagingNavigator;
import org.apache.wicket.Component;

/**
 * Toolbar that displays a paging navigator and a label with message about which rows are being
 * displayed and their total number in the data table.
 * 
 * This toolbar can only be added to {@link org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable}
 *
 * @param <D>
 *            datasource model object type = grid type
 * @param <T>
 *            row/item model object type
 *
 * @author Matej Knopp
 */
public class EltiPagingToolbar<D extends IDataSource<T>, T> extends AbstractToolbar<D, T>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the {@link com.inmethod.grid.datagrid.DataGrid} to which this toolbar belongs.
	 *
	 * @return data grid
	 */
	public EltiDataGrid<D, T> getDataGrid()
	{
		return (EltiDataGrid<D, T>)super.getGrid();
	}

	/**
	 * Constructor
	 *
	 * @param grid
	 *            data grid
	 */
	public EltiPagingToolbar(EltiDataGrid<D, T> grid)
	{
		super(grid, null);

		add(newPagingNavigator("navigator"));
		add(newNavigationLabel("navigationLabel"));
	}

	protected Component newNavigationLabel(String id)
	{
		return new EltiNavigatorLabel(id, getDataGrid());
	}

	protected Component newPagingNavigator(String id)
	{
		return new PagingNavigator(id, getDataGrid());
	}

	/**
	 * Important to prevent early initialization of QueryResult at AbstractPageableView. The
	 * isVisible method can be called during an early step in the form process and the QuertyResult
	 * initialization can fail if it depend upon form components
	 */
	@Override
	protected void onConfigure()
	{
		super.onConfigure();
		setVisible(getDataGrid().getTotalRowCount() != 0);
	}
}
