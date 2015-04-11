package com.eltiland.ui.course.control.data.panel.block;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.bl.course.ELTCourseBlockAccessManager;
import com.eltiland.bl.course.ELTCourseBlockManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.interval.ELTIntervalDialog;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Block access dialog.
 *
 * @author Aleksey Plotnikov.
 */
public class BlockAccessPanel extends ELTDialogPanel {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private ELTCourseBlockAccessManager blockAccessManager;
    @SpringBean
    private ELTCourseBlockManager blockManager;

    private IModel<ELTCourseBlock> blockIModel = new GenericDBModel<>(ELTCourseBlock.class);
    private ELTTable<ELTCourseBlockAccess> grid;

    private ELTSelectDialog<User> selector = new ELTSelectDialog<User>("selector", 880) {
        @Override
        protected int getMaxRows() {
            return 20;
        }

        @Override
        protected String getHeader() {
            return BlockAccessPanel.this.getString("user.header");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            close(target);
            for (Long id : selectedIds) {
                User user = genericManager.getObject(User.class, id);
                ELTCourseBlockAccess blockAccess = new ELTCourseBlockAccess();
                blockAccess.setBlock(blockIModel.getObject());
                blockAccess.setListener(user);
                try {
                    blockAccessManager.create(blockAccess);
                    blockIModel.getObject().getBlockAccessSet().add(blockAccess);
                    blockManager.update(blockIModel.getObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
            target.add(grid);
        }

        @Override
        protected List<IColumn<User>> getColumns() {
            List<IColumn<User>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<User>(new ResourceModel("name.column"), "name", "name"));
            columns.add(new PropertyColumn<User>(new ResourceModel("email.column"), "email", "email"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return userManager.getUserSearchList(first, count,
                    getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return userManager.getUserSearchCount(getSearchString());
        }

        @Override
        protected String getSearchPlaceholder() {
            return BlockAccessPanel.this.getString("user.search");
        }
    };

    private Dialog<ELTIntervalDialog<ELTCourseBlockAccess>> intervalDialog =
            new Dialog<ELTIntervalDialog<ELTCourseBlockAccess>>("interval.selector", 435) {
                @Override
                public ELTIntervalDialog<ELTCourseBlockAccess> createDialogPanel(String id) {
                    return new ELTIntervalDialog<ELTCourseBlockAccess>(id) {
                        @Override
                        protected String getHeader() {
                            return BlockAccessPanel.this.getString("interval.header");
                        }
                    };
                }

                @Override
                public void registerCallback(ELTIntervalDialog<ELTCourseBlockAccess> panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseBlockAccess>() {
                        @Override
                        public void process(IModel<ELTCourseBlockAccess> model, AjaxRequestTarget target) {
                            close(target);
                            try {
                                blockAccessManager.update(model.getObject());
                                target.add(grid);
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    });
                }
            };

    /**
     * Panel ctor.
     *
     * @param id markup id.
     */
    public BlockAccessPanel(String id) {
        super(id);
        grid = new ELTTable<ELTCourseBlockAccess>("grid", 20) {
            @Override
            protected List<IColumn<ELTCourseBlockAccess>> getColumns() {
                List<IColumn<ELTCourseBlockAccess>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<ELTCourseBlockAccess>(
                        new ResourceModel("block.name.column"), "listener.name", "listener.name"));
                columns.add(new AbstractColumn<ELTCourseBlockAccess>(new ResourceModel("block.status.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseBlockAccess>> cellItem,
                                             String componentId, IModel<ELTCourseBlockAccess> rowModel) {
                        boolean opened = rowModel.getObject().isOpen();
                        Label status = new Label(componentId, getString(opened ? "access.opened" : "access.closed"));
                        status.add(new AttributeModifier("class", opened ? "active_item" : "disactive_item"));
                        cellItem.add(status);
                    }
                });
                columns.add(new AbstractColumn<ELTCourseBlockAccess>(new ResourceModel("block.interval.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseBlockAccess>> cellItem,
                                             String componentId, IModel<ELTCourseBlockAccess> rowModel) {
                        if (rowModel.getObject().getStartDate() == null) {
                            cellItem.add(new EmptyPanel(componentId));
                        } else {
                            cellItem.add(new Label(componentId,
                                    DateUtils.formatDate(rowModel.getObject().getStartDate()) + " - " +
                                            DateUtils.formatDate(rowModel.getObject().getEndDate())));
                        }
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return blockAccessManager.getAccessInformation(blockIModel.getObject()).iterator();
            }

            @Override
            protected int getSize() {
                genericManager.initialize(blockIModel.getObject(), blockIModel.getObject().getBlockAccessSet());
                return blockIModel.getObject().getBlockAccessSet().size();
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.ADD));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourseBlockAccess> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.LOCK, GridAction.UNLOCK, GridAction.TIME));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ADD:
                        return getString("add.tooltip");
                    case LOCK:
                        return getString("lock.tooltip");
                    case UNLOCK:
                        return getString("unlock.tooltip");
                    case TIME:
                        return getString("time.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTCourseBlockAccess> rowModel) {
                switch (action) {
                    case LOCK:
                        return rowModel.getObject().isOpen();
                    case UNLOCK:
                        return !rowModel.getObject().isOpen();
                    case TIME:
                        return rowModel.getObject().isOpen();
                    default:
                        return true;
                }
            }

            @Override
            protected void onClick(IModel<ELTCourseBlockAccess> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case ADD:
                        selector.show(target);
                        break;
                    case LOCK:
                        rowModel.getObject().setOpen(false);
                        rowModel.getObject().setStartDate(null);
                        rowModel.getObject().setEndDate(null);
                        try {
                            blockAccessManager.update(rowModel.getObject());
                            target.add(grid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case UNLOCK:
                        rowModel.getObject().setOpen(true);
                        try {
                            blockAccessManager.update(rowModel.getObject());
                            target.add(grid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case TIME:
                        intervalDialog.getDialogPanel().initPanel(rowModel);
                        intervalDialog.show(target);
                        break;

                }
            }

            @Override
            protected String getNotFoundedMessage() {
                return BlockAccessPanel.this.getString("no.users");
            }
        };
        form.add(grid.setOutputMarkupId(true));
        form.add(selector);
        form.add(intervalDialog);
        form.setMultiPart(true);
    }

    public void initData(IModel<ELTCourseBlock> blockModel) {
        blockIModel.setObject(blockModel.getObject());
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public String getVariation() {
        return "styled";
    }
}
