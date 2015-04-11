package com.eltiland.ui.common.components.file;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.validators.FileValidator;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Upload panel.
 *
 * @author Aleksey Plotnikov.
 */
class UploadPanel extends ELTDialogPanel implements IDialogSimpleNewCallback<FileUpload> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadPanel.class);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private FileValidator fileValidator;

    private IDialogActionProcessor<FileUpload> newCallback;

    /**
     * Max upload file size (default value)
     */
    private int maxFileUploadSize = Integer.valueOf(eltilandProps.getProperty("max.upload.file.size.mb"));

    /**
     * main form filed, needs for choose file to uploading.
     */
    private FileUploadField uploadField = new FileUploadField("uploadFileFiled");

    private FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");


    /**
     * Available file MIME types.
     */
    private List<MimeTypes.MimeType> availableTypes;

    public UploadPanel(String id, List<MimeTypes.MimeType> allowedToUploadFileTypes) {
        super(id);
        this.availableTypes = allowedToUploadFileTypes;
        form.add(uploadField);
        //add validators
        uploadField.add(new FileUploadSizeValidator());
        uploadField.add(new FileUploadNameValidator());
        uploadField.add(new FileTypeValidator());

        feedbackPanel.setFilter(new IFeedbackMessageFilter() {
            @Override
            public boolean accept(FeedbackMessage message) {
                return message.isError();
            }
        });
        form.add(feedbackPanel.setOutputMarkupId(true));
    }

    @Override
    protected String getHeader() {
        return getString("chooseFileMessage");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        final FileUpload upload = uploadField.getFileUpload();
        if (upload != null) {
            newCallback.process(new TransientReadOnlyModel<>(upload), target);
        } else {
            ELTAlerts.renderErrorPopup(getString("uploadFileFiled.Required"), target);
        }
    }

    @Override
    protected void onError(AjaxRequestTarget target) {
        super.onError(target);
        target.add(feedbackPanel);
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<FileUpload> callback) {
        newCallback = callback;
    }

    /**
     * File type validator.
     */
    private class FileTypeValidator extends AbstractValidator {

        @Override
        public boolean validateOnNullValue() {
            return false;
        }

        private boolean checkCorrectness(String contentType) {
            MimeTypes.MimeType type = null;
            try {
                type = MimeTypes.getTypeOf(contentType);
            } catch (IllegalArgumentException e) {

                LOGGER.warn("Someone trying to upload file with the unsupported mime type!", e);
            }
            return !(type == null || !availableTypes.contains(type));
        }

        @Override
        protected void onValidate(IValidatable iValidatable) {
            List<FileUpload> fileUploadList = (List<FileUpload>) iValidatable.getValue();
            if (fileUploadList == null) {
                return;
            }
            for (FileUpload fileUpload : fileUploadList) {
                if (checkCorrectness(fileUpload.getContentType())) {
                    return;
                }
                form.error(String.format(getString("incorrectFileFormat"), fileUpload.getClientFileName()));
                return;
            }
        }
    }

    private class FileUploadSizeValidator extends AbstractValidator {

        @Override
        public boolean validateOnNullValue() {
            return true;
        }

        @Override
        protected void onValidate(IValidatable iValidatable) {
            List<FileUpload> fileUploadList = (List<FileUpload>) iValidatable.getValue();
            if (fileUploadList == null) {
                return;
            }
            for (FileUpload fileUpload : fileUploadList) {
                if (fileUpload.getSize() > Bytes.megabytes(maxFileUploadSize).bytes()) {
                    form.error(String.format(getString("uploadTooLargeForSingleFile"),
                            fileUpload.getClientFileName(), maxFileUploadSize));
                    return;
                }
            }
        }
    }

    private class FileUploadNameValidator extends AbstractValidator {

        @Override
        public boolean validateOnNullValue() {
            return true;
        }

        @Override
        protected void onValidate(IValidatable iValidatable) {
            List<FileUpload> fileUploadList = (List<FileUpload>) iValidatable.getValue();
            if (fileUploadList == null) {
                return;
            }
            for (FileUpload fileUpload : fileUploadList) {
                if (fileValidator.isStandardNameFile(fileUpload.getClientFileName())) {
                    form.error(String.format(getString("incorrectFileName"), fileUpload.getClientFileName()));
                    return;
                }
            }
        }
    }
}
