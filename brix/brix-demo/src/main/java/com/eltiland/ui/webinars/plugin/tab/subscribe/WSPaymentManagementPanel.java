package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.brixcms.workspace.Workspace;

/**
 * Панель платежей за абонементы.
 *
 * @author Aleksey Plotnikov
 */
public class WSPaymentManagementPanel extends BaseEltilandPanel<Workspace> {
    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WSPaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
