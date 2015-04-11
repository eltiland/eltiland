package com.eltiland.ui.course.control.users;

import com.eltiland.bl.CourseListenerManager;
import com.eltiland.bl.CourseSessionManager;
import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.course.control.users.panel.GeneralDataPanel;
import com.eltiland.ui.course.control.users.panel.MessagePanel;
import com.eltiland.ui.course.control.listeners.panel.NamePanel;
import com.eltiland.ui.course.control.users.panel.OrganizationPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Listener management panel for training courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseListenerPanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseListenerPanel.class);

    @SpringBean
    private CourseListenerManager courseListenerManager;
    @SpringBean
    private CourseSessionManager courseSessionManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;

    private ELTTable<CourseListener> grid;

    private IModel<CourseSession> sessionIModel = new LoadableDetachableModel<CourseSession>() {
        @Override
        protected CourseSession load() {
            return courseSessionManager.getActiveSession(getModelObject());
        }
    };

    private Dialog<MessagePanel> messagePanelDialog = new Dialog<MessagePanel>("sendDialog", 325) {
        @Override
        public MessagePanel createDialogPanel(String id) {
            return new MessagePanel(id);
        }

        @Override
        public void registerCallback(MessagePanel panel) {
            super.registerCallback(panel);
            panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                @Override
                public void process(IModel<String> model, AjaxRequestTarget target) {
                    try {
                        emailMessageManager.sendCourseListenerMessage(
                                sessionIModel.getObject(), model.getObject(), true);
                    } catch (EmailException e) {
                        LOGGER.error("Cannot send mail", e);
                        throw new WicketRuntimeException("Cannot send mail", e);
                    }
                    close(target);
                    ELTAlerts.renderOKPopup(getString("sendMessage"), target);
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
    public CourseListenerPanel(String id, IModel<Course> courseIModel) {
        super(id, courseIModel);

        grid = new ELTTable<CourseListener>("grid", 15) {
            @Override
            protected List<IColumn<CourseListener>> getColumns() {
                List<IColumn<CourseListener>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<CourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "listener.name") {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> components,
                                             String s, IModel<CourseListener> listenerIModel) {
                        genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getListener());
                        components.add(new NamePanel(s, new Model<>(listenerIModel.getObject().getListener())));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("contactColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new GeneralDataPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("organizationColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> cellItem,
                                             String componentId, IModel<CourseListener> rowModel) {
                        cellItem.add(new OrganizationPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<CourseListener>(new ResourceModel("statusColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<CourseListener>> components,
                                             String s, IModel<CourseListener> courseListenerIModel) {
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
                return courseListenerManager.getListeners(sessionIModel.getObject(), getSearchString(),
                        first, count, getSort().getProperty(), getSort().isAscending(), true).iterator();
            }

            @Override
            protected int getSize() {
                return courseListenerManager.getListenersCount(sessionIModel.getObject(), getSearchString(), true);
            }

            @Override
            protected void onClick(IModel<CourseListener> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.SEND)) {
                    messagePanelDialog.show(target);
                }
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(GridAction.SEND));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.SEND)) {
                    return getString("sendTooltip");
                } else {
                    return super.getActionTooltip(action);
                }
            }

            @Override
            protected boolean isSearching() {
                return true;
            }
        };

        add(grid.setOutputMarkupId(true));
        add(messagePanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
