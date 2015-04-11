package com.eltiland.bl.user;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Manager for User Files.
 *
 * @author Aleksey Plotnikov.
 */
public interface UserFileManager {

    /**
     * Creates and persists UserFile entity.
     *
     * @param userFile entity to persist.
     * @return persisted item.
     */
    UserFile create(UserFile userFile) throws UserException;

    /**
     * Removes UserFile entity.
     *
     * @param userFile entity to remove.
     */
    void delete(UserFile userFile) throws UserException;

    /**
     * Updates UserFile entity.
     *
     * @param userFile entity to update.
     */
    UserFile update(UserFile userFile) throws UserException;

    /**
     * Get list of the files of the given user.
     *
     * @param user         owner of the files.
     * @param index        the start position of the first result, numbered from 0
     * @param count        the maximum number of results to retrieve
     * @param searchString search string.
     * @param sortProperty sorting property.
     * @param isAscending  sorting direction.
     * @return list of the files.
     */
    List<UserFile> getFileSearchList(User user, int index, int count, String searchString,
                                     String sortProperty, boolean isAscending);

    /**
     * Get count of all files, owned by user.
     *
     * @param user         owner of the files.
     * @param searchString search string.
     * @return Count of the files.
     */
    Integer getFileSearchCount(User user, String searchString);

    /**
     * Get list of the available files for the given user.
     *
     * @param user         user.
     * @param index        the start position of the first result, numbered from 0
     * @param count        the maximum number of results to retrieve
     * @param searchString search string.
     * @param sortProperty sorting property.
     * @param isAscending  sorting direction.
     * @return list of the files.
     */
    List<UserFile> getAvailableFileSearchList(User user, int index, int count, String searchString,
                                              String sortProperty, boolean isAscending);

    /**
     * Get count of the available files for the given user.
     *
     * @param user         user.
     * @param searchString search string.
     * @return Count of the files.
     */
    Integer getAvailableFileSearchCount(User user, String searchString);

    /**
     * Get userfile item by owner and content file.
     *
     * @param user user-owner of the file.
     * @param file file, content of userfile item.
     * @return Userfile item.
     */
    UserFile getByAuthorAndFile(User user, File file);

    /**
     * Get list of the files, uploaded by user to given course.
     *
     * @param owner  user-owner of the file.
     * @param course course, in which this file will be accessable.
     * @return list of the files.
     */
    List<UserFile> getListenerFiles(User owner, ELTCourse course);

    /**
     * Get list of the files, uploaded by given user for given user.
     *
     * @param owner    user-owner of the file.
     * @param listener user-destination of the file.
     * @return list of the files.
     */
    List<UserFile> getFilesForListener(User owner, User listener);
}
