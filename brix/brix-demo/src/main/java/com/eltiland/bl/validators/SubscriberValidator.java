package com.eltiland.bl.validators;

import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;
import org.springframework.stereotype.Component;

/**
 * Validator for Subscriber model
 * @author Pavel Androschuk
 */
@Component
public class SubscriberValidator {
    public void validate(Subscriber subscriber) throws SubscriberException {
        if (subscriber == null) {
            throw new SubscriberException(SubscriberException.EMPTY_ENTITY_ERROR);
        }

        if (subscriber.getEmail() == null) {
            throw new SubscriberException(SubscriberException.EMPTY_EMAIL_ERROR);
        }

        if (subscriber.getEmail().isEmpty()) {
            throw new SubscriberException(SubscriberException.EMPTY_EMAIL_ERROR);
        }

        if (subscriber.getUnsubscribe() == null) {
            throw new SubscriberException(SubscriberException.EMPTY_UNSUBSRIBE_LINK_ERROR);
        }

        if (subscriber.getUnsubscribe().isEmpty()) {
            throw new SubscriberException(SubscriberException.EMPTY_UNSUBSRIBE_LINK_ERROR);
        }
    }
}
