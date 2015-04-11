package com.eltiland.ui.payment;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.model.payment.PaidEntity;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.ui.common.components.button.paybuttons.RecordPayButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Internal page for webinar paying
 *
 * @author Aleksey Plotnikov
 */
public class RecordPaymentPage extends AbstractPaymentPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;

    public static final String MOUNT_PATH = "/recordPayment";

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public RecordPaymentPage(PageParameters parameters) {
        super(parameters);

        WebinarRecordPayment payment = (WebinarRecordPayment) getEntity();

        genericManager.initialize(payment, payment.getRecord());
        genericManager.initialize(payment.getRecord(), payment.getRecord().getWebinar());
        Webinar webinar = payment.getRecord().getWebinar();

        WebMarkupContainer container = getMain();
        container.add(new Label("headerLabel", String.format(getString("headerLabel"), webinar.getName())));
        container.add(new Label("priceLabel", String.format(getString("priceValue"), payment.getPrice().toString())));

        RecordPayButton payButton = new RecordPayButton("payButton");
        payButton.setPaymentData(payment);
        container.add(payButton);
    }

    @Override
    protected PaidEntity getEntity(String code) {
        return webinarRecordPaymentManager.getPaymentByLink(code);
    }
}
