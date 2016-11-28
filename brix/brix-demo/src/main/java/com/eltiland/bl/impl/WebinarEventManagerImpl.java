package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarEventManager;
import com.eltiland.bl.validators.WebinarEventValidator;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Class for managing Webinars Events.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarEventManagerImpl extends ManagerImpl implements WebinarEventManager {

    @Autowired
    private WebinarEventValidator webinarEventValidator;
    @Autowired
    private GenericManager genericManager;
    @Qualifier("webinarServiceV3Impl")
    @Autowired
    private WebinarServiceManager webinarServiceManager;

    @Override
    public WebinarEvent create(WebinarEvent event) throws WebinarException {
        webinarEventValidator.isValid(event);

        try {
            event.setEventId(0);
            event = genericManager.saveNew(event);

        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE, e);
        }

        Long eventid = webinarServiceManager.createEvent(event);
        event.setEventId(eventid);
        try {
            genericManager.update(event);
            return event;
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_EVENT_CREATE, e);
        }
    }
}
