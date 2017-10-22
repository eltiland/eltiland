package com.eltiland.ui.paymentnew;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.OneColumnPage;
import com.eltiland.ui.common.components.button.paybutton.PayButton;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * General payment page.
 *
 * @author Aleksey Plotnikov.
 */
public class PaymentPage extends OneColumnPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentPage.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    public static final String MOUNT_PATH = "/payment";

    public static final String CSS_PAYMENT_PAGE = "static/css/panels/payment_page.css";

    public PaymentPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final IModel<PaidEntityNew> paidEntityModel = new LoadableDetachableModel<PaidEntityNew>() {
            @Override
            protected PaidEntityNew load() {

                long id = parameters.get(PARAM_ID).toLong();
                ELTCourseListener entity1 = courseListenerManager.getById(id);
                if (entity1 == null) {
                    WebinarRecordPayment entity2 = genericManager.getObject(WebinarRecordPayment.class, id);
                    if (entity2 == null) {
                        WebinarUserPayment entity3 = genericManager.getObject(WebinarUserPayment.class, id);
                        if (entity3 == null) {
                            WebinarSubscriptionPayment entity4 =
                                    genericManager.getObject(WebinarSubscriptionPayment.class, id);
                            if( entity4 != null ) {
                                return entity4;
                            } else {
                                return null;
                            }
                        } else {
                            return entity3;
                        }
                    } else {
                        return entity2;
                    }
                } else {
                    return entity1;
                }
            }
        };

        if (paidEntityModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        String className = paidEntityModel.getObject().getClass().getSimpleName();

        add(new Label("header", getString("pay.for") + getString(className + ".type")));

        WebMarkupContainer image = new WebMarkupContainer("image");
        image.add(new AttributeAppender("class", new ResourceModel(className + ".class"), " "));
        add(image);

        add(new Label("name", paidEntityModel.getObject().getEntityName()));
        Label desc = new Label("description", "");
        String description = null;

        if (paidEntityModel.getObject() instanceof ELTCourseListener) {
            description = String.format(getString("author.course"),
                    ((ELTCourseListener) paidEntityModel.getObject()).getCourse().getAuthor().getName());
            if (((ELTCourseListener) paidEntityModel.getObject()).getCourse().getDays() == null) {
                description += " " + getString("access.unlimited");
            } else {
                description += " " + String.format(getString("access.limited"),
                        ((ELTCourseListener) paidEntityModel.getObject()).getCourse().getDays());
            }
        } else {
            description = paidEntityModel.getObject().getDescription();
        }

        desc.setDefaultModelObject(description);
        add(desc);

        add(new Label("value", paidEntityModel.getObject().getPrice().toString()));
        add(new PayButton("pay_button", paidEntityModel));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS_PAYMENT_PAGE);
    }
}