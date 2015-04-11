package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.CourseVideoItem;
import com.eltiland.model.course.VideoCourseItem;

import java.util.List;

/**
 * Course item manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseVideoItemManager {

    /**
     * Creates course video item.
     *
     * @param item item to create.
     * @return persisted item.
     */
    CourseVideoItem createVideoItem(CourseVideoItem item) throws EltilandManagerException;

    /**
     * Removes video item.
     *
     * @param item item to delete.
     */
    void deleteVideoItem(CourseVideoItem item) throws EltilandManagerException;

    /**
     * Get course video list for given item.
     *
     * @param item      video course item.
     * @param index     start index.
     * @param count     max results.
     * @param sProperty sortProperty.
     * @param isAsc     asc/desc parameter.
     * @return item list.
     */
    List<CourseVideoItem> getItemList(VideoCourseItem item, int index, int count, String sProperty, boolean isAsc);

    /**
     * Get course video list count with search params.
     *
     * @param item video course item.
     * @return items count
     */
    int getItemCount(VideoCourseItem item);

    /**
     * Move item up to one element.
     */
    void moveUp(CourseVideoItem item) throws EltilandManagerException;

    /**
     * Move item down to one element.
     */
    void moveDown(CourseVideoItem item) throws EltilandManagerException;
}
