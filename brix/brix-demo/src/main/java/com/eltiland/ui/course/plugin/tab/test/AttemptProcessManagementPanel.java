package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.bl.test.TestAttemptManager;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.Iterator;
import java.util.List;

/**
 * Panel for output attempts in process.
 *
 * @author Aleksey Plotnikov
 */
public class AttemptProcessManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private TestAttemptManager testAttemptManager;

    private AttemptDataGrid grid;

    public AttemptProcessManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new AttemptDataGrid("grid", 20) {
            @Override
            protected List<IColumn<UserTestAttempt>> getColumns() {
                List<IColumn<UserTestAttempt>> columns = super.getColumns();
                columns.add(new PropertyColumn(new ResourceModel("currentAttempt"), "attemptCount", "attemptCount"));
                columns.add(new PropertyColumn(new ResourceModel("limitAttempt"), "attemptLimit", "attemptLimit"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return testAttemptManager.getProcessList(
                        first, count, getSort().getProperty(), getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return testAttemptManager.getProcessCount(null);
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
