package com.eltiland.ui.payment;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.model.payment.PaidEntity;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.components.button.paybuttons.WebinarPayButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Internal page for webinar paying
 *
 * @author Aleksey Plotnikov
 */
public class WebinarPaymentPage extends AbstractWebinarPaymentPage {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    public static final String MOUNT_PATH = "/webinarPayment";

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public WebinarPaymentPage(PageParameters parameters) {
        super(parameters);

        WebinarUserPayment payment = (WebinarUserPayment) getEntity();
        WebMarkupContainer container = getMain();

        WebinarPayButton payButton = new WebinarPayButton("payButton");
        payButton.setPaymentData(payment);
        container.add(payButton);
    }

    @Override
    protected PaidEntity getEntity(String code) {
        return webinarUserPaymentManager.getPaymentByLink(code);
    }
}
