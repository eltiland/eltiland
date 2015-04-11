package com.eltiland.model;

import com.eltiland.model.file.File;

import javax.persistence.*;

/**
 * Eltiland DB properties.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "slider", schema = "public")
public class Slider extends AbstractIdentifiable {
    private int order;
    private String link;
    private File file;

    @Column(name = "image_order", nullable = false)
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Column(name = "link", length = 1024)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file")
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
