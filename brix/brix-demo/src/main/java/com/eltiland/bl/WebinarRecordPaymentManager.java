package com.eltiland.bl;

import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarRecordPayment;

import java.util.List;

/**
 * Interface for managing Webinars record payment invoices.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarRecordPaymentManager {

    /**
     * @return TRUE if current user hav already has invoice to access to the given record with status PAYS or CONFIRMED.
     */
    boolean hasRecordInvoicesForCurrentUser(WebinarRecord record);

    /**
     * Get record payment by it's link.
     *
     * @param link - link of the record payment.
     * @return corresponding record payment.
     */
    WebinarRecordPayment getPaymentByLink(String link);

    /**
     * @param searchString search string.
     * @return payment count, which are not in PAYS status.
     */
    int getPaymentsCount(String searchString);

    /**
     * Get all payments, which are not in PAYS status.
     *
     * @param index        the start position of the first result, numbered from 0.
     * @param count        the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty    the sorting property name
     * @param isAscending  the sorting direction.
     * @param searchString search string.
     * @return List of all payments, which are not in PAYS status.
     */
    List<WebinarRecordPayment> getPaymentsList(
            int index, Integer count, String sProperty, boolean isAscending, String searchString);


    /**
     * Get payment information for given user on given webinar.
     *
     *
     * @param webinarRecord
     * @param user    user entity.
     * @return payment info. (can be NULL).
     */
    WebinarRecordPayment getPaymentForUser(WebinarRecord webinarRecord, User user);
}
