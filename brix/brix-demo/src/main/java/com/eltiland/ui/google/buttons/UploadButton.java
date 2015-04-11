package com.eltiland.ui.google.buttons;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.impl.previews.ImagePreviewProcessor;
import com.eltiland.model.file.File;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IErrorMessageSource;
import org.apache.wicket.validation.IValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Image upload button.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class UploadButton extends AbstractUploadButton {

    protected static final Logger LOGGER = LoggerFactory.getLogger(UploadButton.class);

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private ImagePreviewProcessor imagePreviewProcessor;


    public UploadButton(String id) {
        super(id);
    }

    public abstract List<String> getAvailibleMimeTypes();

    @Override
    protected void onSubmit() {
        FileUpload upload = uploadField.getFileUpload();
        if (!getAvailibleMimeTypes().contains(upload.getContentType())) {
            uploadField.error(new IValidationError() {
                @Override
                public String getErrorMessage(IErrorMessageSource messageSource) {
                    return getString("formatErrorMessage");
                }
            });
            return;
        }

        File file = imagePreviewProcessor.createPreview(upload);
//        try {
//            fileManager.saveFile(file);
//        } catch (EltilandManagerException e) {
//            LOGGER.error("Error while creating file", e);
//            throw new WicketRuntimeException(e);
//        }
        onClick(file);
    }

    protected abstract void onClick(File image);
}
