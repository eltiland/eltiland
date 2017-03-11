package com.eltiland.model.google;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Model for content of Google pages, stored in database
 *
 * @author Alex Plotnikov
 */
@Entity
@Table(name = "content", schema = "public")
public class Content extends AbstractIdentifiable {
    private String content;
    private GoogleDriveFile file;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @OneToOne(mappedBy = "content")
    public GoogleDriveFile getFile() {
        return file;
    }

    public void setFile(GoogleDriveFile file) {
        this.file = file;
    }
}
