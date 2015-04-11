package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.webinar.Webinar;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Column for output manager of the webinar.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarManagerColumn<T> extends AbstractColumn<T, Webinar> {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    /**
     * Column constructor.
     *
     * @param columnId    column ID.
     * @param headerModel header label model.
     */
    public WebinarManagerColumn(String columnId, IModel<String> headerModel) {
        super(columnId, headerModel);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<Webinar> rowModel) {
        Webinar webinar = rowModel.getObject();
        return new Label(componentId, webinar.getManagername() + " " + webinar.getManagersurname());
    }

    @Override
    public int getInitialSize() {
        return 200;
    }

    @Override
    public boolean getWrapText() {
        return true;
    }
}
