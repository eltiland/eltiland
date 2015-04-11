package com.eltiland.model.library;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Presentation library entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("PRESENTATION")
public class LibraryPresentationRecord extends LibraryRecord {
}