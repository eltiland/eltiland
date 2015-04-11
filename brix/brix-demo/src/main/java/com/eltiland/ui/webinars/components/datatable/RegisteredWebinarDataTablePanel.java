package com.eltiland.ui.webinars.components.datatable;

import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.webinars.components.WebinarActionPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Iterator;

/**
 * Data table for webinars, to which user was registered.
 *
 * @author Aleksey Plotnikov
 */
public abstract class RegisteredWebinarDataTablePanel extends WebinarDataTablePanel {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    /**
     * Table constructor.
     *
     * @param id      panel's id.
     * @param maxRows rows limit
     */
    public RegisteredWebinarDataTablePanel(String id, int maxRows) {
        super(id, new EltiDataProviderBase<Webinar>() {
            @SpringBean
            private WebinarManager webinarManager;

            @Override
            public Iterator iterator(int first, int count) {
                return webinarManager.getUserWebinarList(
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            public int size() {
                return webinarManager.getUserWebinarCount();
            }
        }, maxRows);
    }

    @Override
    protected AbstractColumn<Webinar> getActionColumn() {
        return new AbstractColumn<Webinar>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<Webinar>> cellItem,
                                     String componentId, final IModel<Webinar> rowModel) {
                cellItem.add(new WebinarActionPanel(componentId, WebinarActionPanel.ACTION.UNREG) {
                    @Override
                    public void onAction(AjaxRequestTarget target) {
                        webinarUserPaymentManager.removeUserFromWebinar(rowModel.getObject(),
                                EltilandSession.get().getCurrentUser());
                        onChange(target);
                    }

                    @Override
                    public void onActionMany(AjaxRequestTarget target) {
                        onAddManyUsers(rowModel, target);
                    }
                });
            }
        };
    }

    @Override
    protected AbstractColumn<Webinar> getStatusColumn() {
        return new AbstractColumn<Webinar>(new ResourceModel("statusColumnHeader")) {
            @Override
            public void populateItem(Item<ICellPopulator<Webinar>> cellItem,
                                     String componentId, IModel<Webinar> rowModel) {
                boolean status = webinarUserPaymentManager.getWebinarStatusForUser(
                        rowModel.getObject(), EltilandSession.get().getCurrentUser());
                cellItem.add(new Label(componentId, getString(status ? "CONFIRMED" : "PAYS")));
            }
        };
    }

    public abstract void onChange(AjaxRequestTarget target);
}
