package com.eltiland.bl.drive;

import com.eltiland.model.google.GoogleDriveFile;

/**
 * Google Drive File entity manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface GoogleDriveFileManager {

    /**
     * Returns google drive file by its google id.
     *
     * @param googleId google id of file.
     * @return google drive file.
     */
    GoogleDriveFile getFileByGoogleId(String googleId);
}
