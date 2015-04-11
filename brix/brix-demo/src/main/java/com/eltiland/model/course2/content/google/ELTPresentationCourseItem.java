package com.eltiland.model.course2.content.google;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Google presentation item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("PRS")
public class ELTPresentationCourseItem extends ELTGoogleCourseItem {
}
