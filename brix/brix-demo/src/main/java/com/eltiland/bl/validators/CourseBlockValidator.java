package com.eltiland.bl.validators;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import org.springframework.stereotype.Component;

/**
 * Course block item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseBlockValidator {
    public void isCourseBlockValid(ELTCourseBlock block) throws CourseException {
        if (block.getName() == null || block.getName().isEmpty()) {
            throw new CourseException(CourseException.ERROR_BLOCK_NAME_EMPTY);
        }
        if (block.getName().length() > 128) {
            throw new CourseException(CourseException.ERROR_BLOCK_NAME_TOO_LONG);
        }
        if (block.getIndex() == null) {
            throw new CourseException(CourseException.ERROR_BLOCK_INDEX_EMPTY);
        }
        if (block.getIndex() < 1) {
            throw new CourseException(CourseException.ERROR_BLOCK_INDEX_INCORRECT);
        }
        if (block.getCourse() == null && block.getDemoCourse() == null) {
            throw new CourseException(CourseException.ERROR_BLOCK_COURSE_EMPTY);
        }
        if (block.getCourse() != null && block.getDemoCourse() != null) {
            throw new CourseException(CourseException.ERROR_BLOCK_COURSE_INCORRECT);
        }
        if (block.getDefaultAccess() == null) {
            throw new CourseException(CourseException.ERROR_BLOCK_DEFAULT_ACCESS_EMPTY);
        }
        if ((block.getStartDate() != null && block.getEndDate() == null)
                || (block.getEndDate() != null && block.getStartDate() == null)) {
            throw new CourseException(CourseException.ERROR_BLOCK_NOT_FULL_DATE);
        }
        if (block.getStartDate() != null && block.getEndDate() != null &&
                block.getStartDate().after(block.getEndDate())) {
            throw new CourseException(CourseException.ERROR_BLOCK_INCORRECT_DATE);
        }
    }
}
