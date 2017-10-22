package com.eltiland.ui.paymentnew;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionPaymentManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.webinars.plugin.tab.WUserManagementPanel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Internal page for processing payments from Assist.
 *
 * @author Aleksey Plotnikov.
 */
public class PaymentProcessingPage extends WebPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private WebinarSubscriptionPaymentManager webinarSubscriptionPaymentManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProcessingPage.class);

    public static final String MOUNT_PATH = "/payproc";

    private static final String SUCCESS_CODE = "AS000";

    public PaymentProcessingPage() {
        super();

        LOGGER.info("Payment start");
        HttpServletRequest req = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();
        String orderNumber = req.getParameter("ordernumber");
        String responseCode = req.getParameter("responsecode");
        if (orderNumber == null || orderNumber.isEmpty() || !responseCode.equals(SUCCESS_CODE)) {
            return;
        }

        LOGGER.info("Payment coming - " + orderNumber + ", status - " + responseCode);
        Long id = Long.valueOf(orderNumber.split("_")[0]);
        PaidEntityNew entity = getEntity(id);
        entity.setStatus(PaidStatus.CONFIRMED);
        entity.setPayDate(DateUtils.getCurrentDate());
        try {
            genericManager.update(entity);
        } catch (ConstraintException e) {
            LOGGER.error(e.getMessage());
        }

        if (entity instanceof ELTCourseListener) {
            try {
                emailMessageManager.sendCoursePayMessage((ELTCourseListener) entity);
            } catch (EmailException e) {
                LOGGER.error(e.getMessage());
            }
        }
        if (entity instanceof WebinarRecordPayment) {
            InputStream pdfStream = null;
            try {
                pdfStream = webinarCertificateGenerator.generateRecordCertificate((WebinarRecordPayment) entity);
            } catch (EltilandManagerException e) {
                LOGGER.error(e.getMessage());
            }
            try {
                emailMessageManager.sendRecordLinkToUser((WebinarRecordPayment) entity, pdfStream);
            } catch (EmailException e) {
                LOGGER.error(e.getMessage());
            }
        }
        if (entity instanceof WebinarUserPayment) {
            WebinarUserPayment payment = webinarUserPaymentManager.getWebinarPaymentById(id);
            if (payment != null && !(payment.getStatus().equals(PaidStatus.CONFIRMED))) {
                try {
                    webinarUserPaymentManager.payWebinarUserPayment(payment);
                } catch (EltilandManagerException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }

        if (entity instanceof WebinarSubscriptionPayment) {
            WebinarSubscriptionPayment payment = genericManager.getObject(WebinarSubscriptionPayment.class, id);
            if (payment != null && !(payment.getStatus().equals(PaidStatus.CONFIRMED))) {
                try {
                    webinarSubscriptionPaymentManager.pay(payment);
                } catch (WebinarException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }

    }

    private PaidEntityNew getEntity(Long id) {
        ELTCourseListener entity1 = genericManager.getObject(ELTCourseListener.class, id);
        if (entity1 == null) {
            WebinarRecordPayment payment = genericManager.getObject(WebinarRecordPayment.class, id);
            if (payment == null) {
                WebinarUserPayment payment1 = genericManager.getObject(WebinarUserPayment.class, id);
                if (payment1 == null) {
                    WebinarSubscriptionPayment payment2 = genericManager.getObject(WebinarSubscriptionPayment.class, id);
                    if (payment2 != null) {
                        return payment2;
                    } else {
                        return null;
                    }
                } else {
                    return payment1;
                }
            } else {
                return payment;
            }
        } else {
            return entity1;
        }
    }
}
