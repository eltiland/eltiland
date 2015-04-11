package com.eltiland.ui.pei.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.workspace.Workspace;

/**
 * PEI management plugin.
 */
public class PeiPlugin extends EltilandPlugin {
    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.pei");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new PeiManagementPanel(panelId, workspaceIModel);
    }
}
