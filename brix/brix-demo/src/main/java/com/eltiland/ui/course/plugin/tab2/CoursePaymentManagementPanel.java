package com.eltiland.ui.course.plugin.tab2;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.model.IModel;
import org.brixcms.workspace.Workspace;

/**
 * Course payments management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePaymentManagementPanel extends BaseEltilandPanel<Workspace> {
    public CoursePaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
    }
}
