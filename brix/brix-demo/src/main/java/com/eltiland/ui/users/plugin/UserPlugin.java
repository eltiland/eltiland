package com.eltiland.ui.users.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.workspace.Workspace;

/**
 * User management plugin.
 */
public class UserPlugin extends EltilandPlugin {
    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.users");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new UserManagementPanel(panelId, workspaceIModel);
    }
}
