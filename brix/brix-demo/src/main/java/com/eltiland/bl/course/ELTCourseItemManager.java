package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;

import java.util.List;

/**
 * Course item manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseItemManager {

    /**
     * Create new course item
     *
     * @param item to create.
     * @return persisted item.
     */
    ELTCourseItem create(ELTCourseItem item) throws CourseException;

    /**
     * Updates course item.
     *
     * @param item to to update.
     * @return persisted item.
     */
    ELTCourseItem update(ELTCourseItem item) throws CourseException;

    /**
     * Get list of the items of the block, sorted by index.
     *
     * @param block parent block.
     * @return list of the items of the block, sorted by items.
     */
    List<ELTCourseItem> getItems(ELTCourseBlock block);

    /**
     * Get list of the items of the group, sorted by index.
     *
     * @param group parent group
     * @return list of the items of the group, sorted by items.
     */
    List<ELTCourseItem> getItems(ELTGroupCourseItem group);

    /**
     * Moves block to 1 position up.
     *
     * @param item item to move.
     */
    void moveUp(ELTCourseItem item) throws CourseException;

    /**
     * Moves block to 1 position down.
     *
     * @param item item to move.
     */
    void moveDown(ELTCourseItem item) throws CourseException;

    /**
     * @return course for given item
     */
    ELTCourse getCourse(ELTCourseItem item);
}
