package com.eltiland.bl.validators;

import com.eltiland.bl.PeiManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.PeiException;
import com.eltiland.model.Pei;
import com.eltiland.model.PostalAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User validator.
 */
@Component
public class PeiValidator {
    public void validate(Pei pei) throws PeiException {
        if (pei == null) {
            throw new PeiException(PeiException.EMPTY_ENTITY);
        }

        if (pei.getName() == null) {
            throw new PeiException(PeiException.EMPTY_NAME_ERROR);
        }

        if (pei.getName().isEmpty()) {
            throw new PeiException(PeiException.EMPTY_NAME_ERROR);
        }

        if (pei.getEmail() == null) {
            throw new PeiException(PeiException.EMPTY_EMAIL_ERROR);
        }

        if (pei.getEmail().isEmpty()) {
            throw new PeiException(PeiException.EMPTY_EMAIL_ERROR);
        }

        if (pei.getAvatar() == null) {
            throw new PeiException(PeiException.EMPTY_AVATAR_ERROR);
        }

        if (pei.getAvatar().getSize() == 0) {
            throw new PeiException(PeiException.EMPTY_AVATAR_ERROR);
        }
    }
}
