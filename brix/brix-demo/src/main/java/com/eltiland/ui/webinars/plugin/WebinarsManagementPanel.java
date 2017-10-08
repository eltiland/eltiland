package com.eltiland.ui.webinars.plugin;

import com.eltiland.bl.WebinarManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.webinars.plugin.tab.*;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.demo.web.admin.AdminPage;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Paid Groups management panel.
 */
public class WebinarsManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarsManagementPanel.class);

    @SpringBean
    private WebinarManager webinarManager;

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    protected WebinarsManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

     /*   try {
            webinarManager.authenticate();
        } catch (EltilandManagerException e) {
            LOGGER.error("Cannot authenticate to use Webinar.ru API", e);
            EltiStaticAlerts.registerErrorPopup(getString("errorAPIAccess"));
            throw new RestartResponseException(AdminPage.class);
        }*/

        add(new BrixTabbedPanel("pgTabPanel",
                Arrays.<IBrixTab>asList(new AbstractWorkspaceTab(new ResourceModel("announcementTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WAnnouncementManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("creationTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WCreationManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("subscriptionTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WSubscriptionManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("paymentTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WPaymentManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("recordTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WRecordManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("historyTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WHistoryManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("userTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WUserManagementPanel(panelId, workspaceModel);
                                            }
                                        }, new AbstractWorkspaceTab(new ResourceModel("invoiceTab"), workspaceIModel) {
                                            @Override
                                            public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                                                return new WInvoiceManagementPanel(panelId, workspaceModel);
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
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);
    }
}
