package com.eltiland.bl.validators;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Course item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseValidator {
    @Autowired
    private ELTCourseManager courseManager;

    public void isCourseValid(ELTCourse course) throws CourseException {
        if (course.getName() == null || course.getName().isEmpty()) {
            throw new CourseException(CourseException.ERROR_COURSE_EMPTY_NAME);
        }

        if (course.getName().length() > 255) {
            throw new CourseException(CourseException.ERROR_COURSE_NAME_TOO_LONG);
        }

        ELTCourse sameNameCourse = courseManager.getCourseByName(course.getName());
        if (sameNameCourse != null && (!(course.getId() != null) || (!course.getId().equals(sameNameCourse.getId())))) {
            throw new CourseException(CourseException.ERROR_COURSE_NAME_NOT_UNIQUE);
        }

        if (course.getAuthor() == null) {
            throw new CourseException(CourseException.ERROR_COURSE_EMPTY_AUTHOR);
        }

        if (course.getCreationDate() == null) {
            throw new CourseException(CourseException.ERROR_COURSE_EMPTY_CREATION_DATE);
        }

        if (course.getStatus() == null) {
            throw new CourseException(CourseException.ERROR_COURSE_EMPTY_STATUS);
        }

        if (course.isNeedConfirm() == null) {
            throw new CourseException(CourseException.ERROR_COURSE_EMPTY_CONFIRMATION_FLAG);
        }

        if (course.getDays() != null && course.getDays() < 1) {
            throw new CourseException(CourseException.ERROR_DAYS_INCORRECT);
        }

        if ((course.getPrice() == null || course.getPrice().equals(BigDecimal.ZERO)) && course.getDays() != null) {
            throw new CourseException(CourseException.ERROR_DAYS_FREE);
        }

        if (course instanceof AuthorCourse) {
            if (((AuthorCourse) course).getIndex() == null) {
                throw new CourseException(CourseException.ERROR_AUTHOR_COURSE_EMPTY_INDEX);
            }
        }

        if (course instanceof TrainingCourse) {
            Date joinDate = ((TrainingCourse) course).getJoinDate();
            Date startDate = ((TrainingCourse) course).getStartDate();
            Date finishDate = ((TrainingCourse) course).getFinishDate();

            if (joinDate == null) {
                throw new CourseException(CourseException.ERROR_TRAINING_COURSE_JOIN_DATE_EMPTY);
            }
            if (startDate == null) {
                throw new CourseException(CourseException.ERROR_TRAINING_COURSE_START_DATE_EMPTY);
            }
            if (finishDate == null) {
                throw new CourseException(CourseException.ERROR_TRAINING_COURSE_END_DATE_EMPTY);
            }
        }
    }
}
