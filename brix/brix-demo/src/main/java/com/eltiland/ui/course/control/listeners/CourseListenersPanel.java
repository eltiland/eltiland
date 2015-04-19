package com.eltiland.ui.course.control.listeners;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.control.listeners.panel.GeneralDataPanel;
import com.eltiland.ui.course.control.listeners.panel.ListenerMailPanel;
import com.eltiland.ui.course.control.listeners.panel.NamePanel;
import com.eltiland.ui.course.control.listeners.panel.OrganizationPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel with information about listeners of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseListenersPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private ELTTable<ELTCourseListener> grid;

    private IModel<ELTCourseListener> currentListenerModel =
            new GenericDBModel<ELTCourseListener>(ELTCourseListener.class);

    private Dialog<ListenerMailPanel> sendDialog = new Dialog<ListenerMailPanel>("sendDialog", 500) {
        @Override
        public ListenerMailPanel createDialogPanel(String id) {
            return new ListenerMailPanel(id);
        }

        @Override
        public void registerCallback(ListenerMailPanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    ELTCourseListener listener = currentListenerModel.getObject();

                    try {
                        if (listener == null) {
                            emailMessageManager.sendCourseListenerMessage(getModelObject(), model.getObject(), true);
                        } else {
                            emailMessageManager.sendCourseListenerMessage(listener, model.getObject());
                        }
                        close(target);
                        ELTAlerts.renderOKPopup(getString("message.send"), target);
                    } catch (EmailException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    public CourseListenersPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);
        add(sendDialog);
        grid = new ELTTable<ELTCourseListener>("grid", 20) {
            @Override
            protected List<IColumn<ELTCourseListener>> getColumns() {
                List<IColumn<ELTCourseListener>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<ELTCourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "listener.name") {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> components,
                                             String s, IModel<ELTCourseListener> listenerIModel) {
                        genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getListener());
                        components.add(new NamePanel(s, new Model<>(listenerIModel.getObject().getListener())));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("contactColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new GeneralDataPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("organizationColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new OrganizationPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("statusColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> components,
                                             String s, IModel<ELTCourseListener> courseListenerIModel) {
                        Label label = new Label(s, getString(
                                courseListenerIModel.getObject().isCompleted() ? "completed" : "not_completed"));
                        if (courseListenerIModel.getObject().isCompleted()) {
                            label.add(new AttributeAppender("style", "color:red;font-weight:bold"));
                        }
                        components.add(label);
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseListenerManager.getList(getModelObject(), getSearchString(), first, count,
                        getSort().getProperty(), getSort().isAscending(), true, false).iterator();
            }

            @Override
            protected int getSize() {
                return courseListenerManager.getCount(getModelObject(), getSearchString(), true, false);
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.SEND));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourseListener> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.USER_SEND, GridAction.REMOVE));
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case REMOVE:
                        return getString("remove.tooltip");
                    case SEND:
                        return getString("send.tooltip");
                    case USER_SEND:
                        return getString("send.user.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean isControlActionVisible(GridAction action) {
                if (action.equals(GridAction.SEND)) {
                    return getSize() > 0;
                } else {
                    return false;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected void onClick(IModel<ELTCourseListener> rowModel, GridAction action, AjaxRequestTarget target) {
                switch (action) {
                    case REMOVE:
                        try {
                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getCourse());
                            if (rowModel.getObject().getCourse() instanceof TrainingCourse) {
                                courseListenerManager.delete(rowModel.getObject());
                            } else {
                                rowModel.getObject().setStatus(PaidStatus.NEW);
                                courseListenerManager.update(rowModel.getObject());
                            }
                            target.add(grid);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case SEND:
                        currentListenerModel.setObject(null);
                        sendDialog.show(target);
                        break;
                    case USER_SEND:
                        currentListenerModel.setObject(rowModel.getObject());
                        sendDialog.show(target);
                        break;
                }
            }
        };
        add(grid.setOutputMarkupId(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
