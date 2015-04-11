package com.eltiland.ui.common.components.file;

import com.eltiland.model.file.File;

/**
 * Inner file and name wrapper entity.
 */
class FileWrapper {
    public String name;
    public File file;

    public FileWrapper(String name, File file) {
        this.name = name;
        this.file = file;
    }
}
