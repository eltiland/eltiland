package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course listener manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseListenerManager {

    /**
     * Get listener information with fetched course and course author information.
     *
     * @param id if of the entity;
     * @return listener information.
     */
    ELTCourseListener getById(Long id);

    /**
     * Creates and persists new course listener entity.
     *
     * @param listener listener entity.
     * @return listener information item.
     */
    ELTCourseListener create(ELTCourseListener listener) throws CourseException;

    /**
     * Removes course listener entity.
     *
     * @param listener listener entity.
     */
    void delete(ELTCourseListener listener) throws CourseException;

    /**
     * Updates course listener entity.
     *
     * @param listener listener entity.
     */
    ELTCourseListener update(ELTCourseListener listener) throws CourseException;

    /**
     * Get listener information for user and course.
     *
     * @param user   user item.
     * @param course course item.
     * @return listener information item.
     */
    ELTCourseListener getItem(User user, ELTCourse course);

    /**
     * Get listener's list for given course.
     *
     * @param course       course item.
     * @param searchString search String
     * @param index        index of the first item
     * @param count        count of the items
     * @param sProperty    sort property
     * @param isAscending  ascending/descending flag
     * @param isListener   if TRUE - returns only confirmed users, FALSE - only not confirmed, NULL - all listeners.
     * @param onlyParents  if TRUE - returns only first-level listeners.
     * @return listener's list.
     */
    List<ELTCourseListener> getList(ELTCourse course, String searchString, Integer index, Integer count,
                                    String sProperty, boolean isAscending, Boolean isListener, Boolean onlyParents);

    /**
     * Get listener's list for given course.
     *
     * @param course      course item.
     * @param isListener  if TRUE - returns only confirmed users, FALSE - only not confirmed, NULL - all listeners.
     * @param onlyParents if TRUE - returns only first-level listeners.
     * @return listener's list.
     */
    List<ELTCourseListener> getList(ELTCourse course, Boolean isListener, Boolean onlyParents);

    /**
     * Get listener's count for given course.
     *
     * @param course       course item.
     * @param searchString search String
     * @param isListener   if TRUE - returns only confirmed users, FALSE - only not confirmed, NULL - all listeners.
     * @param onlyParents  if TRUE - returns only first-level listeners.
     * @return listener's count.
     */
    Integer getCount(ELTCourse course, String searchString, Boolean isListener, Boolean onlyParents);

    /**
     * Get list of child listeners of the given listener.
     *
     * @param parent      parent listener item.
     * @param index       index of the first item
     * @param count       count of the items
     * @param sProperty   sort property
     * @param isAscending ascending/descending flag
     * @return listener's list.
     */
    List<ELTCourseListener> getChildList(ELTCourseListener parent, Integer index, Integer count,
                                         String sProperty, boolean isAscending);

    /**
     * Check if the user has entire access to the course (means that course is paid).
     * 1 step - check if listener has confirmed status.
     * 2 step - check if listener has not time restrictions for access to the course or this restrictions does not work yet.
     *
     * @param user   user item.
     * @param course course item.
     * @return TRUE if user has access to the course.
     */
    boolean hasAccess(User user, ELTCourse course);

    /**
     * Get list of confirmed listeners.
     *
     * @param index       index of the first item
     * @param count       count of the items
     * @param sProperty   sort property
     * @param isAscending ascending/descending flag
     * @return listener's list.
     */
    List<ELTCourseListener> getConfirmedListeners(Integer index, Integer count, String sProperty, boolean isAscending);


    /**
     *  @return count of the confirmed users.
     */
    Integer getConfirmedListenersCount();


}
