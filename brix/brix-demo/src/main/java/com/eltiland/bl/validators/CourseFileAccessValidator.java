package com.eltiland.bl.validators;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.file.CourseFileAccess;
import org.springframework.stereotype.Component;

/**
 * User validator.
 */
@Component
public class CourseFileAccessValidator {
    public void isValid(CourseFileAccess access) throws UserException {
        if (access.getFile() == null) {
            throw new UserException(UserException.ERROR_USERFILE_FILE_EMPTY);
        }
        if (access.getCourse() == null) {
            throw new UserException(UserException.ERROR_USERFILE_COURSE_EMPTY);
        }
    }
}
