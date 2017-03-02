package com.eltiland.ui.google.buttons;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;
import com.eltiland.model.google.ELTGoogleFile;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Google drive upload button.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class GoogleUploadButton extends AbstractUploadButton {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleUploadButton.class);

    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private FileUtility fileUtility;
    @SpringBean
    private FileManager fileManager;

    public GoogleUploadButton(String id) {
        super(id);
    }

    @Override
    protected void onSubmit() {
        FileUpload upload = uploadField.getFileUpload();
        if (!getAvailibleMimeTypes().contains(upload.getContentType())) {
            EltiStaticAlerts.registerErrorPopup(getString("formatErrorMessage"));
            return;
        }

        File file = new File();
        if (upload != null) {
            file.setName(upload.getClientFileName());
            file.setSize(upload.getSize());
            file.setType(upload.getContentType());

            FileBody fileBody = new FileBody();
            fileBody.setHash(fileUtility.saveTemporalFile(upload.getBytes()));
            file.setBody(fileBody);
            file.setPreviewBody(fileBody);

            try {
                fileManager.saveFile(file);
            } catch (FileException e) {
                LOGGER.error("Error while saving file", e);
                throw new WicketRuntimeException("Error while saving file", e);
            }

            GoogleDriveFile gFile = null;

            try {
                gFile = googleDriveManager.insertFile(new ELTGoogleFile(
                        file, upload.getClientFileName(), upload.getClientFileName(), upload.getContentType()),
                        getFolder());
                if (doPublish()) {
                  /*  googleDriveManager.insertPermission(gFile,
                            new ELTGooglePermissions(ELTGooglePermissions.ROLE.OWNER,
                                    ELTGooglePermissions.TYPE.USER, eltilandProps.get("gdrive.mail").toString()));*/
                    googleDriveManager.insertPermission(gFile, new ELTGooglePermissions(
                            ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.ANYONE));
                    googleDriveManager.publishDocument(gFile);
                }

            } catch (GoogleDriveException e) {
                LOGGER.error("Error while uploading google document", e);
                throw new WicketRuntimeException("Error while uploading google document", e);
            }

            try {
                fileManager.deleteFile(file);
            } catch (FileException e) {
                LOGGER.error("Error while deleting file", e);
                throw new WicketRuntimeException("Error while deleting file", e);
            }

            onClick(gFile);
        }
    }

    public abstract void onClick(GoogleDriveFile gFile);

    public abstract List<String> getAvailibleMimeTypes();

    protected GoogleDriveFile getFolder() {
        return null;
    }

    protected boolean doPublish() {
        return true;
    }
}
