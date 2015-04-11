package com.eltiland.ui.course.plugin.tab.test;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;

/**
 * Courses test management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CourseTestManagementPanel extends BaseEltilandPanel<Workspace> {

    public CourseTestManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(
                        new AbstractWorkspaceTab(new ResourceModel("successTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new AttemptSuccessManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("processTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new AttemptProcessManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("limitTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new AttemptLimitManagementPanel(panelId, workspaceModel);
                            }
                        }
                )) {
            @Override
            protected String getTabContainerCssClass() {
                return "brix-plugins-tabbed-panel-row";
            }
        });
    }
}
