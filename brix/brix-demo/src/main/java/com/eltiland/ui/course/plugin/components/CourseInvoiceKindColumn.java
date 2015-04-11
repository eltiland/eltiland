package com.eltiland.ui.course.plugin.components;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Column for output kind of course paid invoice.
 *
 * @author Aleksey Plotnikov
 */
public class CourseInvoiceKindColumn<T, I extends CoursePaidInvoice> extends AbstractColumn<T, I> {
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;

    public CourseInvoiceKindColumn(String columnId, IModel<String> headerModel) {
        super(columnId, headerModel);
        Injector.get().inject(this);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<I> rowModel) {
        return new KindPanel(componentId, rowModel);
    }

    private class KindPanel extends BaseEltilandPanel<I> {
        protected KindPanel(String id, IModel<I> iiModel) {
            super(id, iiModel);

            String value;
            CoursePaidInvoice invoice = iiModel.getObject();
            if (invoice.getPrice().intValue() == 0) {
                value = "removeStatus";
            } else {
                if (invoice.getItem() == null) {
                    if (coursePaidInvoiceManager.isCoursePaid(invoice.getCourse())) {
                        value = "editStatus";
                    } else {
                        value = "createStatus";
                    }
                } else {
                    if (coursePaidInvoiceManager.isBlockPaid(invoice.getItem())) {
                        value = "editStatus";
                    } else {
                        value = "createStatus";
                    }
                }
            }
            add(new Label("kind", getString(value)));
        }
    }
}
