package com.eltiland.bl;

import com.eltiland.model.course.CourseDocument;
import com.eltiland.model.course.CourseSession;

/**
 * Course manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface CourseDocumentManager {

    /**
     * @param session course session.
     * @return course document entity for session.
     */
    CourseDocument getDocumentForSession(CourseSession session);
}
