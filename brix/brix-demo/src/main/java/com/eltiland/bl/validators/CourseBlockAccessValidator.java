package com.eltiland.bl.validators;

import com.eltiland.bl.course.ELTCourseBlockAccessManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Course user data validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseBlockAccessValidator {

    @Autowired
    private ELTCourseBlockAccessManager blockAccessManager;

    public void isValidForCreate(ELTCourseBlockAccess blockAccess) throws CourseException {
        checkGeneral(blockAccess);
        checkUnique(blockAccess);
        checkDates(blockAccess);
    }

    public void isValidForUpdate(ELTCourseBlockAccess blockAccess) throws CourseException {
        checkGeneral(blockAccess);
        checkDates(blockAccess);
    }

    private void checkGeneral(ELTCourseBlockAccess blockAccess) throws CourseException {
        if (blockAccess.getBlock() == null) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_EMPTY_BLOCK);
        }
        if (blockAccess.getListener() == null) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_EMPTY_LISTENER);
        }
    }

    private void checkUnique(ELTCourseBlockAccess blockAccess) throws CourseException {
        ELTCourseBlockAccess block = blockAccessManager.find(blockAccess.getListener(), blockAccess.getBlock());
        if (block != null) {
            throw new CourseException(String.format(
                    CourseException.ERROR_BLOCK_ACCESS_ALREADY_SET, blockAccess.getListener().getName()));
        }
    }

    private void checkDates(ELTCourseBlockAccess blockAccess) throws CourseException {
        if ((blockAccess.getStartDate() != null && blockAccess.getEndDate() == null)
                || (blockAccess.getStartDate() == null) && blockAccess.getEndDate() != null) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_EMPTY_DATES);
        }
        if (blockAccess.getStartDate() != null && blockAccess.getStartDate().after(blockAccess.getEndDate())) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_INCORRECT_DATES);
        }
        if (!blockAccess.isOpen() && blockAccess.getStartDate() != null) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_CLOSED_DATES);
        }
    }
}
