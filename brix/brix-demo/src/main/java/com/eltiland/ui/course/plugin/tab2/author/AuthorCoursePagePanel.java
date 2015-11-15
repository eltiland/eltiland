package com.eltiland.ui.course.plugin.tab2.author;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Page for manage page of the author courses.
 *
 * @author Alex Plotnikov
 */
public class AuthorCoursePagePanel extends Panel {

    @SpringBean
    private ELTCourseManager courseManager;

    private ELTTable<AuthorCourse> grid;

    public AuthorCoursePagePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<AuthorCourse>("grid", 15) {
            @Override
            protected List<IColumn<AuthorCourse>> getColumns() {
                List<IColumn<AuthorCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn(new ResourceModel("authorLabel"), "author.name", "author.name"));
                columns.add(new AbstractColumn<AuthorCourse>(new ResourceModel("pageLabel")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<AuthorCourse>> cellItem,
                                             String componentId, IModel<AuthorCourse> rowModel) {
                        cellItem.add(new Label(componentId, getString(
                                rowModel.getObject().isModule() ? "module.page" : "author.page")));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getAuthorCourses(first, count, null).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getAuthorCoursesCount(null);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<AuthorCourse> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.REFRESH));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REFRESH:
                        return getString("change.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected void onClick(IModel<AuthorCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REFRESH: {
                        Boolean isModule = rowModel.getObject().isModule();
                        rowModel.getObject().setModule(!isModule);
                        try {
                            courseManager.update(rowModel.getObject());
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    default:
                        break;
                }
                target.add(grid);
            }
        };

        add(grid.setOutputMarkupId(true));
    }
}
