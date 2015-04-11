package com.eltiland.bl.test;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;
import com.eltiland.model.course.test.TestVariant;

import java.util.List;

/**
 * Test Variant Manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface TestVariantManager {

    /**
     * Get variants list for given test item.
     *
     * @param item item with variants.
     * @return list of the variants.
     */
    List<TestVariant> getVariantsForItem(TestCourseItem item);

    /**
     * Get variants list for given question.
     *
     * @param question question with variants.
     * @return list of the variants.
     */
    List<TestVariant> getVariantsForQuestion(TestQuestion question);

    /**
     * Move global test variant item.
     *
     * @param variant   variant to move.
     * @param item      item with variants.
     * @param direction if TRUE - moves up, FALSE - move down.
     */
    void moveVariantOfItem(TestVariant variant, TestCourseItem item, boolean direction) throws EltilandManagerException;

    /**
     * Move question test variant item.
     *
     * @param variant   variant to move.
     * @param question  question item with variants.
     * @param direction if TRUE - moves up, FALSE - move down.
     */
    void moveVariantOfQuestion(TestVariant variant, TestQuestion question, boolean direction)
            throws EltilandManagerException;

    /**
     * Removes given variant.
     *
     * @param variant variant to remove.
     */
    void updateNumbers(TestVariant variant) throws EltilandManagerException;

    void deleteEntity(TestVariant variant) throws EltilandManagerException;
}
