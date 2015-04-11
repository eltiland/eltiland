package com.eltiland.bl.user;

import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.User;

import java.util.Date;

/**
 * Manager of Confirmation Information entities.
 *
 * @author Aleksey Plotnikov
 */
public interface ConfirmationManager {

    /**
     * Creates and persists new Confirmation information.
     *
     * @param parent     parent to registration.
     * @param endingDate ending date.
     * @return new confirmation information entity.
     */
    Confirmation createConfirmation(User parent, Date endingDate);

    /**
     * Removes confirmation information.
     *
     * @param confirmation confirmation information to remove
     */
    void removeConfirmation(Confirmation confirmation);

    /**
     * Get confirmation information by it's code
     *
     * @param code confirmation code
     * @return confirmation information.
     */
    Confirmation getConfirmationByCode(String code);


    /**
     * Get confirmation information by user
     *
     * @param user user
     * @return confirmation information.
     */
    Confirmation getConfirmationByUser(User user);
}
