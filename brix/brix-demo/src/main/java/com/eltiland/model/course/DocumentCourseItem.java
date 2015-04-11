package com.eltiland.model.course;

import com.eltiland.model.google.GoogleDriveFile;

import javax.persistence.*;

/**
 * Lecture course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("LECTURE")
public class DocumentCourseItem extends GoogleCourseItem {
    private boolean printable;

    @Column(name = "printable", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPrintable() {
        return printable;
    }

    public void setPrintable(boolean printable) {
        this.printable = printable;
    }
}
