package com.eltiland.bl.impl.integration;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.CopyUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.file.Folder;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * @author: knorr
 * @since 2/8/13
 */
public class FileUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtility.class);

    private final static int STORAGE_HIERARCHY_DEPT = 2;

    private final static int HIERARCHY_LEVEL_PART_SIZE = 2;

    private Folder parentFolder;

    private String folderPath;

    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProperties;

    @PostConstruct
    public void ensureFolderConsistence() {
        String path = eltilandProperties.getProperty("filesystem.path");
        parentFolder = new Folder(path);
        if (!parentFolder.exists()) {
            throw new WicketRuntimeException("Folder not found");
        }
        if (!parentFolder.isDirectory()) {
            throw new WicketRuntimeException(String.format("The file %s is not folder", path));
        }
        folderPath = parentFolder.getAbsolutePath();
        if (!folderPath.endsWith("/")) {
            folderPath = folderPath + "/";
        }
    }

    public String saveTemporalFile(byte[] file) {
        FileOutputStream fos = null;
        String filename = null;
        try {
            byte[] digestBytes = MessageDigest.getInstance("SHA-256").digest(file);
            filename = new String(Hex.encodeHex(digestBytes));
            String folder = filename.substring(0, 2) + "/" + filename.substring(2, 4);
            String shortFilename = filename.substring(4);

            new java.io.File(folderPath + folder).mkdirs();

            java.io.File actualFile = new java.io.File(folderPath + folder + "/" + shortFilename);

            fos = new FileOutputStream(actualFile);
            CopyUtils.copy(file, fos);
        } catch (NoSuchAlgorithmException | IOException e) {
            LOGGER.error("Unable to persist uploaded file", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.warn("Unable to close file stream", e);
                    e.printStackTrace();
                }
            }
        }
        return filename;
    }

    public IResourceStream getFileResource(String hashName) {
        assert hashName != null : "File name cannot be null!";
        File requiredFile = getFileByHash(hashName);
        if (!requiredFile.exists()) {
            throw new WicketRuntimeException("File not found");
        }
        return new FileResourceStream(requiredFile);
    }

    public void deleteFileResource(String hashName) {
        if (hashName != null) {
            File file = getFileByHash(hashName);
            if (file != null) {
                file.delete();
            }
        }
    }

    private File getFileByHash(String hashName) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        int partLen = HIERARCHY_LEVEL_PART_SIZE;
        for (index = 0; index < STORAGE_HIERARCHY_DEPT; index++) {
            stringBuilder.append(hashName.substring(index * partLen, index * partLen + partLen));
            stringBuilder.append(File.separator);
        }
        stringBuilder.append(hashName.substring(index * partLen));
        String relativePathToFile = stringBuilder.toString();
        File requiredFile = new File(parentFolder, relativePathToFile);
        if (requiredFile.exists()) {
            return requiredFile;
        } else {
            return null;
        }
    }

}