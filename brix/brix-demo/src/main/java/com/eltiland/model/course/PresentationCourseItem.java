package com.eltiland.model.course;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Lecture course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("PRESENTATION")
public class PresentationCourseItem extends GoogleCourseItem {
}
