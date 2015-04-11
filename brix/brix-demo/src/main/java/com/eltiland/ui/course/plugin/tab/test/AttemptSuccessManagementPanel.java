package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.bl.test.TestAttemptManager;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.Iterator;

/**
 * Panel for output successfull attempts.
 *
 * @author Aleksey Plotnikov
 */
public class AttemptSuccessManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private TestAttemptManager testAttemptManager;

    private AttemptDataGrid grid;

    public AttemptSuccessManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new AttemptDataGrid("grid", 20) {
            @Override
            protected Iterator getIterator(int first, int count) {
                return testAttemptManager.getSuccessList(
                        first, count, getSort().getProperty(), getSort().isAscending(), getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return testAttemptManager.getSuccessCount(null);
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
