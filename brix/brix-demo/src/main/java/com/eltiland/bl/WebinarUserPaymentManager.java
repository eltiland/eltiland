package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;

import java.util.List;

/**
 * Interface for managing Webinar's Users.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarUserPaymentManager {

    /**
     * Updating payment.
     *
     * @param payment payment to update.
     * @return updated payment.
     */
    WebinarUserPayment update(WebinarUserPayment payment) throws EltilandManagerException;

    /**
     * Adding new moderator to given Webinar, persist it.
     *
     * @param user webinar user to add.
     * @return TRUE if request was successful.
     */
    boolean createModerator(WebinarUserPayment user) throws EltilandManagerException, WebinarException;

    /**
     * Adding new user to given Webinar, persist it.
     *
     * @param user webinar user to add.
     * @return TRUE if request was successful.
     */
    boolean createUser(WebinarUserPayment user) throws EltilandManagerException, EmailException, WebinarException;

    /**
     * @param webinar webinar to check.
     * @param email   email of the user.
     * @return TRUE if user with given email already registered to webinar.
     */
    boolean hasAlreadyRegistered(Webinar webinar, String email);

    /**
     * Remove user from webinar.
     *
     * @param webinar webinar from which user will be deleted.
     * @param user    user to delete from webinar.
     */
    void removeUserFromWebinar(Webinar webinar, User user);

    /**
     * Get count of users, registered on given webinar by search pattern.
     *
     * @param webinar webinar.
     * @param pattern search pattern.
     * @return Count of users, registered on given webinar.
     */
    int getWebinarUserCount(Webinar webinar, String pattern) throws EltilandManagerException;

    /**
     * @return Count of users, registered on given webinar and confirmed.
     */
    int getWebinarConfirmedUserCount(Webinar webinar);

    /**
     * Return sorted list of users for given webinar.
     *
     * @param webinar     given webinar item.
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param status      status of the payment.
     * @return List of users, registered on given webinar.
     */
    List<WebinarUserPayment> getWebinarUserList(Webinar webinar, int index, Integer count,
                                                String sProperty, boolean isAscending,
                                                PaidStatus status) throws EltilandManagerException;

    /**
     * Return list of users for given webinar (not moderators).
     *
     * @param webinar given webinar item.
     * @return List of users, registered on given webinar.
     */
    List<WebinarUserPayment> getWebinarRealListeners(Webinar webinar);

    /**
     * Return sorted list of users for given webinar.
     *
     * @param webinar     given webinar item.
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param pattern     search pattern.
     * @return List of users, registered on given webinar.
     */
    List<WebinarUserPayment> getWebinarUserList(
            Webinar webinar, int index, Integer count,
            String sProperty, boolean isAscending, String pattern) throws EltilandManagerException;

    /**
     * Remove user payment.
     *
     * @param userPayment item to remove.
     */
    void removeUser(WebinarUserPayment userPayment) throws EltilandManagerException;

    /**
     * Get user payment by its pay link.
     *
     * @param payLink pay link.
     */
    WebinarUserPayment getPaymentByLink(String payLink);

    /**
     * Get webinar user payment by it's id.
     *
     * @param id payment id.
     */
    WebinarUserPayment getWebinarPaymentById(long id);

    /**
     * Get webinar user payment list by webinar.
     *
     * @param webinar given webinar.
     * @return user payment entity by webinar.
     */
    List<WebinarUserPayment> getWebinarPayments(Webinar webinar);

    /**
     * Pay for webinar user payment.
     *
     * @param payment webinar user payment.
     */
    boolean payWebinarUserPayment(WebinarUserPayment payment) throws EltilandManagerException;

    /**
     * @return pay status ( PAYS or CONFIRMED) for given user on given webinar.
     */
    boolean getWebinarStatusForUser(Webinar webinar, User user);

    /**
     * @return count of the payments, which are already paid.
     */
    int getPaidPaymentsCount(String searchString);

    /**
     * Return sorted list of the payments, which are already paid.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return sorted list of the payments, which are already paid.
     */
    List<WebinarUserPayment> getPaidPaymentsList(int index, Integer count, String sProperty, boolean isAscending,
                                                 String searchString);

    /**
     * Change role of user in webinar.
     *
     * @param payment user payment object.
     * @return updated user payment.
     */
    WebinarUserPayment updateWebinarUser(WebinarUserPayment payment) throws EltilandManagerException;

    /**
     * Get list of payments, sorted by registration date.
     *
     * @param userProfile user profile entity.
     * @param history     TRUE - for past webinars
     * @param status      - payment status (NULL - all payments).
     */
    List<WebinarUserPayment> getWebinarPayments(User userProfile, boolean history, Boolean status);

    /**
     * Check for user is added to webinar in fact.
     *
     * @param payment user payment object to check.
     * @return webinar user link.
     */
    String getLink(WebinarUserPayment payment) throws EltilandManagerException;

    /**
     * Check for all user is added to webinar in fact.
     *
     * @param webinar webinar to check.
     * @return list of users which are not present in webinar. if NULL - it's all right.
     */
    List<WebinarUserPayment> checkWebinarUsers(Webinar webinar) throws EltilandManagerException;

    /**
     * Get payment infor for given user on given webinar.
     *
     * @param webinar webinar entity.
     * @param user    user entity.
     * @return payment info. (can be NULL).
     */
    WebinarUserPayment getPaymentForUser(Webinar webinar, User user);
}
