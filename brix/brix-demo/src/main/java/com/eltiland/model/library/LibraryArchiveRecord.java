package com.eltiland.model.library;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Archive library entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("ARCHIVE")
public class LibraryArchiveRecord extends LibraryRecord {
}
