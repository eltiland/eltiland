package com.eltiland.model;

import com.eltiland.model.file.File;

import javax.persistence.*;

/**
 * PEI/File relation.
 */
@Entity
@Table(name = "pei_file", schema = "public")
public class PeiFile extends AbstractIdentifiable {
    private Pei pei;
    private File file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pei_id", nullable = false)
    public Pei getPei() {
        return pei;
    }

    public void setPei(Pei pei) {
        this.pei = pei;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

