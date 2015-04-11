package com.eltiland.ui.magazine.plugin;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.magazine.plugin.tab.MagazineListPanel;
import com.eltiland.ui.magazine.plugin.tab.MagazinePaymentPanel;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;

/**
 * Magazine management panel.
 */
public class MagazineManagementPanel extends BaseEltilandPanel<Workspace> {
    public MagazineManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(
                        new AbstractWorkspaceTab(new ResourceModel("listTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new MagazineListPanel(panelId, workspaceModel);
                            }
                        },
                        new AbstractWorkspaceTab(new ResourceModel("paymentsTab"), workspaceIModel) {
                            @Override
                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                return new MagazinePaymentPanel(panelId, workspaceModel);
                            }
                        }
                )) {
            @Override
            protected String getTabContainerCssClass() {
                return "brix-plugins-tabbed-panel-row";
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_COMPONENTS);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);
    }
}
