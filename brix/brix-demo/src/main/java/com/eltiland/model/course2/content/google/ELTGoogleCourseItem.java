package com.eltiland.model.course2.content.google;

import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.google.GoogleDriveFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Course item, which is hosted in Google.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
public abstract class ELTGoogleCourseItem extends ELTCourseItem {
    private GoogleDriveFile item;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public GoogleDriveFile getItem() {
        return item;
    }

    public void setItem(GoogleDriveFile item) {
        this.item = item;
    }
}
