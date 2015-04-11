package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.webinars.plugin.components.WebinarPropertyPanel;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.brixcms.workspace.Workspace;

/**
 * Webinars announcements management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WCreationManagementPanel extends BaseEltilandPanel<Workspace> {

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WCreationManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(new WebinarPropertyPanel("createWebinarPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderJavaScriptReference(ResourcesUtils.JS_NUMBERFORMATTER);
        response.renderJavaScriptReference(ResourcesUtils.JS_TIMEPICKER);

        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
        response.renderCSSReference(ResourcesUtils.CSS_TIMEPICKER);
    }
}
