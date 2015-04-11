package com.eltiland.model.course;

import com.eltiland.model.google.GoogleDriveFile;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Abstract google element course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
public class GoogleCourseItem extends ElementCourseItem {
    private GoogleDriveFile driveFile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driveFile")
    public GoogleDriveFile getDriveFile() {
        return driveFile;
    }

    public void setDriveFile(GoogleDriveFile driveFile) {
        this.driveFile = driveFile;
    }
}
