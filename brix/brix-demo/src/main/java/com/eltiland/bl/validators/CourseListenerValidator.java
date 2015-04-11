package com.eltiland.bl.validators;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Course listener item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseListenerValidator {

    @Autowired
    private ELTCourseListenerManager courseListenerManager;
    @Autowired
    private GenericManager genericManager;

    public void isValid(ELTCourseListener listener) throws CourseException {
        if (listener.getCourse() == null) {
            throw new CourseException(CourseException.ERROR_LISTENER_COURSE_EMPTY);
        }
        if (listener.getListener() == null) {
            throw new CourseException(CourseException.ERROR_LISTENER_USER_EMPTY);
        }
        if (listener.getId() == null &&
                courseListenerManager.getItem(listener.getListener(), listener.getCourse()) != null) {
            genericManager.initialize(listener, listener.getListener());
            throw new CourseException(String.format(
                    CourseException.ERROR_LISTENER_ALREADY_EXISTS, listener.getListener().getName()));
        }
        if (listener.getStatus() == null) {
            throw new CourseException(CourseException.ERROR_LISTENER_STATUS_EMPTY);
        }
        if (listener.getOffer() != null && listener.getOffer().length() > 128) {
            throw new CourseException(CourseException.ERROR_LISTENER_OFFER_TOO_LONG);
        }
        if (listener.getRequisites() != null && listener.getRequisites().length() > 4096) {
            throw new CourseException(CourseException.ERROR_LISTENER_REQUISITES_TOO_LONG);
        }
        if (listener.getType() == null) {
            throw new CourseException(CourseException.ERROR_LISTENER_TYPE_EMPTY);
        }
    }
}
