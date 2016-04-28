package com.eltiland.ui.course.plugin.tab2.author;

import com.eltiland.bl.CountableManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CountableException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Page for manage order of appearance of the author courses.
 */
public class AuthorCourseOrderPanel extends Panel {
    @SpringBean
    private ELTCourseManager courseManager;
    @SpringBean
    private CountableManager<AuthorCourse> countableManager;

    private ELTTable<ELTCourse> grid;

    public AuthorCourseOrderPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<ELTCourse>("grid", 15) {
            @Override
            protected List<IColumn<ELTCourse>> getColumns() {
                List<IColumn<ELTCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn(new ResourceModel("authorLabel"), "author.name", "author.name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getSortedAuthorCourses(first, count, null).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getAuthorCoursesCount(null);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourse> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.UP, GridAction.DOWN));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case UP:
                        return getString("up.tooltip");
                    case DOWN:
                        return getString("down.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTCourse> rowModel) {
                int index = ((AuthorCourse) rowModel.getObject()).getIndex();
                int count = courseManager.getAuthorCoursesCount(null);
                switch (action) {
                    case UP:
                        return index > 0;
                    case DOWN:
                        return (index + 1) < count;
                    default:
                        return false;
                }
            }

            @Override
            protected void onClick(IModel<ELTCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case UP:
                        try {
                            countableManager.moveUp((AuthorCourse) rowModel.getObject());
                        } catch (CountableException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case DOWN:
                        try {
                            countableManager.moveDown((AuthorCourse) rowModel.getObject());
                        } catch (CountableException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                }
                target.add(grid);
            }
        };

        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
