package com.eltiland.bl.validators;

import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.video.ELTVideoItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Course item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseVideoValidator {
    @Autowired
    private ELTCourseManager courseManager;

    public void isValid(ELTVideoItem item) throws CourseException {
        if (item.getItem() == null) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_ITEM_EMPTY);
        }
        if (item.getName() == null) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_NAME_EMPTY);
        }
        if (item.getName().length() > 1024) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_NAME_TOO_LONG);
        }
        if (item.getLink() == null) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_LINK_EMPTY);
        }
        if (item.getLink().length() > 64) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_LINK_TOO_LONG);
        }
        if (item.getDescription() != null && item.getDescription().length() > 1024) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_DESC_TOO_LONG);
        }
        if (item.getIndex() == null) {
            throw new CourseException(CourseException.ERROR_VIDEO_ITEM_INDEX_EMPTY);
        }
    }
}
