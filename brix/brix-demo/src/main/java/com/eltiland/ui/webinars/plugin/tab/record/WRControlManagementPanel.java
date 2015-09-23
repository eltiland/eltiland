package com.eltiland.ui.webinars.plugin.tab.record;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.*;

/**
 * Webinars records control management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WRControlManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private GenericManager genericManager;

    private final ELTTable<Webinar> grid;

    private Dialog<WRPropertyPanel> recordControlPanelDialog = new Dialog<WRPropertyPanel>("addRecordDialog", 350) {
        @Override
        public WRPropertyPanel createDialogPanel(String id) {
            return new WRPropertyPanel(id);
        }

        @Override
        public void registerCallback(WRPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<WebinarRecord>() {
                @Override
                public void process(IModel<WebinarRecord> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<WebinarRecord>() {
                @Override
                public void process(IModel<WebinarRecord> model, AjaxRequestTarget target) {
                    close(target);
                    target.add(grid);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WRControlManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new ELTTable<Webinar>("grid", 10) {
            @Override
            protected boolean hasConfirmation(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ADD:
                        return getString("addAction");
                    case EDIT:
                        return getString("editAction");
                    case REMOVE:
                        return getString("cancelAction");
                    case ON:
                        return getString("onAction");
                    case OFF:
                        return getString("offAction");
                    default:
                        return "";
                }
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<Webinar> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                boolean hasRecord = rowModel.getObject().getRecord() != null;
                boolean recordOpen = hasRecord && rowModel.getObject().getRecord().isOpen();
                switch (action) {
                    case ADD:
                        return !hasRecord;
                    case EDIT:
                        return hasRecord;
                    case REMOVE:
                        // can be deleted only, if there are no payments
                        WebinarRecord webinarRecord = rowModel.getObject().getRecord();
                        if (webinarRecord == null) {
                            return false;
                        }
                        genericManager.initialize(webinarRecord, webinarRecord.getPayments());
                        return webinarRecord.getPayments().size() == 0;
                    case ON:
                        return !recordOpen;
                    case OFF:
                        return recordOpen;
                    default:
                        return true;
                }
            }

            @Override
            protected List<GridAction> getGridActions(IModel<Webinar> rowModel) {
                return Arrays.asList(GridAction.ADD, GridAction.EDIT, GridAction.REMOVE, GridAction.ON, GridAction.OFF);
            }

            @Override
            protected List<IColumn<Webinar>> getColumns() {
                List<IColumn<Webinar>> columns = new ArrayList<>();

                columns.add(new PropertyColumn<Webinar>(new ResourceModel("topicTabLabel"), "name", "name"));
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("descriptionTabLabel"), "description",
                        "description"));
                columns.add(new PropertyColumn<Webinar>(new ResourceModel("startDateTabLabel"), "startDate",
                        "startDate"));

                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return webinarManager.getWebinarList(first, count, getSort().getProperty(), getSort().isAscending(),
                        false, true, getSearchString()).iterator();
            }

            @Override
            protected int getSize() {
                return webinarManager.getWebinarCount(false, true, getSearchString());
            }

            @Override
            protected void onClick(IModel<Webinar> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        recordControlPanelDialog.getDialogPanel().initCreateMode(rowModel.getObject());
                        recordControlPanelDialog.show(target);

                        break;

                    case EDIT:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                        recordControlPanelDialog.getDialogPanel().initEditMode(rowModel.getObject().getRecord());
                        recordControlPanelDialog.show(target);

                        break;

                    case REMOVE:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                        try {
                            genericManager.delete(rowModel.getObject().getRecord());
                        } catch (EltilandManagerException e) {
                            ELTAlerts.renderErrorPopup(getString("cancelActionError"), target);
                        }
                        target.add(grid);

                        break;

                    case ON:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                        try {
                            rowModel.getObject().getRecord().setOpen(true);
                            genericManager.update(rowModel.getObject().getRecord());
                        } catch (ConstraintException e) {
                            ELTAlerts.renderErrorPopup(getString("updateActionError"), target);
                        }
                        target.add(grid);
                        break;
                    case OFF:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getRecord());
                        try {
                            rowModel.getObject().getRecord().setOpen(false);
                            genericManager.update(rowModel.getObject().getRecord());
                        } catch (ConstraintException e) {
                            ELTAlerts.renderErrorPopup(getString("updateActionError"), target);
                        }
                        target.add(grid);
                        break;
                }
            }
        };
        add(grid.setOutputMarkupId(true));
        add(recordControlPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
