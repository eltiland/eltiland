package com.eltiland.ui.course.control.listeners;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.course.control.listeners.panel.GeneralDataPanel;
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

    private ELTTable<ELTCourseListener> grid;

    public CourseListenersPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);
        grid = new ELTTable<ELTCourseListener>("grid", 20) {
            @Override
            protected List<IColumn<ELTCourseListener>> getColumns() {
                List<IColumn<ELTCourseListener>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<ELTCourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "name") {
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
            protected List<GridAction> getGridActions(IModel<ELTCourseListener> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.REMOVE));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.REMOVE)) {
                    return getString("remove.tooltip");
                } else {
                    return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected void onClick(IModel<ELTCourseListener> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.REMOVE)) {
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
