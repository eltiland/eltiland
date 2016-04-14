package com.eltiland.bl.course;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;

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
}
