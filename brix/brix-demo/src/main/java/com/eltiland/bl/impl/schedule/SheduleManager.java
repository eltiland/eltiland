package com.eltiland.bl.impl.schedule;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.VideoManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.Video;
import com.eltiland.model.file.File;
import com.eltiland.model.google.ELTGoogleFile;
import com.eltiland.model.google.ELTGooglePermissions;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.utils.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Properties;

/**
 * Quartz sheduler manager.
 *
 * @author Aleksey Plotnikov.
 */
public class SheduleManager {

    protected static final Logger AUDIT = LoggerFactory.getLogger("Audit");

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private VideoManager videoManager;
    @Autowired
    private LibraryRecordManager libraryRecordManager;
    @Autowired
    private GoogleDriveManager googleDriveManager;
    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private IndexCreator indexCreator;

    /**
     * Function-sheduler for filling video view count.
     */
    public void fillVideoViewCount() {
        for (Video video : genericManager.getEntityList(Video.class, "id")) {
            try {
                videoManager.fillViewCount(video);
                AUDIT.info(String.format("Video (id=%d) updated", video.getId()));
            } catch (EltilandManagerException e) {
                AUDIT.error("Cannot save video", e);
            }
        }
    }

    /**
     * Function-sheduler for filling video view count.
     */
    public void publishGoogleDocument() {
        int count = libraryRecordManager.getNotPublishedRecordCount();
        AUDIT.info(String.format("GOOGLE PUBLISHER - Founded %d not published records", count));
        if (count > 0) {
            LibraryRecord record = libraryRecordManager.getNotPublishedFirstRecord();
            AUDIT.info(String.format("GOOGLE PUBLISHER - Start publish record with id %d", record.getId()));

            genericManager.initialize(record, record.getFileContent());
            if (record.getFileContent() != null) {
                try {
                    record.setPublishing(true);
                    record.setPublishAttempts(record.getPublishAttempts() + 1);
                    genericManager.update(record);

                 /*   GoogleDriveFile folder = genericManager.getObject(GoogleDriveFile.class,
                            Long.decode(eltilandProps.get("gdrive.lib.id").toString()));*/

                    GoogleDriveFile gFile = googleDriveManager.insertFile(
                            new ELTGoogleFile(record.getFileContent(), record.getFileContent().getName(),
                                    record.getFileContent().getName(), record.getFileContent().getType()));
                    googleDriveManager.publishDocument(gFile);
               /*     if (!(record.getFileContent().getType().equals(MimeType.PDF_TYPE))) {
                        googleDriveManager.insertPermission(gFile,
                                new ELTGooglePermissions(ELTGooglePermissions.ROLE.OWNER,
                                        ELTGooglePermissions.TYPE.USER, eltilandProps.get("gdrive.mail").toString()));
                    }*/
                    googleDriveManager.insertPermission(gFile, new ELTGooglePermissions(
                            ELTGooglePermissions.ROLE.WRITER, ELTGooglePermissions.TYPE.ANYONE));
                    AUDIT.info(String.format(
                            "GOOGLE PUBLISHER - Record with id %d SUCCESSFULLY published", record.getId()));

                    File file = record.getFileContent();
                    record.setFileContent(null);
                    record.setPublished(true);
                    record.setPublishing(false);
                    record.setContent(gFile);
                    genericManager.update(record);

                    try {
                        fileManager.deleteFile(file);
                    } catch (FileException e) {
                        AUDIT.error(e.getMessage(), e);
                    }
                    AUDIT.info(String.format(
                            "GOOGLE PUBLISHER - Record with id %d SUCCESSFULLY cleaned", record.getId()));
                } catch (GoogleDriveException e) {
                    AUDIT.error("Error while publishing google document", e);
                    record.setPublishing(false);
                    try {
                        genericManager.update(record);
                    } catch (ConstraintException e1) {
                        AUDIT.error("Error while saving record", e);
                    }
                } catch (ConstraintException e) {
                    AUDIT.error("Error while saving record", e);
                }
            }
        }
    }

    /**
     * Function-sheduler for reindexing library items.
     */
    public void reindexLibraryItems() {
        indexCreator.doRebuildIndex(LibraryRecord.class);
    }
}
