package com.eltiland.ui.payment;

import com.eltiland.model.payment.WebinarPayment;
import com.eltiland.model.webinar.Webinar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Abstract webinar payment page.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractWebinarPaymentPage extends AbstractPaymentPage {
    public AbstractWebinarPaymentPage(PageParameters parameters) {
        super(parameters);

        WebinarPayment payment = (WebinarPayment) getEntity();
        Webinar webinar = payment.getWebinar();

        WebMarkupContainer container = getMain();
        container.add(new Label("headerLabel", String.format(getString("headerLabel"), webinar.getName())));
        container.add(new Label("descriptionLabel", webinar.getDescription()));
        container.add(new Label("managerLabel", webinar.getManagername() + " " + webinar.getManagersurname()));
        container.add(new Label("startDateLabel", webinar.getStartDate().toString()));
        container.add(new Label("durationLabel", String.format(getString("dateValue"), webinar.getDuration())));
        container.add(new Label("priceLabel", String.format(getString("priceValue"), payment.getPrice().toString())));
    }
}
