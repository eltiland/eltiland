package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.model.webinar.WebinarUserPayment;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * Column for output full name of user, taking part in webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarFullNameColumn<T> extends AbstractColumn<T, WebinarUserPayment> {

    public WebinarFullNameColumn() {
        super("fioColumn", new ResourceModel("fioColumnTitle"), "userSurname");
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<WebinarUserPayment> rowModel) {
        String name1 = rowModel.getObject().getUserSurname();
        String name2 = rowModel.getObject().getUserName();
        String name3 = rowModel.getObject().getPatronymic();
        String fullname = name1 + " " + name2;
        if (name3 != null) {
            fullname += " " + name3;
        }
        return new Label(componentId, fullname);
    }

    @Override
    public int getInitialSize() {
        return 235;
    }
}
