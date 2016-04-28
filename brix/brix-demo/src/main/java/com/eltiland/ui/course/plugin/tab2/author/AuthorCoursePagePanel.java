package com.eltiland.ui.course.plugin.tab2.author;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
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
 * Page for manage page of the author courses.
 *
 * @author Alex Plotnikov
 */
public class AuthorCoursePagePanel extends Panel {

    @SpringBean
    private ELTCourseManager courseManager;

    private ELTTable<AuthorCourse> authorGrid, moduleGrid;

    public AuthorCoursePagePanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        authorGrid = new ELTTable<AuthorCourse>("authorGrid", 10) {
            @Override
            protected List<IColumn<AuthorCourse>> getColumns() {
                List<IColumn<AuthorCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn(new ResourceModel("authorLabel"), "author.name", "author.name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getSortedAuthorCourses(first, count, false).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getAuthorCoursesCount(false);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<AuthorCourse> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.REFRESH, GridAction.UP, GridAction.DOWN));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REFRESH:
                        return getString("change.tooltip");
                    case UP:
                        return getString("up.tooltip");
                    case DOWN:
                        return getString("down.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected void onClick(IModel<AuthorCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REFRESH: {
                        try {
                            courseManager.changeAuthorCourseType(rowModel.getObject());
                            target.add(authorGrid);
                            target.add(moduleGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    case UP: {
                        try {
                            courseManager.moveAuthorCourse(rowModel.getObject(), true, false);
                            target.add(authorGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    case DOWN: {
                        try {
                            courseManager.moveAuthorCourse(rowModel.getObject(), false, false);
                            target.add(authorGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<AuthorCourse> rowModel) {
                switch (action) {
                    case UP:
                        return rowModel.getObject().getIndex() > 0;
                    case DOWN:
                        Integer index = rowModel.getObject().getIndex();
                        int count = courseManager.getAuthorCoursesCount(false);

                        return index < (count - 1);
                    default:
                        return true;
                }
            }
        };

        moduleGrid = new ELTTable<AuthorCourse>("moduleGrid", 10) {
            @Override
            protected List<IColumn<AuthorCourse>> getColumns() {
                List<IColumn<AuthorCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn(new ResourceModel("authorLabel"), "author.name", "author.name"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getSortedAuthorCourses(first, count, true).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getAuthorCoursesCount(true);
            }

            @Override
            protected List<GridAction> getGridActions(IModel<AuthorCourse> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.REFRESH, GridAction.UP, GridAction.DOWN));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REFRESH:
                        return getString("change.tooltip");
                    case UP:
                        return getString("up.tooltip");
                    case DOWN:
                        return getString("down.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected void onClick(IModel<AuthorCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REFRESH: {
                        try {
                            courseManager.changeAuthorCourseType(rowModel.getObject());
                            target.add(authorGrid);
                            target.add(moduleGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    case UP: {
                        try {
                            courseManager.moveAuthorCourse(rowModel.getObject(), true, true);
                            target.add(moduleGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    case DOWN: {
                        try {
                            courseManager.moveAuthorCourse(rowModel.getObject(), false, true);
                            target.add(moduleGrid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<AuthorCourse> rowModel) {
                switch (action) {
                    case UP:
                        return rowModel.getObject().getIndex() > 0;
                    case DOWN:
                        Integer index = rowModel.getObject().getIndex();
                        int count = courseManager.getAuthorCoursesCount(true);

                        return index < (count - 1);
                    default:
                        return true;
                }
            }
        };

        add(authorGrid.setOutputMarkupId(true));
        add(moduleGrid.setOutputMarkupId(true));
    }
}
