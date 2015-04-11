package com.eltiland.ui.course.components.listeners;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.datagrid.styled.DataTablePanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Course listeners data table.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseListenersDataTablePanel extends DataTablePanel<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseListenersGridPanel.class);

    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private GenericManager genericManager;

    /**
     * Default constructor. Will show all users.
     *
     * @param id panel's id.
     */
    public CourseListenersDataTablePanel(String id, ISortableDataProvider<User> dataProvider, int maxRows) {
        super(id, dataProvider, maxRows);
    }

    @Override
    protected List<IColumn<User>> getColumns() {
        ArrayList<IColumn<User>> columns = new ArrayList<>();

        columns.add(new AbstractColumn<User>(new ResourceModel("nameColumn"), "name") {
            @Override
            public void populateItem(Item<ICellPopulator<User>> cellItem, String componentId, IModel<User> rowModel) {
                cellItem.add(new LinkPanel(componentId, rowModel));
            }
        });
        columns.add(new PropertyColumn<User>(new ResourceModel("emailColumn"), "email", "email"));

        if (isPaid()) {
            final IModel<CoursePaidInvoice> invoiceModel = new LoadableDetachableModel<CoursePaidInvoice>() {
                @Override
                protected CoursePaidInvoice load() {
                    return coursePaidInvoiceManager.getActualInvoice(getCourseModel().getObject(), null);
                }
            };

            final IModel<CoursePayment> paymentIModel = new GenericDBModel<>(CoursePayment.class);
            final IModel<Float> priceModel = new LoadableDetachableModel<Float>() {
                @Override
                protected Float load() {
                    if( paymentIModel.getObject() == null ) {
                        return (float) 0;
                    }
                    BigDecimal price = paymentIModel.getObject().getPrice();

                    return (price != null) ? price.floatValue() : (float) 0;
                }
            };

            columns.add(new AbstractColumn<User>(new ResourceModel("priceColumn")) {
                @Override
                public void populateItem(Item<ICellPopulator<User>> cellItem,
                                         String componentId, IModel<User> rowModel) {
                    try {
                        paymentIModel.setObject(coursePaymentManager.getPayment(
                                rowModel.getObject(), invoiceModel.getObject(), false));

                        priceModel.detach();
                        String value = (priceModel.getObject() == 0) ? getString("freeValue") :
                                String.format(getString("priceValue"), String.valueOf(priceModel.getObject()));

                        cellItem.add(new Label(componentId, value));
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot get payment info", e);
                        throw new WicketRuntimeException("Cannot get payment info", e);
                    }

                }
            });
            columns.add(new AbstractColumn<User>(new ResourceModel("payDateColumn")) {
                @Override
                public void populateItem(Item<ICellPopulator<User>> cellItem,
                                         String componentId, IModel<User> rowModel) {
                    try {
                        paymentIModel.setObject(coursePaymentManager.getPayment(
                                rowModel.getObject(), invoiceModel.getObject(), false));
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot get payment info", e);
                        throw new WicketRuntimeException("Cannot get payment info", e);
                    }
                    Date payDate = (paymentIModel.getObject() != null) ? paymentIModel.getObject().getDate() : null;
                    cellItem.add(new Label(componentId,
                            (payDate == null) ? "" : DateUtils.formatDate(paymentIModel.getObject().getDate())));
                }
            });
            columns.add(new AbstractColumn<User>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                @Override
                public void populateItem(Item<ICellPopulator<User>> cellItem,
                                         String componentId, final IModel<User> rowModel) {
                    try {
                        paymentIModel.setObject(coursePaymentManager.getPayment(
                                rowModel.getObject(), invoiceModel.getObject(), false));
                        priceModel.detach();
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot get payment info", e);
                        throw new WicketRuntimeException("Cannot get payment info", e);
                    }

                    cellItem.add(new ActionPanel(componentId) {
                        @Override
                        void onClose(AjaxRequestTarget target) {
                            try {
                                genericManager.delete(paymentIModel.getObject());
                                paymentIModel.setObject(null);
                                updateInfo(target);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot remove payment", e);
                                throw new WicketRuntimeException("Cannot remove payment", e);
                            }
                        }

                        @Override
                        void onEdit(AjaxRequestTarget target) {
                            try {
                                editPrice(target, new GenericDBModel<>(CoursePayment.class,
                                        coursePaymentManager.getPayment(
                                                rowModel.getObject(), invoiceModel.getObject(), false)));
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot get payment info", e);
                                throw new WicketRuntimeException("Cannot get payment info", e);
                            }
                        }

                        @Override
                        boolean canBeClosed() {
                            try {
                                paymentIModel.setObject(coursePaymentManager.getPayment(
                                        rowModel.getObject(), invoiceModel.getObject(), false));
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot get payment info", e);
                                throw new WicketRuntimeException("Cannot get payment info", e);
                            }
                            priceModel.detach();
                            return priceModel.getObject() == 0;
                        }

                        @Override
                        boolean canBeEdited() {
                            try {
                                paymentIModel.setObject(coursePaymentManager.getPayment(
                                        rowModel.getObject(), invoiceModel.getObject(), false));
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot get payment info", e);
                                throw new WicketRuntimeException("Cannot get payment info", e);
                            }
                            priceModel.detach();
                            return (priceModel.getObject() != 0) && (!paymentIModel.getObject().getStatus());
                        }
                    });
                }


            });

        }
        return columns;
    }

    abstract protected boolean isPaid();

    abstract protected IModel<Course> getCourseModel();

    abstract protected void updateInfo(AjaxRequestTarget target);

    abstract protected void editPrice(AjaxRequestTarget target, IModel<CoursePayment> paymentIModel);

    private abstract class ActionPanel extends BaseEltilandPanel {

        public ActionPanel(String id) {
            super(id);

            EltiAjaxLink closeButton = new EltiAjaxLink("closeButton") {
                {
                    add(new ConfirmationDialogBehavior());
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    onClose(target);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }

                @Override
                public boolean isVisible() {
                    return canBeClosed();
                }
            };

            EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    onEdit(target);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    return null;
                }

                @Override
                public boolean isVisible() {
                    return canBeEdited();
                }
            };

            add(closeButton);
            add(editButton);
            closeButton.add(new AttributeModifier("title", new ResourceModel("cancelAction")));
            closeButton.add(new TooltipBehavior());
            editButton.add(new AttributeModifier("title", new ResourceModel("editAction")));
            editButton.add(new TooltipBehavior());
        }

        abstract void onClose(AjaxRequestTarget target);

        abstract void onEdit(AjaxRequestTarget target);

        abstract boolean canBeClosed();

        abstract boolean canBeEdited();
    }

    private class LinkPanel extends BaseEltilandPanel<User> {

        public LinkPanel(String id, final IModel<User> userModel) {
            super(id);

            EltiAjaxLink link = new EltiAjaxLink("linkProfile") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    setResponsePage(ProfileViewPage.class,
                            new PageParameters().add(ProfileViewPage.PARAM_ID, userModel.getObject().getId()));
                }
            };
            link.add(new Label("linkText", userModel.getObject().getName()));
            add(link);
        }
    }
}
