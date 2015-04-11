package com.eltiland.bl;

import com.eltiland.model.webinar.WebinarRecord;

import java.util.List;

/**
 * Interface for managing Webinars.
 *
 * @author Aleksey Plotnikov.
 */
public interface WebinarRecordManager {

    /**
     * Returns count of webinar records.
     *
     * @param isCourse   TRUE - only records, related to webinars, from course.
     *                   FALSE - webinars, not related to course.
     *                   NULL - all records.
     * @return count of records.
     */
    int getCount(Boolean isCourse);

    /**
     * Returns list of webinar records.
     *
     * @param index       the start position of the first result, numbered from 0.
     * @param count       the maximum number of results to retrieve. {@code null} means no limit.
     * @param sProperty   the sorting property name
     * @param isAscending the sorting direction.
     * @param isCourse   TRUE - only records, related to webinars, from course.
     *                   FALSE - webinars, not related to course.
     *                   NULL - all records.
     * @return count of records.
     */
    List<WebinarRecord> getList(int index, Integer count, String sProperty, boolean isAscending, Boolean isCourse);

}
