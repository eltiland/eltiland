package com.eltiland.bl.validators;

import com.eltiland.bl.WebinarSubscriptionPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Webinar subcription validator.
 * <p/>
 */
@Component
public class WebinarSubscriptionPaymentValidator {
    @Autowired
    private WebinarSubscriptionPaymentManager webinarSubscriptionPaymentManager;

    public void validate(WebinarSubscriptionPayment webinarSubscriptionPayment) throws WebinarException {
        if (webinarSubscriptionPayment == null) {
            throw new WebinarException(EltilandManagerException.ERROR_EMPTY_ENTITY);
        }
        if (webinarSubscriptionPayment.getSubscription() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_SUB_EMPTY);
        }
        if (webinarSubscriptionPayment.getUserProfile() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_USER_EMPTY);
        }
        if ((webinarSubscriptionPayment.getId() == null) && webinarSubscriptionPaymentManager.getPayment(
                webinarSubscriptionPayment.getSubscription(), webinarSubscriptionPayment.getUserProfile()) != null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_EXISTS);
        }
        String userName = webinarSubscriptionPayment.getUserName();
        if ((userName != null) &&  userName.length() > 255) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_NAME_TOO_LONG);
        }
        String userSurname = webinarSubscriptionPayment.getUserSurname();
        if ((userSurname != null) &&  userSurname.length() > 255) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_SURNAME_TOO_LONG);
        }
        String patronymic = webinarSubscriptionPayment.getPatronymic();
        if ((patronymic != null) &&  patronymic.length() > 255) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_PATRONYMIC_TOO_LONG);
        }
        if (webinarSubscriptionPayment.getStatus() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_STATUS_EMPTY);
        }
        if (webinarSubscriptionPayment.getPrice() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_PRICE_EMPTY);
        }
        if (webinarSubscriptionPayment.getRegistrationDate() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_REGDATE_EMPTY);
        }
    }
}
