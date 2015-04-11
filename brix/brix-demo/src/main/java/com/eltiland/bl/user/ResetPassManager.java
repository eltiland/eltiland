package com.eltiland.bl.user;

import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.ResetCode;
import com.eltiland.model.user.User;

import java.util.Date;

/**
 * Manager of Reset Code Information entities.
 *
 * @author Aleksey Plotnikov
 */
public interface ResetPassManager {
    /**
     * Creates and persists new Reset Code information.
     *
     * @param parent     parent to registration.
     * @param endingDate ending date.
     * @return new reset code information entity.
     */
    ResetCode createResetCode(User parent, Date endingDate);

    /**
     * Removes reset code information.
     *
     * @param resetCode reset code information to remove
     */
    void removeResetCode(ResetCode resetCode);

    /**
     * Get reset code information by it's code
     *
     * @param code reset code
     * @return reset code information.
     */
    ResetCode getResetInfoByCode(String code);
}
