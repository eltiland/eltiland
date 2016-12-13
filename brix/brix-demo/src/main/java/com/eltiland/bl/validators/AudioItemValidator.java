package com.eltiland.bl.validators;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.audio.ELTAudioItem;
import org.springframework.stereotype.Component;

/**
 * Audio item validator.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class AudioItemValidator {
    public void isValid(ELTAudioItem item) throws CourseException {
        if (item.getItem() == null) {
            throw new CourseException(CourseException.ERROR_AUDIO_ITEM_EMPTY);
        }
        if (item.getLink() == null || item.getLink().isEmpty()) {
            throw new CourseException(CourseException.ERROR_AUDIO_LINK_EMPTY);
        }
        if (item.getDescription() != null && item.getDescription().length() > 2048) {
            throw new CourseException(CourseException.ERROR_AUDIO_DESCRIPTION_TOO_LONG);
        }
    }
}
