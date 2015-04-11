package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseUserData;

/**
 * Manager of the course registartion variables.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseUserDataManager {
    /**
     * Creating standart values of registration variables for the course.
     *
     * @param course course item.
     */
    void createStandart(Course course) throws EltilandManagerException;

    /**
     * Get course user data entity.
     *
     * @param course course item.
     * @param type   type of data.
     */
    CourseUserData getCourseData(Course course, CourseUserData.Type type);
}
