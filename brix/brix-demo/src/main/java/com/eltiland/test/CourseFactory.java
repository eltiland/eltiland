package com.eltiland.test;

import com.eltiland.model.course.Course;
import com.eltiland.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * Created with IntelliJ IDEA.
 * User: Klimenko
 * Date: 23.07.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class CourseFactory {
    private static final String TESTCOURSENAME = "testCourseName";

    public static Course createCourse() {
        return createCourse(TESTCOURSENAME);
    }

    private static Course createCourse(String courseName) {
        Course course = new Course();
        course.setName(courseName);
        course.setCreationDate(DateUtils.getCurrentDate());
        return course;
    }
}
