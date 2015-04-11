package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.CourseStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseManager {
    /**
     * Creates and persists new course.
     *
     * @param course course to create
     * @return persisted item.
     */
    ELTCourse create(ELTCourse course) throws CourseException;

    /**
     * Removes invoice to course.
     *
     * @param course course to remove.
     */
    void delete(ELTCourse course) throws CourseException;

    /**
     * Updates course item.
     *
     * @param course course to update.
     */
    ELTCourse update(ELTCourse course) throws CourseException;

    /**
     * Publish course and set it's index (if it is AuthorCourse)
     *
     * @param course course to publish.
     * @return published course.
     */
    ELTCourse publish(ELTCourse course) throws CourseException;

    /**
     * UnPublish course and set it's index (if it is AuthorCourse)
     *
     * @param course course to unpublish.
     * @return unpublished course.
     */
    ELTCourse unPublish(ELTCourse course) throws CourseException;

    /**
     * Retrieve course by given name.
     *
     * @param name name to search.
     * @return course with given name.
     */
    ELTCourse getCourseByName(String name);

    /**
     * Check if current user already have invoice for course creating.
     *
     * @return TRUE if current user have invoice.
     */
    boolean hasInvoices();

    /**
     * @param statuses    list of acceptable statuses ( null for all statuses)
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return List of courses with given status.
     */
    List<ELTCourse> getCourseList(List<CourseStatus> statuses, int index, Integer count,
                                  String sProperty, boolean isAscending);

    /**
     * @param statuses list of acceptable statuses ( null for all statuses)
     * @return Count of courses with given status.
     */
    int getCourseListCount(List<CourseStatus> statuses);


    /**
     * Get list of courses in which user has right to controlling.
     *
     * @param user  user to check.
     * @param clazz of the courses (Training of Author). Null - returns general list of all courses.
     * @return list of courses, in which user has right to controlling.
     */
    List<ELTCourse> getAdminCourses(User user, Class<? extends ELTCourse> clazz);

    /**
     * Get list of courses in which user is a listener.
     *
     * @param user  user to check.
     * @param clazz of the courses (Training of Author). Null - returns general list of all courses.
     * @return list of courses, in which user is a listener.
     */
    List<? extends ELTCourse> getListenerCourses(User user, Class<? extends ELTCourse> clazz);


    /*******************************************************
     * Author courses stuff
     *******************************************************/

    /**
     * @param index the start position of the first result, numbered from 0.
     * @param count the maximum number of results to retrieve. {@code null} means no limit.
     * @return published author courses list, sorted by index.
     */
    List<AuthorCourse> getSortedAuthorCourses(int index, int count);

    /*******************************************************
     * Training courses stuff
     *******************************************************/

    /**
     * @return list of the training courses, which are active (finishDate < currentDate), sorted by startDate.
     */
    List<TrainingCourse> getActiveTrainingCourses();

    /**
     * @return count of the published authro courses.
     */
    int getAuthorCoursesCount();

    /**
     * @return list of the past training courses, which are active (finishDate > currentDate), sorted desc by startDate.
     */
    List<TrainingCourse> getPastTrainingCourses();

    /**
     * Returns course with given ID and fetched documents.
     *
     * @param id ID of the course.
     * @return course with fetched docs
     */
    TrainingCourse fetchDocuments(Long id);
}
