package com.eltiland.ui.payment;

import com.eltiland.bl.*;
import com.eltiland.bl.magazine.ClientManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * Internal page for save results of the
 *
 * @author Aleksey Plotnikov
 */
public class W1PaymentPage extends WebPage implements IMarkupResourceStreamProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(W1PaymentPage.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private CoursePaymentManager coursePaymentManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ClientManager clientManager;
    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @SpringBean
    private WebinarMultiplyPaymentManager webinarMultiplyPaymentManager;


    public static final String MOUNT_PATH = "/paymentResult";

    public W1PaymentPage() {
        super();
        HttpServletRequest req = (HttpServletRequest) RequestCycle.get().getRequest().getContainerRequest();

        String paymentId = req.getParameter("WMI_PAYMENT_NO");
        String status = req.getParameter("WMI_ORDER_STATE");

        if (status != null) {
            if (status.equals("Accepted")) {
                if (paymentId != null) {
                    String paymentParts[] = paymentId.split("#");
                    if (paymentParts[0].contains("_")) {
                        String paymentsIds[] = paymentParts[0].split("_");
                        payPayment(paymentsIds[0]);
                        payPayment(paymentsIds[1]);
                    } else {
                        payPayment(paymentParts[0]);
                    }
                }
            } else {
                // TODO: error handling.
            }
        }
    }

    private void payPayment(String paymentId) {
        boolean paidWebinars = false;
        boolean paidCourse = false;
        boolean paidMagazine = false;
        boolean paidRecord = false;
        boolean paidMWebinar = false;

        if (paymentId.charAt(0) == 'W') {
            paymentId = paymentId.replace("W", "");
            paidWebinars = true;
        } else if (paymentId.charAt(0) == 'C') {
            paymentId = paymentId.replace("C", "");
            paidCourse = true;
        } else if (paymentId.charAt(0) == 'M') {
            paymentId = paymentId.replace("M", "");
            paidMagazine = true;
        } else if (paymentId.charAt(0) == 'R') {
            paymentId = paymentId.replace("R", "");
            paidRecord = true;
        } else if (paymentId.charAt(0) == 'P') {
            paymentId = paymentId.replace("P", "");
            paidMWebinar = true;
        }


        int id = Integer.parseInt(paymentId);
        try {
            if (paidMWebinar) {
                WebinarMultiplyPayment payment = genericManager.getObject(WebinarMultiplyPayment.class, (long) id);
                if (payment != null && !(payment.getStatus())) {
                    webinarMultiplyPaymentManager.payWebinarUserPayment(payment);
                }
            } else if (paidWebinars) {
                WebinarUserPayment payment = webinarUserPaymentManager.getWebinarPaymentById(id);
                if (payment != null && !(payment.getStatus())) {
                    webinarUserPaymentManager.payWebinarUserPayment(payment);
                }
            } else if (paidCourse) {
                CoursePayment payment = genericManager.getObject(CoursePayment.class, (long) id);
                if (payment != null && !(payment.getStatus())) {
                    coursePaymentManager.payCoursePayment(payment);
                    // adding new listener
                    courseManager.addPaidListener(payment);
                }
            } else if (paidMagazine) {
                Client client = genericManager.getObject(Client.class, (long) id);
                if (client != null && !(client.getStatus())) {
                    clientManager.payClientMagazines(client);
                    emailMessageManager.sendDownloadMagazineLink(client);
                    emailMessageManager.sendMagazineActionToAdmin(client);
                }
            } else if (paidRecord) {
                WebinarRecordPayment payment = genericManager.getObject(WebinarRecordPayment.class, (long) id);
                if (payment != null && !(payment.getStatus())) {
                    payment.setStatus(true);
                    payment.setDate(DateUtils.getCurrentDate());
                    genericManager.update(payment);

                    InputStream pdfStream = webinarCertificateGenerator.generateRecordCertificate(payment);
                    emailMessageManager.sendRecordLinkToUser(payment, pdfStream);
                }
            }
        } catch (ConstraintException | EltilandManagerException | UserException e) {
            LOGGER.error("Cannot pay for payment invoice", e);
            throw new WicketRuntimeException(e);
        } catch (EmailException e) {
            LOGGER.error("Cannot send message about success payment", e);
            throw new WicketRuntimeException(e);
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream("WMI_RESULT=OK&WMI_DESCRIPTION=Order successfully processed");
    }
}
