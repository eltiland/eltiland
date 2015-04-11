package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.model.webinar.WebinarUserPayment;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * Column for output full name of user, taking part in webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarUserNameColumn extends AbstractColumn<WebinarUserPayment> {
    public WebinarUserNameColumn(IModel<String> displayModel, String sortProperty) {
        super(displayModel, sortProperty);
    }

    public WebinarUserNameColumn(IModel<String> displayModel) {
        super(displayModel);
    }

    @Override
    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> cellItem,
                             String componentId, IModel<WebinarUserPayment> rowModel) {
        String name1 = rowModel.getObject().getUserSurname();
        String name2 = rowModel.getObject().getUserName();
        String name3 = rowModel.getObject().getPatronymic();
        String fullname = name1 + " " + name2;
        if (name3 != null) {
            fullname += " " + name3;
        }
        cellItem.add(new Label(componentId, fullname));
    }
}
