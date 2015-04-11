package com.eltiland.ui.webinars.plugin.tab.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.webinars.plugin.components.column.WebinarManagerColumn;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Webinar general grid panel for announcement and invoicement tabs.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarDataGridPanel extends BaseEltilandPanel {

    @SpringBean
    private GenericManager genericManager;

    private final EltiDefaultDataGrid<WebinarDataSource, Webinar> grid;

    public WebinarDataGridPanel(String id) {
        super(id);

        List<IGridColumn<WebinarDataSource, Webinar>> columns = new ArrayList<>();
        columns.add(new PropertyWrapColumn(new ResourceModel("topicTabLabel"), "name", "name"));
        columns.add(new PropertyWrapColumn(new ResourceModel("descriptionTabLabel"), "description", "description", 400));
        columns.add(new PropertyWrapColumn(new ResourceModel("startDateTabLabel"), "startDate", "startDate"));
        columns.add(new WebinarManagerColumn<WebinarDataSource>("managerPanel", new ResourceModel("managerTabLabel")));
        columns.add(new PropertyWrapColumn(new ResourceModel("deadlineDateTabLabel"),
                "registrationDeadline", "registrationDeadline", 260));
        if (getActionColumn() != null) {
            columns.add(getActionColumn());
        }
        columns.add(new AbstractColumn<WebinarDataSource, Webinar>("courseColumn", new ResourceModel("courseLabel")) {
            @Override
            public Component newCell(WebMarkupContainer components, String s, IModel<Webinar> webinarIModel) {
                Webinar webinar = webinarIModel.getObject();
                genericManager.initialize(webinar, webinar.getCourseItem());

                if (webinar.getCourseItem() == null) {
                    return new EmptyPanel(s);
                } else {
                    genericManager.initialize(webinar.getCourseItem(), webinar.getCourseItem().getCourse());
                    return new Label(s, webinar.getCourseItem().getCourse().getName());
                }
            }
        });

        grid = new EltiDefaultDataGrid<>("grid", new Model<WebinarDataSource>(new WebinarDataSource() {
            @Override
            protected boolean isApproved() {
                return WebinarDataGridPanel.this.isApproved();
            }
        }), columns);
        add(grid.setOutputMarkupId(true));
    }

    public void updateGrid() {
        grid.markAllItemsDirty();
        grid.update();
    }

    protected boolean isApproved() {
        return true;
    }

    protected AbstractColumn<WebinarDataSource, Webinar> getActionColumn() {
        return null;
    }
}
