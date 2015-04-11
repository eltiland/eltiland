package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Block access items manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTCourseBlockAccessManager {

    /**
     * Creates and persists block access information item.
     *
     * @param blockAccess item to persist.
     * @return persisted item.
     */
    ELTCourseBlockAccess create(ELTCourseBlockAccess blockAccess) throws CourseException;

    /**
     * Updates block access information item.
     *
     * @param blockAccess item to update.
     * @return persisted item.
     */
    ELTCourseBlockAccess update(ELTCourseBlockAccess blockAccess) throws CourseException;

    /**
     * Find information about access to the given block for the given user.
     *
     * @param user  user.
     * @param block block to test.
     * @return access information, null if default.
     */
    ELTCourseBlockAccess find(User user, ELTCourseBlock block);

    /**
     * Get information about access to the given course block.
     *
     * @param block block to test.
     * @return access information list
     */
    List<ELTCourseBlockAccess> getAccessInformation(ELTCourseBlock block);
}
