package com.eltiland.bl.drive;

import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.google.Content;

/**
 * Google Content manager
 *
 * @author Aleksey Plotnikov.
 */
public interface ContentManager {
    /**
     * Create and persist new Content entity.
     *
     * @param content Content to persist.
     * @return persisted content.
     */
    Content create(Content content) throws GoogleDriveException;

    /**
     * Updates Content entity.
     *
     * @param content Content to update.
     */
    void update(Content content) throws GoogleDriveException;
}
