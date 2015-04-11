package com.eltiland.ui.common.components.datagrid;

import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Convenience implementation that adds {@link com.inmethod.grid.toolbar.paging.PagingToolbar} and {@link com.inmethod.grid.toolbar.NoRecordsToolbar} to the
 * grid.
 *
 * @param <D>
 *            datasource model object type = grid type
 * @param <T>
 *            row/item model object type
 *
 * @author Matej Knopp
 */
public class EltiDefaultDataGrid<D extends IDataSource<T>, T> extends EltiDataGrid<D, T>
{

	private static final long serialVersionUID = 1L;

	/**
	 * Crates a new {@link com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid} instance.
	 *
	 * @param id
	 *            component id
	 * @param model
	 *            model to access the {@link com.inmethod.grid.IDataSource} instance used to fetch the data
	 * @param columns
	 *            list of grid columns
	 */
	public EltiDefaultDataGrid(String id, IModel<D> model, List<IGridColumn<D, T>> columns)
	{
		super(id, model, columns);
		init();
	}

	/**
	 * Crates a new {@link com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid} instance.
	 *
	 * @param id
	 *            component id
	 * @param dataSource
	 *            data source used to fetch the data
	 * @param columns
	 *            list of grid columns
	 */
	public EltiDefaultDataGrid(String id, D dataSource, List<IGridColumn<D, T>> columns)
	{
		super(id, dataSource, columns);
		init();
	}

	private void init()
	{
		addBottomToolbar(new EltiNoRecordsToolbar<D, T>(this));
		addBottomToolbar(new EltiPagingToolbar<D, T>(this));
	}
}
