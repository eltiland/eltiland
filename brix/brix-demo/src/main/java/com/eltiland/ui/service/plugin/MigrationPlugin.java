package com.eltiland.ui.service.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.workspace.Workspace;

/**
 * Migration Plugin.
 *
 * @author Aleksey Plotnikov.
 */
public class MigrationPlugin extends EltilandPlugin {
    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.migrate");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new MigrationPanel(panelId, workspaceIModel);
    }
}
