package com.eltiland.ui.subscribe.plugin;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.subscribe.plugin.tab.email.EmailManagementPanel;
import com.eltiland.ui.subscribe.plugin.tab.subscriber.SubscriberManagementPanel;
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
public class SubscribeManagementPanel extends BaseEltilandPanel<Workspace> {

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected SubscribeManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(
                        new AbstractWorkspaceTab(new ResourceModel("subscribersTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new SubscriberManagementPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("emailsTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new EmailManagementPanel(panelId, workspaceModel);
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
