package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.model.webinar.Webinar;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * Column for output manager of the webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarModeratorColumn extends AbstractColumn<Webinar> {
    public WebinarModeratorColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);
    }

    public WebinarModeratorColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    @Override
    public void populateItem(Item<ICellPopulator<Webinar>> cellItem, String componentId, IModel<Webinar> rowModel) {
        String name = rowModel.getObject().getManagername() + " " + rowModel.getObject().getManagersurname();
        cellItem.add(new Label(componentId, name));
    }
}
