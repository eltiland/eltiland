package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.MimeSubType;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.file.File;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.util.List;

/**
 * File Manager, containing methods related to {@link com.eltiland.model.file.File}.
 */
public interface FileManager {
    /**
     * Get standard icon file.
     *
     * @param standardIcon icon file identification
     * @return file instance
     */
    File getStandardIconFile(UrlUtils.StandardIcons standardIcon);

    /**
     * Get standard mime type icon file.
     *
     * @return file instance
     */
    File getStandardIconFileByType(String mimeType);

    /**
     * Load data about mime type from mime type dictionary.
     *
     * @param mimeType string mime representation, like text\plain
     * @return if mime type dictionary contains required mime, then return mime type info: icon name, localization resource key
     */
    MimeSubType getTypeInfo(String mimeType);

    /**
     * Load mime sub types that supported by {@link com.eltiland.ui.common.components.avatar.CreateAvatarPanel}
     *
     * @return list of file types that can be used in avatar creation
     */
    List<MimeSubType> getSupportedForAvatarSubTypes();

    /**
     * Load data about mime types from mime type dictionary.
     *
     * @param mimeTypes list of string mime representation, like text\plain
     * @return if mime type dictionary contains required mime, then return mime type info: icon name, localization resource key
     */
    List<MimeSubType> getTypeInfo(List<String> mimeTypes);

    /**
     * Get new instance of File from uploaded FileUpload.
     *
     * @param uploadedFile uploaded file
     * @return new file instance
     */
    File createFileFromUpload(FileUpload uploadedFile);

    /**
     * Persist new file instance.
     *
     * @param file file to persist
     * @return persisted file
     */
    File saveFile(File file) throws FileException;

    /**
     * Get specified file by unique identifier.
     *
     * @param id file identifier
     * @return file with fetched body
     */
    File getFileById(Long id);

    /**
     * Get file list by file ids.
     *
     * @param ids file ids
     * @return file list
     */
    List<File> getFileListByIds(List<Long> ids);

    /**
     * Get file body as byte array. Method consolidates sources of file body as byte array.
     *
     * @param file file
     * @return file body as byte array
     */
    byte[] getFileBody(File file);

    /**
     * Update file state in data source, this method used only for update many-to-many association.
     * Usually we always create a new file instance when do upload.
     *
     * @param toUpdate file to update
     * @return updated file
     */
    File updateFile(File toUpdate) throws EltilandManagerException;

    /**
     * Remove file from data source
     *
     * @param file to remove
     */
    void deleteFile(File file) throws FileException;

    /**
     * Format file size.
     *
     * @param sizeBytes size of file in bytes
     * @return formatted string with file size info
     */
    String formatFileSize(long sizeBytes);

    /**
     * Get list of attached to course item files.
     *
     * @param item course item.
     * @return list of files.
     */
    List<File> getFilesOfCourseItem(CourseItem item);

    /**
     * Get list of attached to webinar files.
     *
     * @param webinar webinar.
     * @return list of files.
     */
    List<File> getFilesOfWebinar(Webinar webinar);
}
