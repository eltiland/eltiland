package com.eltiland.bl;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;

import java.util.List;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseSessionManager {

    /**
     * Creates and persists new course entity.
     * New course will be NOT applied and NOT published.
     *
     * @param course course to create.
     * @return created course.
     */
    CourseSession getActiveSession(Course course);

    /**
     * Make course session active.
     *
     * @param session course session entity.
     */
    void setActiveSession(CourseSession session) throws EltilandManagerException;
}
