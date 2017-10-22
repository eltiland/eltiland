package com.eltiland.bl;

import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;

/**
 * Interface for managing Webinars subscription payment invoices.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarSubscriptionPaymentManager {
    /**
     * Get payment for given user for given subscription.
     *
     * @param subscription subscription item.
     * @param user         user.
     * @return payment for given parameters.
     */
    WebinarSubscriptionPayment getPayment(WebinarSubscription subscription, User user);

    /**
     * Creates and persists new webinar payment subscription.
     *
     * @param subscription item to create.
     * @return created item
     */
    WebinarSubscriptionPayment create(WebinarSubscriptionPayment subscription) throws WebinarException;

    /**
     * Updates webinar payment subscription.
     *
     * @param item to to update.
     * @return persisted item.
     */
    WebinarSubscriptionPayment update(WebinarSubscriptionPayment item) throws WebinarException;

    void pay(WebinarSubscriptionPayment payment) throws WebinarException;
}
