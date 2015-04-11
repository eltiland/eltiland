package com.eltiland.model.course2.content.google;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Google document item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("DOC")
public class ELTDocumentCourseItem extends ELTGoogleCourseItem {
    private boolean printable;

    @Column(name = "printable", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPrintable() {
        return printable;
    }

    public void setPrintable(boolean printable) {
        this.printable = printable;
    }
}
