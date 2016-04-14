package com.eltiland.bl.validators;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import org.springframework.stereotype.Component;

/**
 * Course item print statistics validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseItemPrintStatValidator {
    public void isValid(CourseItemPrintStat item) throws CourseException {
        if (item.getItem() == null) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_ITEM_EMPTY);
        }
        if (item.getListener() == null) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_LISTENER_EMPTY);
        }
        if (item.getCurrentPrint() == null && item.getPrintLimit() == null) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_NULL_VALUE);
        }
        if (item.getCurrentPrint() < 0 && item.getPrintLimit() < 0) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_NEGATIVE_VALUE);
        }
        if (item.getCurrentPrint() > item.getPrintLimit()) {
            throw new CourseException(CourseException.ERROR_PRINTSTAT_WRONG_VALUE);
        }
    }
}
