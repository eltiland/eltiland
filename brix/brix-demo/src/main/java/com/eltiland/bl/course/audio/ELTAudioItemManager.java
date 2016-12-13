package com.eltiland.bl.course.audio;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.audio.ELTAudioCourseItem;
import com.eltiland.model.course2.content.audio.ELTAudioItem;

/**
 * Video item manager for video course item.
 *
 * @author Aleksey Plotnikov.
 */
public interface ELTAudioItemManager {

    ELTAudioItem create(ELTAudioItem item) throws CourseException;

    ELTAudioItem update(ELTAudioItem item) throws CourseException;
    /**
     * Get audio data for audio course item.
     *
     * @param item audio course item.
     * @return audio data item.
     */
    ELTAudioItem get(ELTAudioCourseItem item);
}
