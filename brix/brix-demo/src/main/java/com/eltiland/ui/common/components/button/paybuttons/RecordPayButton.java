package com.eltiland.ui.common.components.button.paybuttons;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.webinar.WebinarRecordPayment;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;

/**
 * Button for webinar paying.
 *
 * @author Aleksey Plotnikov
 */
public class RecordPayButton extends PayButton {

    @SpringBean
    private GenericManager genericManager;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public RecordPayButton(String id) {
        super(id);
    }

    @Override
    public String getPaymentId() {
        return "R" + entityModel.getObject().getPaidId().toString() + "#" + getTimeString();
    }

    @Override
    public BigDecimal getPrice() {
        return entityModel.getObject().getPrice();
    }

    @Override
    public String getDescription() {
        WebinarRecordPayment payment = (WebinarRecordPayment) entityModel.getObject();

        String baseDescription = getString("recordPayDescription");
        genericManager.initialize(payment, payment.getRecord());
        genericManager.initialize(payment, payment.getUserProfile());
        genericManager.initialize(payment.getRecord(), payment.getRecord().getWebinar());

        return String.format(baseDescription, payment.getRecord().getWebinar().getName(),
                payment.getUserProfile().getName());
    }
}
