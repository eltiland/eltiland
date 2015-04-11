package com.eltiland.ui.course.plugin.tab;

import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.GeneralPriceColumn;
import com.eltiland.ui.common.column.PropertyWrapColumn;
import com.eltiland.ui.common.components.datagrid.EltiDefaultDataGrid;
import com.eltiland.ui.common.components.export.ExportButton;
import com.eltiland.ui.common.model.GenericDBModel;
import com.inmethod.grid.IDataSource;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.IGridSortState;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Courses paid invoice management panel.
 *
 * @author Aleksey Plotnikov
 */
public class CoursePaymentManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private final EltiDefaultDataGrid<CoursePaymentDataSource, CoursePayment> grid;

    public CoursePaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        List<IGridColumn<CoursePaymentDataSource, CoursePayment>> columns = new ArrayList<>();
        columns.add(new PropertyWrapColumn(new ResourceModel("dateLabel"), "date", "date"));
        columns.add(new PropertyWrapColumn(new ResourceModel("userLabel"), "listener.name", "listener.name") {
            @Override
            protected void initialize(Object object) {
                genericManager.initialize(object, ((CoursePayment) object).getListener());
            }

            @Override
            public int getInitialSize() {
                return 235;
            }
        });
        columns.add(new PropertyWrapColumn(
                new ResourceModel("courseLabel"), "invoice.course.name", "invoice.course.name") {
            @Override
            protected void initialize(Object object) {
                genericManager.initialize(object, ((CoursePayment) object).getInvoice());
                genericManager.initialize(((CoursePayment) object).getInvoice(),
                        ((CoursePayment) object).getInvoice().getCourse());
            }

            @Override
            public int getInitialSize() {
                return 235;
            }
        });
        columns.add(new GeneralPriceColumn<CoursePaymentDataSource, CoursePayment>(
                "priceColumn", new ResourceModel("priceLabel"), "price"));

        grid = new EltiDefaultDataGrid<>("grid", new CoursePaymentDataSource(), columns);
        add(grid);

        add(new ExportButton("downloadPanel", CoursePayment.class));
    }

    private class CoursePaymentDataSource implements IDataSource<CoursePayment> {
        @Override
        public void detach() {
        }

        @Override
        public void query(IQuery query, IQueryResult<CoursePayment> result) {
            int count = coursePaymentManager.getPaidPaymentCount();
            result.setTotalCount(count);

            if (count < 1) {
                result.setItems(Collections.<CoursePayment>emptyIterator());
            }

            String sortProperty = "date";
            boolean isAscending = false;

            if (!query.getSortState().getColumns().isEmpty()) {
                IGridSortState.ISortStateColumn sortingColumn = query.getSortState().getColumns().get(0);
                sortProperty = sortingColumn.getPropertyName();
                isAscending = sortingColumn.getDirection() == IGridSortState.Direction.ASC;
            }

            result.setItems(coursePaymentManager.getListOfPaidPayments(
                    query.getFrom(), query.getCount(), sortProperty, isAscending).iterator());
        }

        @Override
        public IModel<CoursePayment> model(CoursePayment object) {
            return new GenericDBModel<>(CoursePayment.class, object);
        }
    }
}
