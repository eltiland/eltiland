package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;

import java.util.List;

/**
 * Interface for managing Webinars.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarManager {

    /**
     * Authentication. Using current credentials on properties.
     *
     * @throws EltilandManagerException in case of wrong authentication.
     */
    void authenticate() throws EltilandManagerException;

    /**
     * Webinar creation. Create event on webinar service and persist webinar item.
     *
     * @param webinar webinar structure to create.
     * @return newly persisted webinar entity.
     */
    Webinar create(Webinar webinar) throws EltilandManagerException, WebinarException;

    /**
     * Webinar removing. Drop webinar event and removes persisted item.
     *
     * @param webinar webinar structure to drop.
     * @return TRUE if request was successful.
     */
    boolean remove(Webinar webinar) throws EltilandManagerException;

    /**
     * Webinar updating. Edit webinar item and webinar event.
     *
     * @param webinar webinar structure to update.
     * @return TRUE if request was successful.
     */
    boolean update(Webinar webinar) throws EltilandManagerException;

    /**
     * Webinar applying. Also adds moderator to webinar.
     *
     * @param webinar webinar structure to apply.
     * @param moderator moderator to create
     */
    void apply(Webinar webinar, WebinarUserPayment moderator) throws EltilandManagerException, WebinarException;

    /**
     * @param isFuture   if TRUE - return count of future webinars, otherwise - past webinars.
     * @param isApproved if TRUE - return only approved webinars.
     * @param searchString search string
     * @return Returns total count of webinars item.
     */
    int getWebinarCount(boolean isFuture, boolean isApproved, String searchString);

    /**
     * Get all webinars.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param isFuture    if TRUE - return future webinars, FALSE - past webinars.
     * @param isApproved  if TRUE - return only approved webinars.
     * @param searchString search string
     * @return List of all webinars.
     */
    List<Webinar> getWebinarList(
            int index, Integer count, String sProperty, boolean isAscending, boolean isFuture, boolean isApproved,
            String searchString);

    /**
     * @return Returns total count of webinars, available for current user.
     *         If there is no current user, logged on system, it will return all future webinars, which are opened.
     */
    int getWebinarAvailableCount();

    /**
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of webinars, available for current user.
     *         If there is no current user, logged on system, it will return all future webinars, which are opened.
     */
    List<Webinar> getWebinarAvailableList(int index, Integer count, String sProperty, boolean isAscending);

    /**
     * @return Returns total count of webinars, to which current user was registered.
     */
    int getUserWebinarCount();

    /**
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of webinars, to which current user was registered.
     */
    List<Webinar> getUserWebinarList(int index, Integer count, String sProperty, boolean isAscending);

    /**
     * Close registration on webinar.
     *
     * @param webinar webinar to update.
     */
    void closeRegistration(Webinar webinar) throws EltilandManagerException;

    /**
     * Open registration on webinar.
     *
     * @param webinar webinar to update.
     */
    void openRegistration(Webinar webinar) throws EltilandManagerException;

    /**
     * Updates webinar files.
     *
     * @param webinar webinar to update.
     */
    void updateFiles(Webinar webinar) throws EltilandManagerException;
    /**
     * Updates webinar files.
     *
     * @param webinar webinar to update.
     */
    String getCertificateNumber(Webinar webinar, User user);


    List<Webinar> getWebinars(String userEmail);
}
