package com.eltiland.bl.impl.previews;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.PreviewProcessor;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author knorr
 * @version 1.0
 * @since 8/3/12
 */
@Component
@Qualifier("default")
public class DefaultPreviewProcessor implements PreviewProcessor {

    @Autowired
    private FileManager fileManager;

    @Autowired
    private FileUtility fileUtility;

    @Override
    public File createPreview(FileUpload file) {
        String type = file.getContentType();

        File newFile = new File();
        newFile.setSize(file.getSize());
        newFile.setName(file.getClientFileName());
        newFile.setType(file.getContentType());

        FileBody body = new FileBody();
        body.setHash(fileUtility.saveTemporalFile(file.getBytes()));

        FileBody preview = fileManager.getStandardIconFileByType(type).getPreviewBody();

        newFile.setBody(body);
        newFile.setPreviewBody(preview);

        return newFile;
    }

    @Override
    public boolean isCompatible(String mimeType) {
        return !MimeTypes.getTypeOf(mimeType).equals(MimeTypes.IMAGE);
    }
}
