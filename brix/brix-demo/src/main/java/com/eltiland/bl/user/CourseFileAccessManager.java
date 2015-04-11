package com.eltiland.bl.user;

import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.CourseFileAccess;
import com.eltiland.model.file.UserFile;

import java.util.List;

/**
 * User/File M-M relation manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseFileAccessManager {

    /**
     * Creates new entity.
     *
     * @param access course access entity to persist.
     */
    CourseFileAccess create(CourseFileAccess access) throws UserException;

    /**
     * Removes entity.
     *
     * @param access course access entity to remove.
     */
    void delete(CourseFileAccess access) throws UserException;

    /**
     * Return access information for given file and course.
     *
     * @param course course to check
     * @param file   file to check
     * @return access information, null if not found (Course admin has not any access to file).
     */
    CourseFileAccess getAccessInformation(ELTCourse course, UserFile file);

    /**
     * Return access information for given file.
     *
     * @param file file to check
     * @return list of the access information entities.
     */
    List<CourseFileAccess> getAccessInformation(UserFile file);
}


