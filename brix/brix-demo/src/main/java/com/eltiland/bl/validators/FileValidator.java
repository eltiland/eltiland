package com.eltiland.bl.validators;

import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.utils.UrlUtils;
import org.springframework.stereotype.Component;

/**
 * File validator.
 *
 * @author Igor Cherednichenko
 */
@Component
public class FileValidator {

    public void isFileCompletlyValid(File file) throws FileException {
        if (!isFileValid(file)) {
            throw new FileException(FileException.ERROR_FILE_INSTANCE);
        }
        if (isStandardNameFile(file.getName())) {
            throw new FileException(FileException.ERROR_FILE_NAME);
        }
    }

    /**
     * File is valid if it has preview and valid all common fields.
     *
     * @param file file to check
     * @return true, if file instance is valid
     */
    public boolean isFileValid(File file) {
        return file.getPreviewBody() != null
                && file.getName() != null
                && file.getBody() != null
                && file.getSize() > 0
                && file.getType() != null;
    }

    /**
     * File valid if method return false
     *
     * @param fileName - file name check
     * @return true, if file name matches with system files
     */
    public boolean isStandardNameFile(String fileName) {
        for (UrlUtils.StandardIcons s : UrlUtils.StandardIcons.values()) {
            if (fileName.equals(s.name())) {
                return true;
            }
        }
        return false;
    }
}
