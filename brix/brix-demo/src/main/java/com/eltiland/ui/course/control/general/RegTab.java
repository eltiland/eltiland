package com.eltiland.ui.course.control.general;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseUserDataManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseUserData;
import com.eltiland.model.course2.listeners.UserDataStatus;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

/**
 * Panel for editing data set, required for registration.
 *
 * @author Aleksey Plotnikov.
 */
class RegTab extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseUserDataManager courseUserDataManager;

    private static final String NOT_PRESENT_STYLE = "font-weight:bold;";
    private static final String PRESENT_STYLE_REQ = "font-weight:bold;color:red;";
    private static final String PRESENT_STYLE_NOREQ = "font-weight:bold;color:green";

    private ELTTable<ELTCourseUserData> grid;

    private Dialog<EditCaptionPanel> editCaptionPanelDialog = new Dialog<EditCaptionPanel>("captionDialog", 330) {
        @Override
        public EditCaptionPanel createDialogPanel(String id) {
            return new EditCaptionPanel(id);
        }

        @Override
        public void registerCallback(EditCaptionPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseUserData>() {
                @Override
                public void process(IModel<ELTCourseUserData> model, AjaxRequestTarget target) {
                    try {
                        courseUserDataManager.update(model.getObject());
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    close(target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public RegTab(String id, IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);

        grid = new ELTTable<ELTCourseUserData>("grid", 5) {
            @Override
            protected List<IColumn<ELTCourseUserData>> getColumns() {
                List<IColumn<ELTCourseUserData>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<ELTCourseUserData>(new ResourceModel("type.label"), "type") {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseUserData>> cellItem,
                                             String componentId, IModel<ELTCourseUserData> rowModel) {
                        cellItem.add(new Label(componentId,
                                getString(rowModel.getObject().getType().toString() + ".type")));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseUserData>(new ResourceModel("status.label")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseUserData>> cellItem,
                                             String componentId, IModel<ELTCourseUserData> rowModel) {
                        UserDataStatus status = rowModel.getObject().getStatus();
                        Label label = new Label(componentId, getString(status.toString() + ".status"));
                        label.add(new AttributeModifier("style",
                                new Model<>(status.equals(UserDataStatus.NO) ? (NOT_PRESENT_STYLE) :
                                        (status.equals(UserDataStatus.ACTIVE) ?
                                                (PRESENT_STYLE_NOREQ) : (PRESENT_STYLE_REQ)))));

                        cellItem.add(label);
                    }
                });
                columns.add(new PropertyColumn<ELTCourseUserData>(
                        new ResourceModel("caption.label"), "caption", "caption"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                genericManager.initialize(getModelObject(), getModelObject().getUserDataSet());
                Set<ELTCourseUserData> dataSet = getModelObject().getUserDataSet();
                return dataSet.iterator();
            }

            @Override
            protected int getSize() {
                return 5;
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourseUserData> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.CONTROL_SET, GridAction.CONTROL_RESET,
                        GridAction.REMOVE, GridAction.ADD, GridAction.EDIT));
            }

            @Override
            protected void onClick(IModel<ELTCourseUserData> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case CONTROL_SET:
                        rowModel.getObject().setStatus(UserDataStatus.REQUIRED);
                        break;
                    case CONTROL_RESET:
                        rowModel.getObject().setStatus(UserDataStatus.ACTIVE);
                        break;
                    case REMOVE:
                        rowModel.getObject().setStatus(UserDataStatus.NO);
                        break;
                    case ADD:
                        rowModel.getObject().setStatus(UserDataStatus.ACTIVE);
                        break;
                    case EDIT:
                        editCaptionPanelDialog.getDialogPanel().initData(rowModel);
                        editCaptionPanelDialog.show(target);
                        break;
                }

                try {
                    courseUserDataManager.update(rowModel.getObject());
                    target.add(grid);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTCourseUserData> rowModel) {
                UserDataStatus status = rowModel.getObject().getStatus();
                switch (action) {
                    case CONTROL_SET:
                        return status.equals(UserDataStatus.ACTIVE);
                    case CONTROL_RESET:
                        return status.equals(UserDataStatus.REQUIRED);
                    case REMOVE:
                        return !status.equals(UserDataStatus.NO);
                    case ADD:
                        return status.equals(UserDataStatus.NO);
                    default:
                        return true;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case CONTROL_SET:
                        return getString("action.req");
                    case CONTROL_RESET:
                        return getString("action.noreq");
                    case REMOVE:
                        return getString("action.remove");
                    case EDIT:
                        return getString("action.edit");
                    case ADD:
                        return getString("action.add");
                    default:
                        return StringUtils.EMPTY;
                }
            }
        };

        add(grid.setOutputMarkupId(true));
        add(editCaptionPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class EditCaptionPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTCourseUserData> {

        private IDialogActionProcessor<ELTCourseUserData> callback;
        private ELTTextField<String> captionField = new ELTTextField<>(
                "caption", new ResourceModel("caption"), new Model<String>(), String.class, true);

        private IModel<ELTCourseUserData> courseUserDataIModel = new GenericDBModel<>(ELTCourseUserData.class);

        public EditCaptionPanel(String id) {
            super(id);
            form.add(captionField);
        }

        public void initData(IModel<ELTCourseUserData> dataIModel) {
            courseUserDataIModel = dataIModel;
            captionField.setModelObject(dataIModel.getObject().getCaption());
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                courseUserDataIModel.getObject().setCaption(captionField.getModelObject());
                callback.process(courseUserDataIModel, target);
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<ELTCourseUserData> callback) {
            this.callback = callback;
        }
    }
}
