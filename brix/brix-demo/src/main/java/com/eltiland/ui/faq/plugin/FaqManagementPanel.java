package com.eltiland.ui.faq.plugin;

import com.eltiland.model.faq.Faq;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.faq.plugin.tab.FaqCategoryManagementPanel;
import com.eltiland.ui.faq.plugin.tab.FaqQuestionManagementPanel;
import com.eltiland.ui.faq.plugin.tab.FaqUserManagementPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;

/**
 * Paid Groups management panel.
 */
public class FaqManagementPanel extends BaseEltilandPanel<Workspace> {

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected FaqManagementPanel(String id, final IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(new BrixTabbedPanel("faqPanel",
                Arrays.<IBrixTab>asList(new AbstractWorkspaceTab(new ResourceModel("categoryTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new FaqCategoryManagementPanel(
                                                        panelId, workspaceIModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("faqTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new FaqUserManagementPanel(
                                                        panelId, workspaceIModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("questionTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new FaqQuestionManagementPanel(panelId, workspaceIModel);
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
