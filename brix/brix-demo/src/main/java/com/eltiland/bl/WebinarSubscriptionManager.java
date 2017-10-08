package com.eltiland.bl;

import com.eltiland.model.webinar.WebinarRecord;

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

}
