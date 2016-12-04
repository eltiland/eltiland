package com.eltiland.ui.course.control.webinar;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
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
 * Panel for controlling webinars of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseWebinarPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;

    private ELTTable<ELTWebinarCourseItem> grid;


    public CourseWebinarPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        grid = new ELTTable<ELTWebinarCourseItem>("grid", 20) {
            @Override
            protected List<IColumn<ELTWebinarCourseItem>> getColumns() {
                List<IColumn<ELTWebinarCourseItem>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<ELTWebinarCourseItem>(
                        new ResourceModel("name.column"), "name", "name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getWebinars(CourseWebinarPanel.this.getModelObject(),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getWebinarsCount(CourseWebinarPanel.this.getModelObject());
            }

            @Override
            protected void onClick(IModel<ELTWebinarCourseItem> rowModel, GridAction action, AjaxRequestTarget target) {

            }

            @Override
            protected String getNotFoundedMessage() {
                return CourseWebinarPanel.this.getString("no.webinars");
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
