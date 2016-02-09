package com.eltiland.model.course2.content.google;

import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.google.GoogleDriveFile;

import javax.persistence.*;

/**
 * Course item, which is hosted in Google.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
public abstract class ELTGoogleCourseItem extends ELTCourseItem {
    private GoogleDriveFile item;
    private Boolean hasWarning;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public GoogleDriveFile getItem() {
        return item;
    }

    public void setItem(GoogleDriveFile item) {
        this.item = item;
    }

    @Column(name = "warning", nullable = false, columnDefinition = "boolean default FALSE")
    public Boolean isHasWarning() {
        return hasWarning;
    }

    public void setHasWarning(Boolean hasWarning) {
        this.hasWarning = hasWarning;
    }
}
