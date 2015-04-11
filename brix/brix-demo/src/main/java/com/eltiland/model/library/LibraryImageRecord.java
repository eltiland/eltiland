package com.eltiland.model.library;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Image library entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("IMAGE")
public class LibraryImageRecord extends LibraryRecord {
}
