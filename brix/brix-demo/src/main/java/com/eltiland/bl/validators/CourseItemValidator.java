package com.eltiland.bl.validators;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import org.springframework.stereotype.Component;

/**
 * Course item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseItemValidator {
    public void isValid(ELTCourseItem item) throws CourseException {
        if ((item.getBlock() == null && item.getParent() == null) &&
                (item.getBlock() != null) && (item.getParent() != null)) {
            throw new CourseException(CourseException.ERROR_ITEM_BLOCK_EMPTY);
        }
        if (item.getName() == null) {
            throw new CourseException(CourseException.ERROR_ITEM_NAME_EMPTY);
        }
        if (item.getName().length() > 128) {
            throw new CourseException(CourseException.ERROR_ITEM_NAME_TOO_LONG);
        }
        if (item.getIndex() == null) {
            throw new CourseException(CourseException.ERROR_ITEM_INDEX_EMPTY);
        }
        if (item.getIndex() < 0) {
            throw new CourseException(CourseException.ERROR_ITEM_INDEX_INCORRECT);
        }
        if (item instanceof ELTGoogleCourseItem && ((ELTGoogleCourseItem) item).getItem() == null) {
            throw new CourseException(CourseException.ERROR_ITEM_DOCUMENT_EMPTY);
        }
        if (item instanceof ELTWebinarCourseItem && ((ELTWebinarCourseItem) item).getWebinar() == null) {
            throw new CourseException(CourseException.ERROR_ITEM_WEBINAR_EMPTY);
        }
    }
}
