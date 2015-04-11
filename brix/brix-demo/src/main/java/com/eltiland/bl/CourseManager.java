package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseManager {

    /**
     * Creates and persists new course entity.
     * New course will be NOT applied and NOT published.
     *
     * @param course course to create.
     * @return created course.
     */
    Course createCourse(Course course) throws EltilandManagerException;

    /**
     * Creates and persists new course entity and attach new course session to it.
     * New course will be NOT applied and NOT published.
     *
     * @param course course to create.
     * @return created course.
     */
    Course createTrainingCourse(Course course, CourseSession session) throws EltilandManagerException;

    /**
     * Deletes entity of course.
     *
     * @param course course to delete.
     */
    void deleteCourse(Course course) throws EltilandManagerException;

    /**
     * Updates entity of course.
     *
     * @param course course to delete.
     */
    void updateCourse(Course course) throws EltilandManagerException;

    /**
     * Check if user has request to create a course.
     *
     * @param user owner of the course.
     * @return TRUE if user has unprocessed request for creating course.
     */
    boolean hasCourseInvoice(User user);

    /**
     * @param status      status of course.
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of courses with given status.
     */
    List<Course> getCourseList(boolean status, int index, Integer count,
                               String sProperty, boolean isAscending);

    /**
     * @param status status of course.
     * @return Count of courses with given status.
     */
    int getCourseListCount(boolean status);

    /**
     * @param user       user-author of courses
     * @param index      the start position of the first result, numbered from 0.
     * @param count      the maximum number of results to retrieve. {@code null} means no limit.
     * @param isTraining if TRUE - returns only training courses.
     *                   if FALSE - returns only author's courses
     *                   if NULL - returns all courses.
     * @return List of approved courses for given user.
     */
    List<Course> getApprovedCourseList(User user, int index, Integer count, Boolean isTraining);

    /**
     * @param index    the start position of the first result, numbered from 0.
     * @param count    the maximum number of results to retrieve. {@code null} means no limit.
     * @param training type of courses (true - training, false - author, null - all)
     * @return List of approved and published courses.
     */
    List<Course> getPublishedCourseList(int index, Integer count, Boolean training);

    /**
     * @param id id of the course.
     * @return course by it's id.
     */
    Course getCourseById(Long id);

    /**
     * Adding new listener, which is paid the course.
     *
     * @param payment course payment.
     */
    void addPaidListener(CoursePayment payment) throws EltilandManagerException, UserException;

    /**
     * Returns list of courses, in which user is a listener.
     *
     * @param user       user-listener object.
     * @param isTraining if TRUE - returns only training courses.
     *                   if FALSE - returns only author's courses
     *                   if NULL - returns all courses.
     * @return course list.
     */
    List<Course> getUserCourses(User user, Boolean isTraining);
}
