package com.eltiland.bl;

import com.eltiland.model.file.File;
import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 * Tools for creating previews for uploaded files.
 *
 * @author knorr
 * @version 1.0
 * @since 7/26/12
 */
public interface PreviewProcessor {

    /**
     * <p>Creates file preview.<p/>
     * <b>Please notice, that returned file can be persisted or no!</b>
     *
     * @param file file to create preview
     * @return file with preview
     */
    File createPreview(FileUpload file);

    /**
     * Indicates when file of type: {@param mimeType} can be processed fby this preview processor.
     *
     * @param mimeType mime type of the file
     * @return true if preview processor can create preview of this file, false otherwises
     */
    boolean isCompatible(String mimeType);

}
