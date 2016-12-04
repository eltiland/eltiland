package com.eltiland.ui.course.control.webinar;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.utils.StringUtils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for controlling webinars of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseWebinarPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;

    private ELTTable<ELTWebinarCourseItem> grid;

    private Dialog<WebinarItemPanel> webinarItemPanelDialog =
            new Dialog<WebinarItemPanel>("webinar_property_dialog", 400) {
                @Override
                public WebinarItemPanel createDialogPanel(String id) {
                    return new WebinarItemPanel(id);
                }
            };

    public CourseWebinarPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        grid = new ELTTable<ELTWebinarCourseItem>("grid", 20) {
            @Override
            protected List<IColumn<ELTWebinarCourseItem>> getColumns() {
                List<IColumn<ELTWebinarCourseItem>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<ELTWebinarCourseItem>(
                        new ResourceModel("name.column"), "name", "name"));
                columns.add(new AbstractColumn<ELTWebinarCourseItem>(new ResourceModel("status.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTWebinarCourseItem>> item,
                                             String s, IModel<ELTWebinarCourseItem> iModel) {
                        boolean isWebinar = !(iModel.getObject().getWebinar() == null);
                        Label label = new Label(s, new ResourceModel(isWebinar ? "yes" : "no"));
                        label.add(new AttributeModifier("class",
                                new Model<>(isWebinar ? "disactive_item " : "active_item")));
                        item.add(label);
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getWebinars(CourseWebinarPanel.this.getModelObject(),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getWebinarsCount(CourseWebinarPanel.this.getModelObject());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTWebinarCourseItem> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.NEW));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTWebinarCourseItem> rowModel) {
                if (action.equals(GridAction.NEW)) {
                    boolean hasWebinar = rowModel.getObject().getWebinar() != null;
                    return !hasWebinar;
                } else {
                    return false;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.NEW)) {
                    return getString("new.action");
                } else {
                    return StringUtils.EMPTY_STRING;
                }
            }

            @Override
            protected void onClick(IModel<ELTWebinarCourseItem> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.NEW)) {
                    webinarItemPanelDialog.show(target);
                }
            }

            @Override
            protected String getNotFoundedMessage() {
                return CourseWebinarPanel.this.getString("no.webinars");
            }
        };

        add(grid.setOutputMarkupPlaceholderTag(true));
        add(webinarItemPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
