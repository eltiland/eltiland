package com.eltiland.model.course2.content.audio;

import com.eltiland.model.course2.content.ELTCourseItem;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Course item - audio.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("AUDIO")
public class ELTAudioCourseItem extends ELTCourseItem {

}
