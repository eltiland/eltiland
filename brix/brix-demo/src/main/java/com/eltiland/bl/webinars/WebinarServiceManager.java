package com.eltiland.bl.webinars;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;

import java.util.List;
import java.util.Map;

/**
 * Webinar service interface.
 *
 * @author Aleksey Plotnikov
 */
public interface WebinarServiceManager {

    /**
     * Authentication.
     */
    void authenticate() throws EltilandManagerException;

    /**
     * Webinar creation.
     *
     * @param webinar webinar structure to create.
     * @return TRUE, if request successful.
     */
    boolean createWebinar(Webinar webinar) throws EltilandManagerException;

    /**
     * Webinar removing.
     *
     * @param webinar webinar to remove.
     * @return TRUE, if request successful.
     */
    boolean removeWebinar(Webinar webinar) throws EltilandManagerException;

    /**
     * Webinar data updating.
     *
     * @param webinar webinar to update.
     * @return TRUE, if request successful.
     */
    boolean updateWebinar(Webinar webinar) throws EltilandManagerException;

    /**
     * Adding user to webinar.
     *
     * @param user user to add.
     * @return TRUE, if request successful.
     */
    boolean addUser(WebinarUserPayment user) throws EltilandManagerException;

    /**
     * Removing user from webinar.
     *
     * @param user user to remove.
     * @return TRUE, if request successful.
     */
    boolean removeUser(WebinarUserPayment user) throws EltilandManagerException;

    /**
     * Updating webinar user.
     *
     * @param user user to update.
     * @return TRUE, if request successful.
     */
    boolean updateUser(WebinarUserPayment user) throws EltilandManagerException;

    /**
     * Get list of user data's of given webinar.
     *
     * @param webinar webinar to check.
     * @return list of users.
     */
    Map<String, String> getUsersData(Webinar webinar) throws EltilandManagerException;
}
