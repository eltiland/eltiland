package com.eltiland.ui.subscribe.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.workspace.Workspace;

/**
 * Paid Groups management plugin.
 */
public class SubscribePlugin extends EltilandPlugin {
    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.subscribe");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new SubscribeManagementPanel(panelId, workspaceIModel);
    }
}
