package com.eltiland.bl.course.video;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoItem;

import java.util.List;

/**
 * Video item manager for video course item.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTVideoItemManager {
    /**
     * Creates and persists video tem.
     *
     * @param item item to create.
     * @return persisted item.
     */
    ELTVideoItem create(ELTVideoItem item) throws CourseException;

    /**
     * Updates video tem.
     *
     * @param item item to update.
     * @return updated item.
     */
    ELTVideoItem update(ELTVideoItem item) throws CourseException;

    /**
     * Deletes video tem.
     *
     * @param item item to delete.
     */
    void delete(ELTVideoItem item) throws CourseException;

    /**
     * Move video item up to one position.
     *
     * @param item item to move.
     */
    void moveUp(ELTVideoItem item) throws CourseException;

    /**
     * Move video item down to one position.
     *
     * @param item item to move.
     */
    void moveDown(ELTVideoItem item) throws CourseException;

    /**
     * Return video list for course item.
     *
     * @param item        course item.
     * @param index       start index.
     * @param count       count of the item to select.
     * @param sProperty   sort property.
     * @param isAscending ascending/descending sorting.
     * @return video list.
     */
    List<ELTVideoItem> getItems(ELTVideoCourseItem item, int index, int count, String sProperty, boolean isAscending);

    /**
     * Get course video list count.
     *
     * @param item video course item.
     * @return items count
     */
    int getCount(ELTVideoCourseItem item);
}
