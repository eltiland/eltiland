package com.eltiland.bl;

import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course payment manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CoursePaymentManager {

    /**
     * Creaye and persist new CoursePayment entity.
     *
     * @param payment payment to create.
     * @return persisted entity.
     */
    CoursePayment createPayment(CoursePayment payment) throws EltilandManagerException;

    /**
     * Get payment info for given user/invoice.
     *
     * @param listener         user-listener.
     * @param invoice          paid invoice for given entity.
     * @param createIfNoExists if TRUE - if there is no payment, function will create it and return.
     * @return Last payment info, NULL if not exists.
     */
    CoursePayment getPayment(User listener, CoursePaidInvoice invoice, boolean createIfNoExists)
            throws EltilandManagerException;

    CoursePayment getPayment(User listener, CoursePaidInvoice invoice) throws EltilandManagerException;


    /**
     * Check if entity is paid by given user.
     *
     * @param listener user-listener.
     * @param invoice  paid invoice for given entity.
     * @return TRUE if entity is already paid.
     */
    boolean isEntityPaidByUser(User listener, CoursePaidInvoice invoice);

    /**
     * Pay given payment.
     */
    void payCoursePayment(CoursePayment payment) throws ConstraintException, EltilandManagerException, UserException;

    /**
     * @return paid payment count.
     */
    int getPaidPaymentCount();

    /**
     * @param index     index of the first element.
     * @param count     count of elements.
     * @param sProperty sorting property.
     * @param isAsc     if TRUE - use ascending sorting.
     * @return Formatted list of all paid payments.
     */
    List<CoursePayment> getListOfPaidPayments(int index, int count, String sProperty, boolean isAsc);
}
