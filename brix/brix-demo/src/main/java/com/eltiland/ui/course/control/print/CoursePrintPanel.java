package com.eltiland.ui.course.control.print;

import com.eltiland.bl.course.CoursePrintStatManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for controlling print statistics of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePrintPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private CoursePrintStatManager coursePrintStatManager;

    private ELTTable<CourseItemPrintStat> grid;

    public CoursePrintPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        grid = new ELTTable<CourseItemPrintStat>("grid", 20) {
            @Override
            protected List<IColumn<CourseItemPrintStat>> getColumns() {
                List<IColumn<CourseItemPrintStat>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<CourseItemPrintStat>(
                        new ResourceModel("name.column"), "listener.name", "listener.name"));
                columns.add(new PropertyColumn<CourseItemPrintStat>(
                        new ResourceModel("item.column"), "item.name", "item.name"));
                columns.add(new PropertyColumn<CourseItemPrintStat>(
                        new ResourceModel("count.column"), "currentPrint", "currentPrint"));
                columns.add(new PropertyColumn<CourseItemPrintStat>(
                        new ResourceModel("limit.column"), "printLimit", "printLimit"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return coursePrintStatManager.getItems(
                        getModelObject(), first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return coursePrintStatManager.getCount(getModelObject());
            }

            @Override
            protected void onClick(IModel<CourseItemPrintStat> rowModel, GridAction action, AjaxRequestTarget target) {

            }
        };

        add(grid.setOutputMarkupPlaceholderTag(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
