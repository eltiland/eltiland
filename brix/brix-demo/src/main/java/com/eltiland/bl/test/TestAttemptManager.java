package com.eltiland.bl.test;

import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.UserTestAttempt;

import java.util.List;

/**
 * Test attempt manager interface
 *
 * @author Aleksey Plotnikov
 */
public interface TestAttemptManager {

    /**
     * @param item test item.
     * @return user attempt entity for current user for given item.
     */
    UserTestAttempt getAttempt(TestCourseItem item);

    /**
     * @param item test item.
     * @return true if current user has attempts record.
     */
    boolean hasAttemptRecord(TestCourseItem item);

    /**
     * @return count of users, who successfull complete the test.
     * @param searchString search string
     */
    int getSuccessCount(String searchString);

    /**
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param searchString search string
     * @return List of users, who successfull complete the test.
     */
    List<UserTestAttempt> getSuccessList(
            int index, Integer count, String sProperty, boolean isAscending, String searchString);

    /**
     * @return count of users, who currently processed a test.
     * @param searchString search string
     */
    int getProcessCount(String searchString);

    /**
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param searchString search string
     * @return List of users, who currently processed a test.
     */
    List<UserTestAttempt> getProcessList(
            int index, Integer count, String sProperty, boolean isAscending, String searchString);

    /**
     * @return count of users, who reached limit of attempts
     * @param searchString search string
     */
    int getLimitCount(String searchString);

    /**
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param searchString search string
     * @return List of users, who reached limit of attempts.
     */
    List<UserTestAttempt> getLimitList(
            int index, Integer count, String sProperty, boolean isAscending, String searchString);
}
