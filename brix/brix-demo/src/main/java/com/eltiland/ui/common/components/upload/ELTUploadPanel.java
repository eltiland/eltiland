package com.eltiland.ui.common.components.upload;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.validators.FileValidator;
import com.eltiland.model.MimeSubType;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUploadCallback;
import com.eltiland.ui.common.components.feedbacklabel.ELTFeedbackLabel;
import com.eltiland.ui.common.model.TransientReadOnlyModel;
import com.eltiland.utils.MimeTypes;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * File Upload panel.
 * Used for uploading files to the application.
 * This panel supports file type and file size restrictions.
 *
 * @author knorr
 * @version 1.0
 * @since 8/1/12
 */
public class ELTUploadPanel extends Panel implements IDialogUploadCallback<FileUpload> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ELTUploadPanel.class);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private FileValidator fileValidator;

    /**
     * Max upload file size (default value)
     */
    private int maxFileUploadSize = Integer.valueOf(eltilandProps.getProperty("max.upload.file.size.mb"));

    /**
     * Available file MIME types.
     */
    private List<MimeTypes.MimeType> availableTypes;

    /**
     * Generally limit file MIME type is sufficient to correct application work. But sometimes
     * (for example {@link com.eltiland.ui.common.components.avatar.CreateAvatarPanel}) is need more
     * precisely specify the file type
     */
    private List<MimeSubType> availableSubTypes;

    private ELTUploadPanel(String id) {
        super(id);
        //Custom upload form
        add(new UploadForm("uploadFileForm"));
    }

    /**
     * Construct.
     *
     * @param id                       Wicket markup id.
     * @param allowedToUploadFileTypes List of {@link com.eltiland.utils.MimeTypes.MimeType} object which represents supported to upload file types.
     */
    public ELTUploadPanel(String id, List<MimeTypes.MimeType> allowedToUploadFileTypes) {
        this(id);
        this.availableTypes = allowedToUploadFileTypes;

    }

    /**
     * Construct.
     *
     * @param id                       Wicket markup id.
     * @param maxFileUploadSizeMB      Maximum file upload size.
     * @param allowedToUploadFileTypes List of {@link com.eltiland.utils.MimeTypes.MimeType} object which represents supported to upload file types.
     */
    public ELTUploadPanel(String id, int maxFileUploadSizeMB, List<MimeTypes.MimeType> allowedToUploadFileTypes) {
        this(id, allowedToUploadFileTypes);
        //set required params.
        this.maxFileUploadSize = maxFileUploadSizeMB;
    }

    /**
     * Construct.
     *
     * @param id                       Wicket markup id.
     * @param allowedToUploadFileTypes List of {@link com.eltiland.utils.MimeTypes.MimeType} object which represents supported to upload file types.
     */
    public ELTUploadPanel(String id, int maxFileUploadSizeMB, List<MimeTypes.MimeType> allowedToUploadFileTypes,
                          List<MimeSubType> allowedToUploadFileSubTypes) {
        this(id, allowedToUploadFileTypes);
        //Check that passed sub types correctly related to passed types.
        boolean isPassedTypesCorrect = true;
        Iterator<MimeSubType> iterator = allowedToUploadFileSubTypes.iterator();
        while (isPassedTypesCorrect && iterator.hasNext()) {
            MimeSubType subType = iterator.next();
            if (!allowedToUploadFileTypes.contains(MimeTypes.getTypeOf(subType.getType()))) {
                LOGGER.warn("Passed sub types: %s, not correspond to passed type, sub types will be ignored!",
                        allowedToUploadFileSubTypes);
                isPassedTypesCorrect = false;
            }
        }
        if (isPassedTypesCorrect) {
            this.availableSubTypes = allowedToUploadFileSubTypes;
        }
    }


    /**
     * Extension of the standard wicket form component.
     */
    private class UploadForm extends Form<Void> {

        /**
         * main form filed, needs for choose file to uploading.
         */
        FileUploadField uploadField = new FileUploadField("uploadFileFiled");

        /**
         * Feedback label to notify user about errors which happens in file uploading process, like
         * "file is to big", or "wrong file format" etc.
         */
        private ELTFeedbackLabel feedbackLabel = new ELTFeedbackLabel("choseFileMessage",
                new ResourceModel("choseFileMessage"),
                uploadField);


        private EltiAjaxSubmitLink submitLink = new EltiAjaxSubmitLink("submitLink") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                final FileUpload upload = uploadField.getFileUpload();
                if (upload != null) {
                    if (onUploadCallback != null) {
                        onUploadCallback.process(new TransientReadOnlyModel<FileUpload>(upload), target);
                    } else {
                        LOGGER.warn("No callback object passed to the upload panel, uploaded files"
                                + " wouldn't be processed!");
                    }
                    target.add(feedbackLabel);
                    target.add(uploadField);
                    ELTAlerts.renderOKPopup(getString("completeUploadingFiles"), target);
                } else {
                    ValidationError ve = new ValidationError();
                    ve.addMessageKey("undefinedError");
                    uploadField.error(ve);
                }
            }
        };

        /**
         * Construct.
         *
         * @param id form wicket markup id.
         */
        public UploadForm(String id) {
            super(id);
            setMultiPart(true);
            setOutputMarkupId(true);

            //add form components
            add(uploadField.setOutputMarkupId(true));
            add(feedbackLabel.setOutputMarkupId(true));
            add(submitLink);

            //add validators
            uploadField.setRequired(true);
            uploadField.add(new FileUploadSizeValidator());
            uploadField.add(new FileUploadNameValidator());
            uploadField.add(new FileTypeValidator());
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
                if (type == null || !availableTypes.contains(type)) {
                    return false;
                }
                if (availableSubTypes == null) {
                    return true;
                } else {
                    return availableSubTypes.contains(fileManager.getTypeInfo(contentType));
                }
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
                    ValidationError ve = new ValidationError();
                    ve.getVariables().put("fileName", fileUpload.getClientFileName());
                    ve.addMessageKey("incorrectFileFormat");
                    uploadField.error(ve);
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
                        ValidationError ve = new ValidationError();
                        ve.addMessageKey("uploadTooLargeForSingleFile");
                        ve.getVariables().put("fileName", fileUpload.getClientFileName());
                        ve.getVariables().put("maxSize", maxFileUploadSize);
                        uploadField.error(ve);
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
                        ValidationError ve = new ValidationError();
                        ve.addMessageKey("incorrectFileName");
                        ve.getVariables().put("fileName", fileUpload.getClientFileName());
                        uploadField.error(ve);
                    }
                }
            }
        }
    }

    /**
     * Action processors.
     */
    protected IDialogUploadCallback.IDialogActionProcessor<FileUpload> onUploadCallback;

    public void setUploadCallback(IDialogUploadCallback.IDialogActionProcessor<FileUpload> callback) {
        this.onUploadCallback = callback;
    }

}