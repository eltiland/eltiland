package com.eltiland.bl.validators;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarSubscription;
import org.springframework.stereotype.Component;

/**
 * Webinar subcription validator.
 */
@Component
public class WebinarSubscriptionValidator {
    public void validate(WebinarSubscription webinarSubscription) throws WebinarException {
        if (webinarSubscription == null) {
            throw new WebinarException(EltilandManagerException.ERROR_EMPTY_ENTITY);
        }
        if(webinarSubscription.getName() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE_NAME_EMPTY);
        }
        if(webinarSubscription.getName().isEmpty()) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE_NAME_EMPTY);
        }
        if(webinarSubscription.getName().length() > 255) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE_NAME_TOO_LONG);
        }
        if(webinarSubscription.getInfo() != null && webinarSubscription.getInfo().length() > 1024) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE_INFO_TOO_LONG);
        }
        if(webinarSubscription.getWebinars() == null || webinarSubscription.getWebinars().isEmpty()) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE_WEBINARS_EMPTY);
        }

    }
}
