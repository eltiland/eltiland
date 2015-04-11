package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestQuestion;

import java.util.List;

/**
 * Course item manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseItemManager {

    /**
     * Creates and persists new course item.
     *
     * @param item course item.
     * @param kind content kind.
     * @return persisted new item.
     */
    CourseItem createCourseItem(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException;

    /**
     * Deletes course item.
     *
     * @param item   course item to delete.
     * @param kind   content kind
     * @param isMove if TRUE - elements of the same heirarchy level of deleting element will be moved up.
     */
    void deleteCourseItem(CourseItem item, Course.CONTENT_KIND kind, boolean isMove) throws EltilandManagerException;

    /**
     * Updates course item.
     *
     * @param item course item.
     */
    void updateCourseItem(CourseItem item) throws EltilandManagerException;

    /**
     * Get course item by it's ID.
     *
     * @param id course item id.
     * @return course item.
     */
    CourseItem getCourseItemById(Long id);

    /**
     * Move course item up to one position.
     *
     * @param item course item
     * @param kind content kind
     */
    void moveCourseItemUp(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException;

    /**
     * Move course item down to one position.
     *
     * @param item course item
     * @param kind content kind
     */
    void moveCourseItemDown(CourseItem item, Course.CONTENT_KIND kind) throws EltilandManagerException;

    /**
     * Get top level course item by it's index.
     *
     * @param course course item.
     * @param kind   content kind (DEMO or FULL).
     * @param index  index of the item.
     * @return course item.
     */
    CourseItem getTopLevelElementByIndex(Course course, Course.CONTENT_KIND kind, int index);

    /**
     * Get test item with fetched results.
     *
     * @param item item to initialize.
     * @return test course item.
     */
    TestCourseItem initializeTestItem(TestCourseItem item);

    /**
     * @param item           course item - parent.
     * @param exceptQuestion this question (if not null) will be removed from resulting list.
     * @return list of top level questions of given test item.
     */
    List<TestQuestion> getTopLevelQuestions(TestCourseItem item, TestQuestion exceptQuestion);

    /**
     * @param item           course item - parent.
     * @param exceptQuestion this question (if not null) will be removed from resulting list.
     * @param isSortByNumber if TRUE - list will be sorted by order.
     * @return list of top level questions of given test item.
     */
    List<TestQuestion> getTopLevelQuestions(TestCourseItem item, TestQuestion exceptQuestion, boolean isSortByNumber);

    /**
     * @return count of items in the logical level, corresponding to given course item.
     */
    int getItemsCountInLevel(CourseItem item);

    /**
     * @return course item by ID of parent item and it's name.
     */
    CourseItem getItemByParentIdAndName(Long id, String name);
}
