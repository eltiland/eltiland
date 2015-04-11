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
@Table(name = "google_page", schema = "public")
public class GooglePage extends AbstractIdentifiable {
    private String name;
    private GoogleDriveFile content;

    @Column(name = "name", nullable = false, unique = true, length = 128)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content")
    public GoogleDriveFile getContent() {
        return content;
    }

    public void setContent(GoogleDriveFile content) {
        this.content = content;
    }
}
