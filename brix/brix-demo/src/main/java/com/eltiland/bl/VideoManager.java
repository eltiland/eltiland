package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.Video;

/**
 * Manager for Video entity.
 *
 * @author Aleksey Plotnikov
 */
public interface VideoManager {
    /**
     * Creates and persists new Video.
     *
     * @param video video to create.
     * @return new created video..
     */
    Video createVideo(Video video) throws EltilandManagerException;

    /**
     * Updates Video.
     *
     * @param video video to update.
     * @return new updated video..
     */
    Video updateVideo(Video video) throws EltilandManagerException;

    /**
     * Removes Video.
     *
     * @param video video to delete.
     */
    void deleteVideo(Video video) throws EltilandManagerException;

    /**
     * Filling video entity by it's duration and date of adding (if not present)
     *
     * @param video video to fill.
     */
    void fillAdditionalInfo(Video video) throws EltilandManagerException;

    /**
     * Filling video view count on Youtube.
     *
     * @param video video to fill.
     */
    void fillViewCount(Video video) throws EltilandManagerException;
}
