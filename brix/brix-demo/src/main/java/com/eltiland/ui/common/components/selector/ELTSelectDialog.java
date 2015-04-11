package com.eltiland.ui.common.components.selector;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.dialog.Dialog;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Selector dialog control.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTSelectDialog<T extends Identifiable> extends Dialog<ELTSelectorPanel<T>> {

    private ELTSelectorPanel<T> dialogPanel;

    /**
     * Dialog ctor.
     *
     * @param id           markup id.
     * @param initialWidth initial width,
     */
    public ELTSelectDialog(String id, int initialWidth) {
        super(id, initialWidth);
    }

    /**
     * Dialog ctor.
     *
     * @param id            markup id.
     * @param initialWidth  initial width.
     * @param initialHeight initial height.
     */
    public ELTSelectDialog(String id, int initialWidth, int initialHeight) {
        super(id, initialWidth, initialHeight);
    }

    @Override
    public ELTSelectorPanel<T> createDialogPanel(String id) {
        dialogPanel = new ELTSelectorPanel<T>(id, getMaxRows(), getSelectedIds()) {
            @Override
            protected String getHeader() {
                return ELTSelectDialog.this.getHeader();
            }

            @Override
            protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
                ELTSelectDialog.this.onSelect(target, selectedIds);
            }

            @Override
            protected List<IColumn<T>> getColumns() {
                return ELTSelectDialog.this.getColumns();
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return ELTSelectDialog.this.getIterator(first, count);
            }

            @Override
            protected int getSize() {
                return ELTSelectDialog.this.getSize();
            }

            @Override
            protected String getSearchPlaceholder() {
                return ELTSelectDialog.this.getSearchPlaceholder();
            }

            @Override
            protected boolean isSearching() {
                return ELTSelectDialog.this.isSearching();
            }
        };
        return dialogPanel;
    }

    /**
     * @return maximum count of rows in the table.
     */
    protected abstract int getMaxRows();

    /**
     * @return header of the dialog.
     */
    protected abstract String getHeader();

    /**
     * Event handler of user pressing button 'Select'
     *
     * @param target      ajax request target.
     * @param selectedIds array of the ID's of the selected items.
     */
    protected abstract void onSelect(AjaxRequestTarget target, List<Long> selectedIds);

    /**
     * @return array of the columns of the table.
     */
    protected abstract List<IColumn<T>> getColumns();

    /**
     * @return iterator of the data provider of the table.
     */
    protected abstract Iterator getIterator(int first, int count);

    /**
     * @return size of the items of the data provider of the table.
     */
    protected abstract int getSize();

    /**
     * @return search placeholder.
     */
    protected String getSearchPlaceholder() {
        return StringUtils.EMPTY;
    }


    protected List<Long> getSelectedIds() {
        return new ArrayList<>();
    }

    protected SortParam getSort() {
        return dialogPanel.getSort();
    }

    protected String getSearchString() {
        return dialogPanel != null ? dialogPanel.getSearchString() : StringUtils.EMPTY;
    }

    protected boolean isSearching() {
        return true;
    }
}
