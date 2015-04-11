package com.eltiland.ui.common.components.grid;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * General ELT table control.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTCheckTable<T extends Identifiable> extends ELTTable<T> {

    private List<Long> selectedIds = new ArrayList<>();

    /**
     * Panel constructor.
     *
     * @param id      markup id.
     * @param maxRows maximum count of rows to output.
     */
    public ELTCheckTable(String id, int maxRows) {
        super(id, maxRows);
    }

    /**
     * Panel constructor.
     *
     * @param id          markup id.
     * @param maxRows     maximum count of rows to output.
     * @param selectedIds list of id's of the preselected items in the table.
     */
    public ELTCheckTable(String id, int maxRows, List<Long> selectedIds) {
        super(id, maxRows);
        this.selectedIds = selectedIds;
    }

    @Override
    protected IColumn<T> getFirstColumn() {
        return new AbstractColumn<T>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {
                Long id = rowModel.getObject().getId();
                boolean selected = selectedIds.contains(id);

                cellItem.add(new CheckPanel(componentId, selected) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target, boolean newValue) {
                        if (newValue) {
                            selectedIds.add(rowModel.getObject().getId());
                        } else {
                            selectedIds.remove(rowModel.getObject().getId());
                        }
                    }
                });
            }

        };
    }

    /**
     * @return list of ID's of selected items.
     */
    public List<Long> getSelectedIds() {
        return selectedIds;
    }

    /**
     * Panel constructor.
     *
     * @param selectedIds list of id's of the preselected items in the table.
     */
    public void setSelectedIds(List<Long> selectedIds) {
        this.selectedIds = selectedIds;
    }

    private abstract class CheckPanel extends BaseEltilandPanel {

        public CheckPanel(String id, boolean value) {
            super(id);

            add(new ELTAjaxCheckBox("checkBox", new Model<String>(), new Model<>(value)) {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    CheckPanel.this.onUpdate(target, getModelObject());
                }
            });
        }

        protected abstract void onUpdate(AjaxRequestTarget target, boolean newValue);
    }
}