package com.eltiland.bl.test;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;

import java.util.List;

/**
 * Test Question Manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface TestQuestionManager {

    /**
     * Updates test question item.
     *
     * @param testQuestion test question to update.
     * @return updated test question.
     */
    TestQuestion updateTestQuestion(TestQuestion testQuestion) throws EltilandManagerException;

    /**
     * Get list of top level questions and question-sections, sorted by it's number.
     *
     * @param item item-owner of questions.
     * @return list of questions.
     */
    List<TestQuestion> getSortedTopLevelList(TestCourseItem item);

    /**
     * Get list of childs of given item , sorted by position.
     *
     * @param parent parent item.
     * @return list of questions-childs.
     */
    List<TestQuestion> getSortedList(TestQuestion parent);

    /**
     * Get count of top level questions of test item.
     *
     * @param item item-owner of questions.
     * @return count of top level questions.
     */
    int getTopLevelItemCount(TestCourseItem item);

    /**
     * Removes test question item.
     *
     * @param item test question to delete.
     */
    void deleteTestQuestion(TestQuestion item) throws EltilandManagerException;

    /**
     * Moves test question item up to one position.
     *
     * @param item test question to move.
     */
    void moveTestQuestionUp(TestQuestion item) throws EltilandManagerException;

    /**
     * Moves test question item up to down position.
     *
     * @param item test question to move.
     */
    void moveTestQuestionDown(TestQuestion item) throws EltilandManagerException;

    /**
     * Return question, which is child of given parent by it's position.
     *
     * @param position test question position.
     * @param item     item-owner
     * @param parent   parent question.
     */
    TestQuestion getQuestionByPosition(int position, TestCourseItem item, TestQuestion parent);
}
