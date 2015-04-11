package com.eltiland.bl.user;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * User/File M-M relation manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface UserFileAccessManager {

    /**
     * Creates new entity.
     *
     * @param fileAccess file access entity to persist.
     */
    UserFileAccess create(UserFileAccess fileAccess) throws UserException;

    /**
     * Removes entity.
     *
     * @param fileAccess file access entity to remove.
     */
    void delete(UserFileAccess fileAccess) throws UserException;

    /**
     * Return access information for given file and user.
     *
     * @param user user to check
     * @param file file to check
     * @return access information, null if not found (User has not any access to file).
     */
    UserFileAccess getAccessInformation(User user, UserFile file);

    /**
     * Return access information for given file.
     *
     * @param file file to check
     * @return list of the access information entities.
     */
    List<UserFileAccess> getAccessInformation(UserFile file);
}


