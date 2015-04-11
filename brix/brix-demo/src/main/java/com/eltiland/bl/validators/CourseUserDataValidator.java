package com.eltiland.bl.validators;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.listeners.ELTCourseUserData;
import org.springframework.stereotype.Component;

/**
 * Course user data validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseUserDataValidator {

    public void isCourseUserDataValid(ELTCourseUserData userData) throws CourseException {
        if (userData.getCourse() == null) {
            throw new CourseException(CourseException.ERROR_USERDATA_EMPTY_COURSE);
        }
        if (userData.getType() == null) {
            throw new CourseException(CourseException.ERROR_USERDATA_EMPTY_TYPE);
        }
        if (userData.getStatus() == null) {
            throw new CourseException(CourseException.ERROR_USERDATA_EMPTY_STATUS);
        }
        if (userData.getCaption().length() > 128) {
            throw new CourseException(CourseException.ERROR_USERDATA_CAPTION_TOO_LONG);
        }
    }
}
