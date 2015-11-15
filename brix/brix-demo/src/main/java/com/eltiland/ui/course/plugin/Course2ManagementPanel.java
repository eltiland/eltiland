package com.eltiland.ui.course.plugin;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.course.plugin.tab2.author.CourseAuthorManagementPanel;
import com.eltiland.ui.course.plugin.tab2.CourseControlManagementPanel;
import com.eltiland.ui.course.plugin.tab2.CourseInvoiceManagementPanel;
import com.eltiland.ui.course.plugin.tab2.CoursePaymentManagementPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;

/**
 * Course management Panel.
 *
 * @author Aleksey Plotnikov
 */
public class Course2ManagementPanel extends BaseEltilandPanel<Workspace> {

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected Course2ManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(
                        new AbstractWorkspaceTab(new ResourceModel("invoiceTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new CourseInvoiceManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("controlTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new CourseControlManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("paymentsTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new CoursePaymentManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("authorTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new CourseAuthorManagementPanel(panelId, workspaceModel);
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
