package com.eltiland.bl.validators;

import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarEvent;
import org.springframework.stereotype.Component;

/**
 * Validator of Webinar Event.
 */
@Component
public class WebinarEventValidator {
    public void isValid(WebinarEvent event) throws WebinarException {
        if (event.getName() == null) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_NAME_EMPTY);
        }
    }
}
