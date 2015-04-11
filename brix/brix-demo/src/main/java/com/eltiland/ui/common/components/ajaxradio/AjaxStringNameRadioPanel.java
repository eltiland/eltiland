package com.eltiland.ui.common.components.ajaxradio;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.value.ValueMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Ajax radiobox panel adapted for our panels.
 *
 * @author Aleksey Plotnikov
 */
public abstract class AjaxStringNameRadioPanel extends AjaxRadioPanel<ValueMap> {

    private IModel<List<ValueMap>> listModel = new ListModel<>();

    private int currentSelection = 0;

    /**
     * Panel constructor.
     *
     * @param id             markup id.
     * @param selectionIndex selection index
     */
    public AjaxStringNameRadioPanel(String id, int selectionIndex) {
        super(id);

        List<ValueMap> values = new ArrayList<>();
        for (String value : getList()) {
            values.add(newValue(value));
        }

        listModel.setObject(values);
        currentSelection = selectionIndex;
    }

    private ValueMap newValue(String valueName) {
        ValueMap map = new ValueMap();
        map.put("name", valueName);
        return map;
    }

    @Override
    protected void onRadioSelect(AjaxRequestTarget target, ValueMap newSelection) {
        currentSelection = listModel.getObject().indexOf(newSelection);
        onRadioSelect(target, currentSelection);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        Component oldForm = get("form");
        if (oldForm != null) {
            oldForm.remove();
        }
        add(buildForm(listModel.getObject(), listModel.getObject().get(currentSelection), "name"));
    }

    /**
     * @return list of string values for radio panel.
     */
    protected abstract List<String> getList();

    protected abstract void onRadioSelect(AjaxRequestTarget target, int selectionIndex);
}
