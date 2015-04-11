package com.eltiland.model.library;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

/**
 * Document library entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("DOCUMENT")
@Indexed
public class LibraryDocumentRecord extends LibraryRecord {
}
