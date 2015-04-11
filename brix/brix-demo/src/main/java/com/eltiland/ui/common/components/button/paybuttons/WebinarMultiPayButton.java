package com.eltiland.ui.common.components.button.paybuttons;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;

/**
 * Button for webinar paying for multiply users.
 *
 * @author Aleksey Plotnikov
 */
public class WebinarMultiPayButton extends PayButton {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public WebinarMultiPayButton(String id) {
        super(id);
    }

    @Override
    public String getPaymentId() {
        return "P" + entityModel.getObject().getPaidId().toString() + "#" + getTimeString();
    }

    @Override
    public BigDecimal getPrice() {
        return entityModel.getObject().getPrice();
    }

    @Override
    public String getDescription() {
        WebinarMultiplyPayment payment = (WebinarMultiplyPayment) entityModel.getObject();

        String baseDescription = getString("webinarPayDescription");

        genericManager.initialize(payment, payment.getUsers());
        return String.format(baseDescription, payment.getWebinar().getName(), payment.getUsers().size());
    }
}
