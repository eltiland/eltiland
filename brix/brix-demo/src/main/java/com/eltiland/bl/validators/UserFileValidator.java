package com.eltiland.bl.validators;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.file.UserFile;
import org.springframework.stereotype.Component;

/**
 * UserFile entity validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class UserFileValidator {

    public void isValid(UserFile userFile) throws UserException {
        if (userFile.getOwner() == null) {
            throw new UserException(UserException.ERROR_USERFILE_OWNER_EMPTY);
        }
        if (userFile.getFile() == null) {
            throw new UserException(UserException.ERROR_USERFILE_FILE_EMPTY);
        }
        if (userFile.getUploadDate() == null) {
            throw new UserException(UserException.ERROR_USERFILE_UPLOAD_DATE_EMPTY);
        }
    }
}
