package com.eltiland.ui.tags.plugin;

import com.eltiland.model.tags.ITagable;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.model.IModel;
import org.brixcms.workspace.Workspace;

/**
 * Abstract tag management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class AbstractTagPanel extends BaseEltilandPanel<Workspace> {
    /**
     * Panel constructor.
     *
     * @param id              markup id.
     * @param workspaceIModel workspace model.
     * @param clazz           entity class.
     */
    protected AbstractTagPanel(String id, IModel<Workspace> workspaceIModel, Class<? extends ITagable> clazz) {
        super(id, workspaceIModel);
        add(new GeneralTagPanel("panel", clazz));
    }
}
