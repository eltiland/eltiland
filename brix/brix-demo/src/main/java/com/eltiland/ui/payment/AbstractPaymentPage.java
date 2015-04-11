package com.eltiland.ui.payment;

import com.eltiland.model.payment.PaidEntity;
import com.eltiland.ui.common.OneColumnPage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;

/**
 * Abstract Payment Page.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractPaymentPage extends OneColumnPage {

    private IModel<PaidEntity> paidEntityIModel = new GenericDBModel<>(PaidEntity.class);

    private WebMarkupContainer webinarPage = new WebMarkupContainer("webinarPage");

    public AbstractPaymentPage(PageParameters parameters) {
        super(parameters);
        String code = parameters.get(UrlUtils.PAYMENT_CODE_PARAMETER_NAME).toString();

        WebMarkupContainer alreadyPaidContainer = new WebMarkupContainer("alreadyPaidContainer");

        add(webinarPage.setOutputMarkupId(true));
        add(alreadyPaidContainer.setOutputMarkupId(true));

        if (code == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        } else {
            PaidEntity payment = getEntity(code);
            paidEntityIModel.setObject(payment);
            if (payment == null) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
            }
            boolean status = payment.getStatus();
            webinarPage.setVisible(!status);
            alreadyPaidContainer.setVisible(status);
        }
    }

    protected abstract PaidEntity getEntity(String code);

    protected PaidEntity getEntity() {
        return paidEntityIModel.getObject();
    }

    protected WebMarkupContainer getMain() {
        return webinarPage;
    }
}
