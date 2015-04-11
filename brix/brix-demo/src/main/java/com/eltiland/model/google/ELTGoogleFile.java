package com.eltiland.model.google;

import com.eltiland.model.file.File;

/**
 * Structure for handling all Google File data for Eltiland.
 *
 * @author Aleksey Plotnikov.
 */
public class ELTGoogleFile {
    private File file;
    private String name;
    private String description;
    private String mimeType;

    public ELTGoogleFile(File file, String name, String description, String mimeType) {
        this.file = file;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
