package com.eltiland.ui.course.plugin.tab2;

import com.eltiland.bl.CountableManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.course.CourseContentPage;
import com.eltiland.ui.course.CourseControlPage;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Course management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseControlManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private ELTCourseManager eltCourseManager;
    @SpringBean
    private CountableManager<AuthorCourse> countableManager;

    private ELTTable<ELTCourse> grid;

    public CourseControlManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<ELTCourse>("grid", 15) {
            @Override
            protected List<IColumn<ELTCourse>> getColumns() {
                List<IColumn<ELTCourse>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("nameLabel"), "name", "name"));
                columns.add(new PropertyColumn(new ResourceModel("authorLabel"), "author.name", "author.name"));
                columns.add(new AbstractColumn<ELTCourse>(new ResourceModel("typeLabel")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourse>> cellItem,
                                             String componentId, IModel<ELTCourse> rowModel) {
                        Label label = new Label(componentId, getString(
                                rowModel.getObject().getClass().getSimpleName() + ".class"));
                        cellItem.add(label);
                    }
                });
                columns.add(new PropertyColumn(new ResourceModel("dateLabel"), "creationDate", "creationDate"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return eltCourseManager.getCourseList(
                        new ArrayList<>(Arrays.asList(CourseStatus.CONFIRMED, CourseStatus.PUBLISHED)),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return eltCourseManager.getCourseListCount(
                        Arrays.asList(CourseStatus.CONFIRMED, CourseStatus.PUBLISHED));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourse> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.APPLY, GridAction.REMOVE, GridAction.SETTINGS));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case APPLY:
                        return getString("apply.tooltip");
                    case REMOVE:
                        return getString("deny.tooltip");
                    case SETTINGS:
                        return getString("settings.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTCourse> rowModel) {
                switch (action) {
                    case SETTINGS:
                        return true;
                    case APPLY:
                        return rowModel.getObject().getStatus().equals(CourseStatus.CONFIRMED);
                    case REMOVE:
                        return rowModel.getObject().getStatus().equals(CourseStatus.PUBLISHED);
                    default:
                        return false;
                }
            }

            @Override
            protected void onClick(IModel<ELTCourse> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case APPLY:
                        rowModel.getObject().setStatus(CourseStatus.PUBLISHED);
                        try {
                            eltCourseManager.publish(rowModel.getObject());
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("publishMessage"), target);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case REMOVE:
                        rowModel.getObject().setStatus(CourseStatus.CONFIRMED);
                        try {
                            eltCourseManager.unPublish(rowModel.getObject());
                            target.add(grid);
                            ELTAlerts.renderOKPopup(getString("cancelMessage"), target);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case SETTINGS:
                        throw new RestartResponseException(CourseControlPage.class,
                                new PageParameters()
                                        .add(CourseContentPage.PARAM_ID, rowModel.getObject().getId()));
                }
            }
        };
        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
