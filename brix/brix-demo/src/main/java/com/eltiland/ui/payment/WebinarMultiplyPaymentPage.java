package com.eltiland.ui.payment;

import com.eltiland.bl.WebinarMultiplyPaymentManager;
import com.eltiland.model.payment.PaidEntity;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.ui.common.components.button.paybuttons.WebinarMultiPayButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Internal page for webinar paying
 *
 * @author Aleksey Plotnikov
 */
public class WebinarMultiplyPaymentPage extends AbstractWebinarPaymentPage {

    @SpringBean
    private WebinarMultiplyPaymentManager webinarMultiplyPaymentManager;

    public static final String MOUNT_PATH = "/webinarMPayment";

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public WebinarMultiplyPaymentPage(PageParameters parameters) {
        super(parameters);

        WebinarMultiplyPayment payment = (WebinarMultiplyPayment) getEntity();
        WebMarkupContainer container = getMain();

        WebinarMultiPayButton payButton = new WebinarMultiPayButton("payButton");
        payButton.setPaymentData(payment);
        container.add(payButton);
    }

    @Override
    protected PaidEntity getEntity(String code) {
        return webinarMultiplyPaymentManager.getPaymentByLink(code);
    }
}
