package com.eltiland.ui.paymentnew;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProcessingPage.class);

    public static final String MOUNT_PATH = "/payProc";

    private static final String SUCCESS_CODE = "AS000";

    public PaymentProcessingPage() {
        super();

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
    }

    private PaidEntityNew getEntity(Long id) {
        ELTCourseListener entity1 = genericManager.getObject(ELTCourseListener.class, id);
        if (entity1 == null) {
            return null;
        } else {
            return entity1;
        }
    }
}
