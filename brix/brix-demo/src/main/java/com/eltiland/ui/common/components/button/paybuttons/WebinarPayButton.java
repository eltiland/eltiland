package com.eltiland.ui.common.components.button.paybuttons;

import com.eltiland.model.webinar.WebinarUserPayment;

import java.math.BigDecimal;

/**
 * Button for webinar paying.
 *
 * @author Aleksey Plotnikov
 */
public class WebinarPayButton extends PayButton {
    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public WebinarPayButton(String id) {
        super(id);
    }

    @Override
    public String getPaymentId() {
        return "W" + entityModel.getObject().getPaidId().toString() + "#" + getTimeString();
    }

    @Override
    public BigDecimal getPrice() {
        return entityModel.getObject().getPrice();
    }

    @Override
    public String getDescription() {
        WebinarUserPayment payment = (WebinarUserPayment) entityModel.getObject();

        String baseDescription = getString("webinarPayDescription");

        return String.format(baseDescription, payment.getWebinar().getName(),
                payment.getUserName() + " " + payment.getUserSurname());
    }
}
