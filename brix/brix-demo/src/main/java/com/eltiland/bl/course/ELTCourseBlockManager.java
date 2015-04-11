package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;

import java.util.List;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseBlockManager {

    /**
     * Creates and persists new course block.
     *
     * @param block block to create.
     * @return persisted block.
     */
    ELTCourseBlock create(ELTCourseBlock block) throws CourseException;

    /**
     * Updates course block.
     *
     * @param block block to update.
     * @return updated block.
     */
    ELTCourseBlock update(ELTCourseBlock block) throws CourseException;

    /**
     * Moves block to 1 position up.
     *
     * @param block block to move.
     */
    void moveUp(ELTCourseBlock block) throws CourseException;

    /**
     * Moves block to 1 position down.
     *
     * @param block block to move.
     */
    void moveDown(ELTCourseBlock block) throws CourseException;

    /**
     * Get course blocks. sorted by index.
     *
     * @param course course item.
     * @param status content status (DEMO/FULL)
     * @return list of the course blocks.
     */
    List<ELTCourseBlock> getSortedBlockList(ELTCourse course, ContentStatus status);
}
