package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.column.PriceColumn;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Панель модерации участников вебинара.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarModeratePanel extends ELTDialogPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarModeratePanel.class);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private IModel<String> confirmedCountModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            if (webinarIModel.getObject() != null) {
                return String.format(getString("webinar.confirmed"),
                        webinarUserPaymentManager.getWebinarConfirmedUserCount(webinarIModel.getObject()));
            } else {
                return "";
            }
        }
    };

    private Label nameLabel = new Label("name", new Model<String>());

    private Label confirmedCountLabel = new Label("confirmed", confirmedCountModel);

    private ELTTable<WebinarUserPayment> grid;

    /**
     * Конструктор панели.
     *
     * @param id markup id
     */
    public WebinarModeratePanel(String id) {
        super(id);
        form.add(nameLabel);
        form.add(new Label("capacity",
                String.format(getString("webinar.capacity"), eltilandProps.getProperty("webinar.usercount"))));
        form.add(confirmedCountLabel.setOutputMarkupId(true));

        grid = new ELTTable<WebinarUserPayment>("grid", 20) {
            @Override
            protected List<IColumn<WebinarUserPayment>> getColumns() {
                List<IColumn<WebinarUserPayment>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<WebinarUserPayment>(
                        new ResourceModel("email.title"), "userEmail", "userEmail"));
                columns.add(new PriceColumn(new ResourceModel("price.title"), "price", "price"));
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("status.title")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> cellItem,
                                             String componentId, IModel<WebinarUserPayment> rowModel) {
                        boolean status = rowModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
                        cellItem.add(new Label(componentId, new ResourceModel(status ? "CONFIRMED" : "PAYS")));
                    }
                });
                columns.add(new AbstractColumn<WebinarUserPayment>(new ResourceModel("role.title")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<WebinarUserPayment>> cellItem,
                                             String componentId, IModel<WebinarUserPayment> rowModel) {
                        cellItem.add(new Label(componentId,
                                new ResourceModel(rowModel.getObject().getRole().toString())));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                try {
                    return webinarUserPaymentManager.getWebinarUserList(webinarIModel.getObject(), first, count,
                            getSort().getProperty(), getSort().isAscending(), getSearchString()).iterator();
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot get list of users", e);
                    throw new WicketRuntimeException("Cannot get list of users", e);
                }
            };

            @Override
            protected int getSize() {
                try {
                    return webinarUserPaymentManager.getWebinarUserCount(
                            webinarIModel.getObject(), getSearchString());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot get list of users", e);
                    throw new WicketRuntimeException("Cannot get list of users", e);
                }
            }

            @Override
            protected boolean isSearching() {
                return true;
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return Arrays.asList(GridAction.ADD, GridAction.CHECK);
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch(action) {
                    case ADD:
                        return getString("add.tooltip");
                    case CHECK:
                        return getString("check.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected void onClick(IModel<WebinarUserPayment> rowModel, GridAction action, AjaxRequestTarget target) {

            }
        };
        form.add(grid.setOutputMarkupId(true));
    }

    public void initWebinarData(IModel<Webinar> model) {
        this.webinarIModel.setObject(model.getObject());
        nameLabel.setDefaultModelObject(model.getObject().getName());
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
