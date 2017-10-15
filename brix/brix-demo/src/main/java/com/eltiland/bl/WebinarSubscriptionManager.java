package com.eltiland.bl;

import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarSubscription;

import java.util.List;

/**
 * Interface for managing Webinar's Subscriptions.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarSubscriptionManager {

    /**
     * Returns count of webinar subscriptions.
     */
    int getCount();

    /**
     * Returns list of webinar's subscription.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return list of subscriptions.
     */
    List<WebinarRecord> getList(int index, Integer count, String sProperty, boolean isAscending);

    /**
     * Creates and persists new webinar subscription.
     *
     * @param subscription item to create.
     * @return created item
     */
    WebinarSubscription create(WebinarSubscription subscription) throws WebinarException;

    /**
     * Updates webinar subscription.
     *
     * @param item to to update.
     * @return persisted item.
     */
    WebinarSubscription update(WebinarSubscription item) throws WebinarException;
}
