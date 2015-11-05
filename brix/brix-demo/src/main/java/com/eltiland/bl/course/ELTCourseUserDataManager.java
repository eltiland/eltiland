package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseUserData;
import com.eltiland.model.course2.listeners.UserDataType;

/**
 * Course user registration data entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseUserDataManager {

    /**
     * Create new user data entity.
     *
     * @param data data to create.
     * @return persisted and created
     */
    ELTCourseUserData create(ELTCourseUserData data) throws CourseException;

    /**
     * Updates user data entity.
     *
     * @param data data to update.
     * @return updated data.
     */
    ELTCourseUserData update(ELTCourseUserData data) throws CourseException;

    /**
     * Removes all user data, related to specified course.
     *
     * @param course course-owner of the data.
     */
     void  deleteForCourse(ELTCourse course) throws CourseException;

    /**
     * Creating standart values of registration variables for the course.
     *
     * @param course course item.
     */
    void createStandart(ELTCourse course) throws CourseException;

    /**
     * Get user data for given course and type.
     *
     * @param course course item.
     * @param type   type of the registration item.
     * @return user data item
     */
    ELTCourseUserData get(ELTCourse course, UserDataType type);
}
