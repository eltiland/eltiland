package com.eltiland.ui.common.components.label.multiselect;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.column.SelectorColumn;
import com.eltiland.ui.common.components.UIConstants;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.datatable.EltiDataTable;
import com.eltiland.ui.common.components.dialog.callback.IDialogSelectCallback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base panel for search and selecting any identifiable instance.
 *
 * @author knorr
 * @version 1.0
 * @since 8/13/12
 */
public abstract class GenericSelectorPanel<T extends Identifiable> extends Panel implements IDialogSelectCallback<T> {

    /**
     * Search results data table.
     */
    private EltiDataTable<T> dataGrid;

    /**
     * Search query text field.
     */
    private TextField<String> searchQueryField = new TextField<String>("searchQueryField", new Model<String>());

    /**
     * Start search button
     */
    private EltiAjaxSubmitLink<T> findButton = new EltiAjaxSubmitLink<T>("findButton") {

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            target.add(dataGrid);
        }
    };

    /**
     * Constructor default
     *
     * @param id          - wicket id
     * @param headerModel - panel header
     */
    public GenericSelectorPanel(String id, IModel<String> headerModel) {
        super(id);
        Form form = new Form("form");
        add(form);
        List<IColumn<T>> wrappedColumns = new ArrayList<>(createColumns());

        wrappedColumns.add(new SelectorColumn<T>() {
            @Override
            public void processSelection(IModel<T> selectedObjectModel, AjaxRequestTarget target) {
                if (callback != null) {
                    callback.process(selectedObjectModel, target);
                }
            }
        });
        add(dataGrid = new EltiDataTable<>(
                "dataGrid",
                wrappedColumns,
                createDataProvider(),
                UIConstants.LOWER_ROWS_PER_PAGE));
        dataGrid.setEnableNoRecordsToolbar(true);
        add(new Label("panelHeader", headerModel));
        form.add(searchQueryField);
        form.add(findButton);
        form.setMultiPart(true);
    }

    /**
     * Callback processors.
     */
    private IDialogActionProcessor<T> callback;

    @Override
    public void setSelectCallback(IDialogActionProcessor<T> callback) {
        this.callback = callback;
    }

    /**
     * <p/>
     * Implement this method to get real data for data table.
     * see {@link com.eltiland.ui.common.components.datatable.EltiDataProviderBase} as example data provider.
     * <p/>
     *
     * @return Sortable data provider
     */
    protected abstract ISortableDataProvider<T> createDataProvider();

    /**
     * Implement this method for construct real view for data table.
     *
     * @return Columns array.
     */
    protected abstract List<IColumn<T>> createColumns();

    /**
     * Return search query string.
     *
     * @return search Query.
     */
    protected IModel<String> getSearchQueryModel() {
        return searchQueryField.getModel();
    }
}
