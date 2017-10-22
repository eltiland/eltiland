package com.eltiland.bl;

import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;

import java.util.List;

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

    /**
     * @return payment count.
     */
    int getCount();

    /**
     * Get all payments
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of all payments, which are not in PAYS status.
     */
    List<WebinarSubscriptionPayment> getList(int index, Integer count, String sProperty, boolean isAscending);
}
