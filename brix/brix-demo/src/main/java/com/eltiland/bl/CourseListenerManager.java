package com.eltiland.bl;

import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseListenerManager {

    /**
     * Get listener by its ID.
     *
     * @param id entity id.
     */
    CourseListener getListenerById(Long id);

    /**
     * Returns listener information for given user for given course.
     * if NULL - user does not send invoice to register into a course.
     *
     * @param course course entity.
     * @param user   user entity.
     * @return listener information.
     */
    CourseListener getListener(Course course, User user);

    /**
     * Returns listener object of user, who registered given user into a course.
     * if NULL - user was not registered by other user.
     *
     * @param course course entity.
     * @param user   user entity.
     * @return listener information.
     */
    CourseListener getCurator(Course course, User user);

    /**
     * Return listeners count for given session.
     *
     * @param session      course session.
     * @param searchString search string
     * @param isConfirmed  kind of listeners:
     *                     TRUE - only confirmed,
     *                     FALSE - only not confirmed,
     *                     NULL - all listeners.
     * @return get listeners count.
     */
    int getListenersCount(CourseSession session, String searchString, Boolean isConfirmed);

    /**
     * Return listeners for given session.
     *
     * @param session      course session.
     * @param searchString search string.
     * @param first        first item index.
     * @param count        items count.
     * @param sProperty    sort property.
     * @param isAsc        ascending flag.
     * @param isConfirmed  kind of listeners:
     *                     TRUE - only confirmed,
     *                     FALSE - only not confirmed,
     *                     NULL - all listeners.
     * @return get listeners count.
     */
    List<CourseListener> getListeners(CourseSession session, String searchString,
                                      int first, int count, String sProperty, boolean isAsc, Boolean isConfirmed);

    /**
     * Return listeners for given session.
     *
     * @param session     course session.
     * @param isConfirmed kind of listeners:
     *                    TRUE - only confirmed,
     *                    FALSE - only not confirmed,
     *                    NULL - all listeners.
     * @return get listeners count.
     */
    List<CourseListener> getListeners(CourseSession session, Boolean isConfirmed);
}
