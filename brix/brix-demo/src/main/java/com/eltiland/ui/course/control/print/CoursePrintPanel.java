package com.eltiland.ui.course.control.print;

import com.eltiland.bl.course.CoursePrintStatManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
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
                Long limit = rowModel.getObject().getPrintLimit();
                if (action == GridAction.UP) {
                    rowModel.getObject().setPrintLimit(limit + 1);
                }
                if (action == GridAction.DOWN) {
                    rowModel.getObject().setPrintLimit(limit - 1);
                }

                if (action == GridAction.UP || action == GridAction.DOWN) {
                    try {
                        coursePrintStatManager.update(rowModel.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<CourseItemPrintStat> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.UP, GridAction.DOWN));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case UP:
                        return getString("up.action");
                    case DOWN:
                        return getString("down.action");
                    default:
                        return StringUtils.EMPTY_STRING;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<CourseItemPrintStat> rowModel) {
                return !action.equals(GridAction.DOWN) || rowModel.getObject().getPrintLimit() > 0;
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
