package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.webinars.plugin.tab.subscribe.WSControlManagementPanel;
import com.eltiland.ui.webinars.plugin.tab.subscribe.WSPaymentManagementPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;

/**
 * Панель управления подписками на вебинары.
 *
 * @author Aleksey Plotnikov
 */
public class WSubscriptionManagementPanel extends BaseEltilandPanel<Workspace> {
    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WSubscriptionManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(new AbstractWorkspaceTab(new ResourceModel("controlTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WSControlManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("paymentTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WSPaymentManagementPanel(panelId, workspaceModel);
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
