package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.CourseAdmin;

/**
 * Course user registration data entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseAdminManager {

    /**
     * Create new course admin entity.
     *
     * @param admin course admin
     * @return persisted and created
     */
    CourseAdmin create(CourseAdmin admin) throws CourseException;
}
