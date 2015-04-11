package com.eltiland.ui.course.plugin.components;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Column for output course paid invoice item information.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseItemColumn<T, I extends CoursePaidInvoice> extends AbstractColumn<T, I> {
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;


    public CourseItemColumn(String columnId, IModel<String> headerModel) {
        super(columnId, headerModel);
        Injector.get().inject(this);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel rowModel) {
        return new ItemPanel(componentId, rowModel);
    }

    @Override
    public int getInitialSize() {
        return 280;
    }

    @Override
    public boolean getWrapText() {
        return true;
    }

    private class ItemPanel extends BaseEltilandPanel<CoursePaidInvoice> {
        protected ItemPanel(String id, IModel<CoursePaidInvoice> coursePaidInvoiceIModel) {
            super(id, coursePaidInvoiceIModel);
            CoursePaidInvoice invoice =
                    coursePaidInvoiceManager.fetchCoursePaidInvoice(coursePaidInvoiceIModel.getObject());
            FolderCourseItem item = invoice.getItem();
            add(new Label("course", invoice.getCourse().getName()));

            Label itemLabel = new Label("item", new Model<String>());
            if (item != null) {
                itemLabel.setDefaultModelObject(String.format(getString("taskLabel"), item.getName()));
            }
            add(itemLabel.setVisible(item != null));
        }
    }
}
