package com.eltiland.bl.user;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;

import java.util.List;

/**
 * User Manager, containing methods related to user.
 * User: LEXAUX
 * Date: 7/24/12
 * Time: 12:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserManager {

    /**
     * Creates and persists new User.
     *
     * @param toCreate user to create.
     * @return created and persisted user.
     */
    User createUser(User toCreate) throws UserException, EltilandManagerException;

    /**
     * Updates user.
     *
     * @param toUpdate user to update.
     * @return updated user.
     */
    User updateUser(User toUpdate) throws UserException;

    /**
     * Delete user from DB.
     *
     * @param toDelete user to delete
     * @throws EltilandManagerException if item cannot be deleted
     */
    void deleteUser(User toDelete) throws EltilandManagerException;

    /**
     * Get user by it's id.
     *
     * @param id user's id.
     * @return user by it's id.
     */
    User getUserById(Long id);

    /**
     * Get user by it's email.
     *
     * @param email email of the user.
     * @return user by it's email.
     */
    User getUserByEmail(String email);


    /**
     * Get user by it's email and class
     *
     * @param email email of the user.
     * @param clazz class of the user entity.
     * @return user.
     */
    User getUserByClass(String email, Class<? extends User> clazz);

    /**
     * Initialize base information of simple user.
     *
     * @param user user to initialize.
     * @return initialized user.
     */
    User initializeSimpleUserInfo(User user);

    /**
     * Initialize avatar information of the user.
     *
     * @param user user to initialize.
     * @return initialized user.
     */
    User initializeAvatarInfo(User user);

    /**
     * Get list of the users, which satisfy given condition.
     *
     * @param index        the start position of the first result, numbered from 0
     * @param count        the maximum number of results to retrieve
     * @param searchString search string.
     * @param sortProperty sorting property.
     * @param isAscending  sorting direction.
     * @return list of the Teachers
     */
    List<User> getUserSearchList(int index, int count, String searchString,
                                 String sortProperty, boolean isAscending);

    /**
     * Get count of all users with specified search string.
     *
     * @param searchString search string.
     * @return Count of record item
     */
    Integer getUserSearchCount(String searchString);

    /**
     * Get listeners of the course.
     *
     * @param course      given course.
     * @param first       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return course listeners list.
     */
    List<User> getCourseListeners(Course course, int first, int count, String sProperty, boolean isAscending);

    /**
     * Return count listeners of the course, who already paid all course.
     */
    int getCoursePaidListenersCount(Course course);

    /**
     * Get listeners of the course, who already paid all course.
     *
     * @param course      given course.
     * @param first       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @return course listeners list.
     */
    List<User> getCoursePaidListeners(Course course, int first, int count, String sProperty, boolean isAscending);

    /**
     * Returns count of the users, which are available to add to the webinar.
     *
     * @param searchString search string.
     * @param webinar      webinar
     * @return count of the available users.
     */
    int getUserCountAvailableToWebinar(String searchString, Webinar webinar);

    /**
     * Get users, available to add to the webinar.
     *
     * @param searchString search string.
     * @param first        the start position of the first result, numbered from 0.
     * @param count        the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty    the sorting property name
     * @param isAscending  the sorting direction.
     * @param webinar      webinar
     * @return users list.
     */
    List<User> getUserListAvailableToWebinar(
            String searchString, int first, int count, String sProperty, boolean isAscending, Webinar webinar);

    /**
     * Returns count of the users, each of them was webinar listener or bought the record.
     *
     * @param searchString search string.
     * @param webinars     webinar list.
     * @return count of the users.
     */
    int getUserCountOnWebinars(String searchString, List<Webinar> webinars);

    /**
     * Get users list, each of them was webinar listener or bought the record.
     *
     * @param searchString search string.
     * @param first        the start position of the first result, numbered from 0.
     * @param count        the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty    the sorting property name
     * @param isAsc        the sorting direction.
     * @param webinars     webinars list.
     * @return users list.
     */
    List<User> getUserListOnWebinars(
            String searchString, int first, int count, String sProperty, boolean isAsc, List<Webinar> webinars);

    /**
     * Returns count of confirmed users.
     *
     * @param isConfirmed if TRUE returns only confirmed users, FALSE - not confirmed.
     */
    int getConfirmedUsersCount(boolean isConfirmed);

    /**
     * Returns confirmed users which are not subscribers.
     */
    List<User> getUsersNotSubscribers();

    /**
     * Return count of users, available for adding to the course session.
     *
     * @param session      course session.
     * @param searchString search string.
     */
    int getUsersAvailiableForSessionCount(CourseSession session, String searchString) throws EltilandManagerException;

    /**
     * @param session      course session.
     * @param searchString search string.
     * @param first        the start position of the first result, numbered from 0.
     * @param count        the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty    the sorting property name
     * @param isAsc        the sorting direction.
     * @return list of users, available for joining to the course session.
     */
    List<User> getUsersAvailiableForSession(
            CourseSession session, String searchString, int first, int count, String sProperty, boolean isAsc) throws EltilandManagerException;

    /**
     * Return count of users, which was added to course session by given listener.
     *
     * @param listener course listener entity.
     */
    int getInvitedUsersCount(CourseListener listener);

    /**
     * @param listener  course listener entity.
     * @param first     the start position of the first result, numbered from 0.
     * @param count     the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty the sorting property name
     * @param isAsc     the sorting direction.
     * @return list of users, which was added to course session by given listener.
     */
    List<User> getInvitedUsers(CourseListener listener, int first, int count, String sProperty, boolean isAsc);

    /**
     * @param course    course entity.
     * @param first     the start position of the first result, numbered from 0.
     * @param count     the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty the sorting property name
     * @param isAsc     the sorting direction.
     * @return list of users, which has administration rights for the given course.
     */
    List<User> getCourseAdmins(ELTCourse course, int first, int count, String sProperty, boolean isAsc);
}
