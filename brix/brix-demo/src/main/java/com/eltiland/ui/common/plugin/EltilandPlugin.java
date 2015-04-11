package com.eltiland.ui.common.plugin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.Plugin;
import org.brixcms.demo.web.DemoBrix;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract eltiland plugin.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class EltilandPlugin implements Plugin, Serializable {
    @Override
    public String getId() {
        return this.getClass().getName();
    }

    @Override
    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return null;
    }

    @Override
    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        SitePlugin sitePlugin = SitePlugin.get(DemoBrix.get());
        return sitePlugin.getWorkspaces(currentWorkspace, isFrontend);
    }

    @Override
    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
    }

    @Override
    public boolean isPluginWorkspace(Workspace workspace) {
        SitePlugin sitePlugin = SitePlugin.get(DemoBrix.get());
        return sitePlugin.isPluginWorkspace(workspace);
    }

    @Override
    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        return Arrays.<IBrixTab>asList(new AbstractWorkspaceTab(getTabName(), workspaceModel, 100) {
            @Override
            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                return getTabPanel(panelId, workspaceModel);
            }
        });
    }

    public abstract IModel<String> getTabName();

    public abstract Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel);
}
