package com.eltiland.ui.course.plugin.tab2;

import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.model.course2.listeners.ELTCourseListener;
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
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Course payments management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePaymentManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private ELTCourseListenerManager listenerManager;

    private ELTTable<ELTCourseListener> grid;

    public CoursePaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<ELTCourseListener>("grid", 20) {
            @Override
            protected List<IColumn<ELTCourseListener>> getColumns() {
                List<IColumn<ELTCourseListener>> columns = new ArrayList<>();
                columns.add(new PropertyColumn(new ResourceModel("date.label"), "payDate", "payDate"));
                columns.add(new PropertyColumn(new ResourceModel("listener.label"), "listener.name", "listener.name"));
                columns.add(new PropertyColumn(new ResourceModel("course.label"), "course.name", "course.name"));
                columns.add(new PropertyColumn(new ResourceModel("price.label"), "price", "price"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return listenerManager.getConfirmedListeners(
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return listenerManager.getConfirmedListenersCount();
            }

            @Override
            protected void onClick(IModel<ELTCourseListener> rowModel, GridAction action, AjaxRequestTarget target) {

            }
        };

        add(grid);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
