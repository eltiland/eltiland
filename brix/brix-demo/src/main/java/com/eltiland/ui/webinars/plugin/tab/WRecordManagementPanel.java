package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.webinars.plugin.tab.record.WRControlManagementPanel;
import com.eltiland.ui.webinars.plugin.tab.record.WRInvoiceManagementPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Webinars records management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WRecordManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WRecordManagementPanel.class);

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WRecordManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(new AbstractWorkspaceTab(new ResourceModel("controlTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WRControlManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("invoiceRecordTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WRInvoiceManagementPanel(panelId, workspaceModel);
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
