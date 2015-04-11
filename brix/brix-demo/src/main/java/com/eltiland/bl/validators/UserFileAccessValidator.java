package com.eltiland.bl.validators;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.file.UserFileAccess;
import org.springframework.stereotype.Component;

/**
 * User validator.
 */
@Component
public class UserFileAccessValidator {
    public void isValid(UserFileAccess fileAccess) throws UserException {
        if (fileAccess.getFile() == null) {
            throw new UserException(UserException.ERROR_USERFILE_OWNER_EMPTY);
        }
        if (fileAccess.getClient() == null) {
            throw new UserException(UserException.ERROR_USERFILE_FILE_EMPTY);
        }
    }
}
