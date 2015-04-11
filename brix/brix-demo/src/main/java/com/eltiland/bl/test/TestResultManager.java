package com.eltiland.bl.test;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestResult;

import java.util.List;

/**
 * Course tests - manager for result entities.
 *
 * @author Aleksey Plotnikov.
 */
public interface TestResultManager {

    /**
     * Get corresponding to value test result.
     *
     * @param testItem test Item.
     * @param value    value.
     */
    TestResult getResult(TestCourseItem testItem, int value);

    /**
     * Get corresponding to value test result (sub-element).
     *
     * @param question test question.
     * @param value    value.
     */
    TestResult getResult(TestQuestion question, int value);

    /**
     * Get results list for given test item.
     *
     * @param item item with variants.
     * @return list of the variants.
     */
    List<TestResult> getResultsForItem(TestCourseItem item);

    /**
     * Deletes given test result entity.
     *
     * @param testResult test result to delete.
     */
    void deleteTestResult(TestResult testResult) throws EltilandManagerException;

    /**
     * Reset all flags of global results for given item except given testResult.
     *
     * @param item       test course item
     * @param testResult except result.
     */
    void updateRightFlag(TestCourseItem item, TestResult testResult) throws EltilandManagerException;
}
