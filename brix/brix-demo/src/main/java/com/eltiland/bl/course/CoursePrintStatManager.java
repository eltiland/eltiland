package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;

import java.util.List;

/**
 * Course item print statistics manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CoursePrintStatManager {

    /**
     * Create new course item statistics.
     *
     * @param listener listener.
     * @param item     course item.
     * @return persisted item.
     */
    CourseItemPrintStat create(ELTCourseListener listener, ELTGoogleCourseItem item) throws CourseException;

    /**
     * Update course item statistics.
     *
     * @param stat statistics item.
     * @return persisted item.
     */
    CourseItemPrintStat update(CourseItemPrintStat stat) throws CourseException;

    /**
     * @param listener listener.
     * @param item     course item.
     * @return statistics for given data.
     */
    CourseItemPrintStat getItem(ELTCourseListener listener, ELTGoogleCourseItem item);

    /**
     * Get list of print statictica for given course.
     *
     * @param course      course item.
     * @param index       start index.
     * @param count       count of items.
     * @param sProperty   sort property
     * @param isAscending asc/desc param.
     * @return list of items.
     */
    List<CourseItemPrintStat> getItems(ELTCourse course, Integer index,
                                       Integer count, String sProperty, boolean isAscending);

    /**
     * Get count of print statictica for given course.
     *
     * @param course      course item.
     * @return count of items.
     */
    Integer getCount(ELTCourse course);
}
