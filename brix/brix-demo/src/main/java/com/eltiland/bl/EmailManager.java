package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.subscribe.Email;

import java.util.List;

/**
 * Email entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface EmailManager {

    /**
     * Delete Email record
     *
     * @param email
     * @throws EmailException
     */
    void deleteEmail(Email email) throws EmailException;

    /**
     * Create and persists Email entity.
     *
     * @param email entity to create.
     * @return newly created and peristed entity.
     */
    Email createEmail(Email email) throws EmailException;

    /**
     * Updates email entity.
     *
     * @param email entity to update.
     */
    Email updateEmail(Email email) throws EmailException;

    /**
     * Get email (sended or not) count.
     *
     * @param status if TRUE - get sended email count.
     * @return email count
     */
    int getEmailCount(boolean status);

    /**
     * Get email's list.
     *
     * @param status      status of mail.
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of emails with given status.
     */
    List<Email> getEmailList(boolean status, int index, Integer count,
                             String sProperty, boolean isAscending);

}
