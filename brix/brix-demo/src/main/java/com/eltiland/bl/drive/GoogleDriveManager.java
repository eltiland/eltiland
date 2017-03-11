package com.eltiland.bl.drive;

import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.google.ELTGoogleFile;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;

import java.io.InputStream;
import java.util.List;

/**
 * Google Drive API manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface GoogleDriveManager {

    /**
     * Insert IO file.
     *
     * @param file File to insert.
     * @return google id of inserted file. Return NULL if fails.
     */
    GoogleDriveFile insertFile(ELTGoogleFile file) throws GoogleDriveException;

    /**
     * Insert IO file to specified folder.
     *
     * @param file   File to insert.
     * @param folder Folder-parent of inserted file.
     * @return google id of inserted file. Return NULL if fails.
     */
    GoogleDriveFile insertFile(ELTGoogleFile file, GoogleDriveFile folder) throws GoogleDriveException;

    /**
     * Insert new folder.
     *
     * @param name Folder name to insert.
     * @return google id of inserted folder. Return NULL if fails.
     */
    GoogleDriveFile insertFolder(String name) throws GoogleDriveException;

    /**
     * Deletes google file.
     *
     * @param file File to delete.
     */
    void deleteFile(GoogleDriveFile file) throws GoogleDriveException;

    /**
     * Insert new empty document to Google Drive.
     *
     * @param name Name of the file to insert.
     * @param type Google Document type.
     * @return inserted google file.
     */
    GoogleDriveFile createEmptyDoc(String name, GoogleDriveFile.TYPE type) throws GoogleDriveException;

    /**
     * Download file from Google Drive.
     *
     * @param file File to download.
     * @return input stream of downloaded file.
     */
    InputStream downloadFile(GoogleDriveFile file) throws GoogleDriveException;

    /**
     * Download file from Google Drive as PDF.
     *
     * @param file File to download.
     * @return input stream of downloaded file.
     */
    InputStream downloadFileAsPDF(GoogleDriveFile file) throws GoogleDriveException;

    /**
     * Saves given google file to database.
     *
     * @param file File to Save
     */
    void cacheFile(GoogleDriveFile file) throws GoogleDriveException;

    /**
     * Return all documents, which can be cached.
     *
     */
    List<GoogleDriveFile> getFilesToCache();

    /**
     * Add new permission to file.
     *
     * @param file       File to insert.
     * @param permission Permission structure to add.
     */
    void insertPermission(GoogleDriveFile file, ELTGooglePermissions permission) throws GoogleDriveException;

    /**
     * Publish Google Drive document.
     *
     * @param file File to publish.
     */
    void publishDocument(GoogleDriveFile file) throws GoogleDriveException;
}
