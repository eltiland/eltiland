package com.eltiland.ui.forum.panels.table;

import com.eltiland.model.forum.ForumThread;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Forum table grid.
 *
 * @author Aleksey Plotnikov.
 */
public class ThreadTable extends DataTable<ForumThread> {
    /**
     * Constructor
     *
     * @param id           component id
     * @param iColumns     list of IColumn objects
     * @param dataProvider imodel for data provider
     * @param rowsPerPage  number of rows per page
     */
    public ThreadTable(String id, List<IColumn<ForumThread>> iColumns,
                       IDataProvider<ForumThread> dataProvider, int rowsPerPage) {
        super(id, iColumns, dataProvider, rowsPerPage);
    }

    @Override
    protected Item<ForumThread> newRowItem(String id, int index, IModel<ForumThread> model) {
        return new OddEvenItem<>(id, index, model);
    }
}
