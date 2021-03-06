package com.eltiland.model.google;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Google drive DB entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "google_file", schema = "public")
public class GoogleDriveFile extends AbstractIdentifiable implements Serializable {
    private String googleId;
    private String mimeType;
    private GooglePage page;
    private Content content;

    public enum TYPE {
        DOCUMENT, PRESENTATION
    }

    @Column(name = "google_id", nullable = false)
    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    @Column(name = "mime_type", nullable = false)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @OneToOne(mappedBy = "content")
    public GooglePage getPage() {
        return page;
    }

    public void setPage(GooglePage page) {
        this.page = page;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content")
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
