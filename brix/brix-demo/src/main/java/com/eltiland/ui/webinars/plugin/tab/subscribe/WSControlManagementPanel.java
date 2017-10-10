package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.bl.WebinarSubscriptionManager;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Панель управления абоментами.
 *
 * @author Aleksey Plotnikov
 */
public class WSControlManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private WebinarSubscriptionManager webinarSubscriptionManager;

    private ELTTable<WebinarSubscription> grid;

    private Dialog<WSPropertyPanel> propertyPanelDialog = new Dialog<WSPropertyPanel>("subPropertyDialog", 350) {
        @Override
        public WSPropertyPanel createDialogPanel(String id) {
            return new WSPropertyPanel(id);
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WSControlManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<WebinarSubscription>("grid", 10) {
            @Override
            protected List<IColumn<WebinarSubscription>> getColumns() {
                List<IColumn<WebinarSubscription>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<WebinarSubscription>(new ResourceModel("nameLabel"), "name", "name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarSubscriptionManager.getList(
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return webinarSubscriptionManager.getCount();
            }

            @Override
            protected void onClick(IModel<WebinarSubscription> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case NEW:
                    {
                        propertyPanelDialog.getDialogPanel().initCreateMode();
                        propertyPanelDialog.show(target);
                        break;
                    }
                    default:
                        break;
                }
            }

            @Override
            protected boolean isControlActionVisible(GridAction action) {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.NEW));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch(action) {
                    case NEW:
                        return getString("createAction");
                    default:
                        return StringUtils.EMPTY_STRING;
                }
            }

            @Override
            protected boolean isControlling() {
                return true;
            }
        };

        add(grid.setOutputMarkupId(true));
        add(propertyPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
