package com.eltiland.ui.news.plugin;

import com.eltiland.ui.common.plugin.EltilandPlugin;
import com.eltiland.ui.news.page.NewsPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.brixcms.Brix;
import org.brixcms.workspace.Workspace;

/**
 * Plugin manages eltiland's news. News are stored in DB.
 *
 * @see com.eltiland.model.NewsItem
 */
public class NewsPlugin extends EltilandPlugin {
    public NewsPlugin(Brix brix) {

        // register news page
        WebApplication.get().mountPage("/news", NewsPage.class);
    }

    @Override
    public IModel<String> getTabName() {
        return new ResourceModel("tab.news");
    }

    @Override
    public Panel getTabPanel(String panelId, IModel<Workspace> workspaceIModel) {
        return new NewsManagementPanel(panelId, workspaceIModel);
    }
}
