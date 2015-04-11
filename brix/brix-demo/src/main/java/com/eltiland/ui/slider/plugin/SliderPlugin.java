package com.eltiland.ui.slider.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.workspace.Workspace;

/**
 * Paid Groups management plugin.
 */
public class SliderPlugin extends EltilandPlugin {
    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.slider");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new SliderManagementPanel(panelId, workspaceIModel);
    }
}
