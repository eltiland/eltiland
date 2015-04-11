package com.eltiland.bl.validators;

import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.CourseAdmin;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Course admin validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseAdminValidator {

    @Autowired
    private GenericManager genericManager;

    public void isCourseAdminValid(CourseAdmin admin) throws CourseException {
        if (admin.getAdmin() == null) {
            throw new CourseException(CourseException.ERROR_COURSEADMIN_USER_EMPTY);
        }
        if (admin.getCourse() == null) {
            throw new CourseException(CourseException.ERROR_COURSEADMIN_COURSE_EMPTY);
        }
        ELTCourse course = admin.getCourse();
        genericManager.initialize(course, course.getAuthor());
        if (admin.getAdmin().getId().equals(course.getAuthor().getId())) {
            throw new CourseException(CourseException.ERROR_COURSEADMIN_USER_AUTHOR);
        }
        genericManager.initialize(course, course.getAdmins());
        for (User user : course.getAdmins()) {
            if (user.getId().equals(admin.getAdmin().getId())) {
                throw new CourseException(String.format(CourseException.ERROR_COURSEADMIN_USER_EXISTS, user.getName()));
            }
        }
    }
}
