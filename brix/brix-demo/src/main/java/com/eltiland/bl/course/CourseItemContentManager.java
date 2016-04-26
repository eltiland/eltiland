package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.google.CourseItemContent;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;

import java.util.List;

/**
 * Course item content manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseItemContentManager {

    /**
     * Create new course item
     *
     * @param item to create.
     * @return persisted item.
     */
    CourseItemContent create(CourseItemContent item) throws CourseException;

    /**
     * Updates course item.
     *
     * @param item to to update.
     * @return persisted item.
     */
    CourseItemContent update(CourseItemContent item) throws CourseException;
}
