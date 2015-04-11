package com.eltiland.ui.common.components.selector;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.grid.ELTCheckTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.List;

/**
 * Dialog panel for selector dialog.
 * Contains check table and Button "select".
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTSelectorPanel<T extends Identifiable> extends BaseEltilandPanel {

    private ELTCheckTable<T> grid;

    public ELTSelectorPanel(String id, int maxRows, List<Long> selectedIds) {
        super(id);
        grid = new ELTCheckTable<T>("grid", maxRows, selectedIds) {

            @Override
            protected List<IColumn<T>> getColumns() {
                return ELTSelectorPanel.this.getColumns();
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return ELTSelectorPanel.this.getIterator(first, count);
            }

            @Override
            protected int getSize() {
                return ELTSelectorPanel.this.getSize();
            }

            @Override
            protected void onClick(IModel<T> rowModel, GridAction action, AjaxRequestTarget target) {
            }

            @Override
            protected boolean isSearching() {
                return ELTSelectorPanel.this.isSearching();
            }

            @Override
            protected String getSearchPlaceHolder() {
                return ELTSelectorPanel.this.getSearchPlaceholder();
            }
        };
        add(grid);

        add(new EltiAjaxLink("selectButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onSelect(target, grid.getSelectedIds());
            }
        });

        add(new Label("header", getHeader()));
    }

    /**
     * set ID's of selected entities.
     *
     * @param selectedIds list of the ID's of the selected items.
     */
    public void setSelectedIds(List<Long> selectedIds) {
        grid.setSelectedIds(selectedIds);
    }

    /**
     * @return search string.
     */
    public String getSearchString() {
        return grid.getSearchString();
    }

    protected abstract String getHeader();

    /**
     * Event handler, triggered when user push Select button.
     *
     * @param target      ajax target.
     * @param selectedIds ID's of the selected entities.
     */
    protected abstract void onSelect(AjaxRequestTarget target, List<Long> selectedIds);

    /**
     * @return columns of grid.
     */
    protected abstract List<IColumn<T>> getColumns();

    /**
     * @return data provider iterator.
     */
    protected abstract Iterator getIterator(int first, int count);

    /**
     * @return data provider list size.
     */
    protected abstract int getSize();

    /**
     * @return sort param.
     */
    public SortParam getSort() {
        return grid.getSort();
    }


    /**
     * @return placeholder for search field.
     */
    protected abstract String getSearchPlaceholder();

    /**
     * @return TRUE if search field will be available.
     */
    protected abstract boolean isSearching();

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
