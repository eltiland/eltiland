package com.eltiland.ui.webinars.plugin.tab.subscribe;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionPaymentManager;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.tab.subscribe.components.WebinarListPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Панель платежей за абонементы.
 *
 * @author Aleksey Plotnikov
 */
public class WSPaymentManagementPanel extends BaseEltilandPanel<Workspace> {

    @SpringBean
    private WebinarSubscriptionPaymentManager webinarSubscriptionPaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private ELTTable<WebinarSubscriptionPayment> grid = new ELTTable<WebinarSubscriptionPayment>("grid", 20) {
        @Override
        protected List<IColumn<WebinarSubscriptionPayment>> getColumns() {
            ArrayList<IColumn<WebinarSubscriptionPayment>> columns = new ArrayList<>();

            columns.add(new PropertyColumn<WebinarSubscriptionPayment>(
                    new ResourceModel("nameLabel"), "subscription.name", "subscription.name"));
            columns.add(new AbstractColumn<WebinarSubscriptionPayment>(new ResourceModel("webinarLabel")) {
                @Override
                public void populateItem(Item<ICellPopulator<WebinarSubscriptionPayment>> item,
                                         String s, IModel<WebinarSubscriptionPayment> iModel) {
                    genericManager.initialize(iModel.getObject(), iModel.getObject().getSubscription());
                    item.add(new WebinarListPanel(s,
                            new GenericDBModel<>(WebinarSubscription.class, iModel.getObject().getSubscription())));
                }
            });
            columns.add(new AbstractColumn<WebinarSubscriptionPayment>(new ResourceModel("memberLabel")) {
                @Override
                public void populateItem(Item<ICellPopulator<WebinarSubscriptionPayment>> item,
                                         String s, IModel<WebinarSubscriptionPayment> iModel) {
                    WebinarSubscriptionPayment payment = iModel.getObject();
                    String name = String.format("%s %s", payment.getUserSurname(), payment.getUserName());
                    String data = String.format("%s\n%s", name, payment.getUserEmail());
                    item.add(new MultiLineLabel(s, new Model<>(data)));
                }
            });
            columns.add(new PropertyColumn<WebinarSubscriptionPayment>(new ResourceModel("dateLabel"), "date",
                    "date"));
            columns.add(new PriceColumn(new ResourceModel("sumLabel"), "price", "price"));

            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return webinarSubscriptionPaymentManager.getList(
                    first, count, getSort().getProperty(), getSort().isAscending()).iterator();
        }

        @Override
        protected int getSize() {
            return webinarSubscriptionPaymentManager.getCount();
        }

        @Override
        protected void onClick(IModel<WebinarSubscriptionPayment> rowModel, GridAction action, AjaxRequestTarget target) {
        }
    };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WSPaymentManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);
        add(grid);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
