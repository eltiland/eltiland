package com.eltiland.ui.common.column;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.demo.web.WrapperPanel;

/**
 * @author knorr
 * @version 1.0
 * @since 8/28/12
 */
public abstract class SelectorColumn<T extends Identifiable> extends AbstractColumn<T> {

    public SelectorColumn() {
        super(ReadonlyObjects.EMPTY_DISPLAY_MODEL);
    }


    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, final IModel<T> rowModel) {
        cellItem.add(new WrapperPanel(componentId, new EltiAjaxLink<Long>(WrapperPanel.INNER_PANEL_ID,
                new Model<Long>(rowModel.getObject().getId())) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                processSelection(new GenericDBModel<T>((Class<T>) rowModel.getObject().getClass(),
                        rowModel.getObject().getId()), target);
            }
        }.setBody(new ResourceModel("select"))));
    }

    public abstract void processSelection(IModel<T> selectedObjectModel, AjaxRequestTarget target);
}
