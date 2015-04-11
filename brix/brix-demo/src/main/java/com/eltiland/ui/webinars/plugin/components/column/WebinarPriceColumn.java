package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogProcessCallback;
import com.eltiland.ui.common.panels.changeprice.ChangePricePanel;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Column for output and changing webinar price for user.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarPriceColumn<T> extends AbstractColumn<T, WebinarUserPayment> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebinarPriceColumn.class);

    /**
     * Column constructor.
     *
     * @param columnId     column ID.
     * @param headerModel  header label model.
     * @param sortProperty sort property.
     */
    public WebinarPriceColumn(String columnId, IModel<String> headerModel, String sortProperty) {
        super(columnId, headerModel, sortProperty);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<WebinarUserPayment> rowModel) {
        return new PricePanel(componentId, rowModel) {
            @Override
            protected void onChangePrice(AjaxRequestTarget target) {
                onUpdate(target);
            }
        };
    }

    private abstract class PricePanel extends BaseEltilandPanel<WebinarUserPayment> {

        @SpringBean
        private WebinarUserPaymentManager webinarUserPaymentManager;
        @SpringBean
        private EmailMessageManager emailMessageManager;
        @SpringBean
        private GenericManager genericManager;
        @SpringBean
        private WebinarServiceManager webinarServiceManager;

        private Label priceLabel = new Label("priceLabel", new Model<>(""));

        /**
         * change price dialog
         */
        private Dialog<ChangePricePanel> changePriceDialog
                = new Dialog<ChangePricePanel>("changePriceDialog", 360) {
            @Override
            public ChangePricePanel createDialogPanel(String id) {
                WebinarUserPayment payment = PricePanel.this.getModelObject();
                genericManager.initialize(payment, payment.getWebinar());
                return new ChangePricePanel(id, new Model<>(payment.getWebinar().getPrice()));
            }

            @Override
            public void registerCallback(ChangePricePanel panel) {
                super.registerCallback(panel);
                panel.setProcessCallback(new IDialogProcessCallback.IDialogActionProcessor<BigDecimal>() {
                    @Override
                    public void process(IModel<BigDecimal> model, AjaxRequestTarget target) {
                        WebinarUserPayment payment = PricePanel.this.getModelObject();
                        payment.setPrice(model.getObject());
                        try {
                            if (model.getObject().equals(BigDecimal.valueOf(0))) {
                                payment.setStatus(true);
                                genericManager.initialize(payment, payment.getWebinar());
                                webinarServiceManager.addUser(payment);
                            }

                            webinarUserPaymentManager.update(payment);
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Cannot change price for user", e);
                            throw new WicketRuntimeException("Cannot change price for user", e);
                        }

                        try {
                            genericManager.initialize(payment, payment.getWebinar());
                            emailMessageManager.sendWebinarChangePriceToUser(payment);
                        } catch (EmailException e) {
                            LOGGER.error("Cannot send email to user", e);
                            throw new WicketRuntimeException("Cannot send email to user", e);
                        }

                        close(target);
                        onChangePrice(target);
                        ELTAlerts.renderOKPopup(getString("priceChangedMessage"), target);
                    }
                });
            }
        };


        protected PricePanel(String id, final IModel<WebinarUserPayment> webinarUserPaymentIModel) {
            super(id, webinarUserPaymentIModel);

            String priceString = getString("freeAccess");
            BigDecimal price = webinarUserPaymentIModel.getObject().getPrice();

            if (price != null && !(price.floatValue() == 0)) {
                priceString = price.toString();
            }

            priceLabel.setDefaultModelObject(priceString);
            add(priceLabel.setOutputMarkupId(true));

            add(new EltiAjaxLink("changeLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    changePriceDialog.show(target);
                }

                @Override
                public boolean isVisible() {
                    return isChangeVisible() && !(webinarUserPaymentIModel.getObject().getStatus());
                }
            });

            setOutputMarkupId(true);
            add(changePriceDialog);
        }

        protected abstract void onChangePrice(AjaxRequestTarget target);
    }

    public boolean isChangeVisible() {
        return true;
    }

    protected void onUpdate(AjaxRequestTarget target) {
    }
}
