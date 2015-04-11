package com.eltiland.ui.tags.plugin;

import com.eltiland.model.Video;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.tags.ITagable;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tag management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class TagManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TagManagementPanel.class);

    protected TagManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<Class<? extends ITagable>> classList =
                new ArrayList<Class<? extends ITagable>>(Arrays.asList(Video.class, LibraryRecord.class));

        List<IBrixTab> tabs = new ArrayList<>();
        for (final Class<? extends ITagable> clazz : classList) {
            try {
                ITagable object = clazz.newInstance();
                tabs.add(new AbstractWorkspaceTab(new Model<>(object.getTabName()), workspaceIModel) {
                    @Override
                    public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
                        return new AbstractTagPanel(panelId, workspaceModel, clazz);
                    }
                });
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Cannot receive tag entities", e);
                throw new WicketRuntimeException("Cannot receive tag entities", e);
            }
        }
        add(new BrixTabbedPanel("tabPanel", tabs) {
            @Override
            protected String getTabContainerCssClass() {
                return "brix-plugins-tabbed-panel-row";
            }
        });
    }
}
