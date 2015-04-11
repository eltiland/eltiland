package com.eltiland.bl.impl;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.PreviewProcessor;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.validators.FileValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.MimeSubType;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.file.File;
import com.eltiland.model.file.FileBody;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.ui.common.components.avatar.CreateAvatarPanel;
import com.eltiland.utils.MimeTypes;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link FileManager}.
 *
 * @see FileManager
 */
@Component
public class FileManagerImpl extends ManagerImpl implements FileManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerImpl.class);

    public static final int KILOBYTE = 1024;
    public static final int MEGABYTE = KILOBYTE * KILOBYTE;
    public static final int GIGABYTE = MEGABYTE * KILOBYTE;

    @Autowired
    private GenericManager genericManager;

    @Autowired
    List<PreviewProcessor> previewProcessors;

    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private FileUtility fileUtility;

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public File getStandardIconFile(UrlUtils.StandardIcons standardIcon) {
        Criteria criteria = getCurrentSession().createCriteria(File.class);
        criteria.add(Restrictions.eq("name", standardIcon.toString()))
                .setFetchMode("previewBody", org.hibernate.FetchMode.JOIN);

        return (File) criteria.uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public File getStandardIconFileByType(String mimeTypeString) {
        //Check whenever we have specific file icon fot this mime type.
        MimeSubType mimeSubType = getTypeInfo(mimeTypeString);
        if (mimeSubType == null) {
            //So, no specific icon for this type, try to use default!
            MimeTypes.MimeType mimeType = MimeTypes.getTypeOf(mimeTypeString);
            return getStandardIconFile(mimeType.getIcon());
        } else {
            return getStandardIconFile(UrlUtils.StandardIcons.valueOf(mimeSubType.getIcon()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public MimeSubType getTypeInfo(String mimeType) {
        return (MimeSubType) getCurrentSession()
                .createQuery("select mime from MimeSubType as mime where mime.type = :type")
                .setString("type", mimeType)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public List<MimeSubType> getSupportedForAvatarSubTypes() {
        return getCurrentSession().createCriteria(MimeSubType.class)
                .add(Restrictions.in("type", CreateAvatarPanel.SUPPORTED_FILE_MIMES))
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public List<MimeSubType> getTypeInfo(List<String> mimeTypes) {
        Criteria criteria = getCurrentSession().createCriteria(MimeSubType.class)
                .add(Restrictions.in("type", mimeTypes));

        return criteria.list();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public File createFileFromUpload(FileUpload uploadedFile) {
        for (PreviewProcessor processor : previewProcessors) {
            if (processor.isCompatible(uploadedFile.getContentType())) {
                return processor.createPreview(uploadedFile);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = FileException.class)
    @Override
    public File saveFile(File file) throws FileException {
        fileValidator.isFileCompletlyValid(file);
        try {
            genericManager.saveNew(file.getBody());
            //save preview only if it new one (image preview)
            //otherwise, just keep link to existed standard file body
            if (file.getPreviewBody().getId() == null) {
                genericManager.saveNew(file.getPreviewBody());
            }
            return genericManager.saveNew(file);
        } catch (ConstraintException e) {
            throw new FileException(FileException.ERROR_FILE_CREATE, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public File getFileById(Long id) {
        return (File) getCurrentSession()
                .createQuery("select file from File as file"
                        + " left join fetch file.body as body"
                        + " left join fetch file.previewBody as previewBody"
                        + " where file.id = :id")
                .setLong("id", id)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public List<File> getFileListByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        //noinspection unchecked
        return getCurrentSession()
                .createQuery("select file from File as file"
                        + " left join fetch file.previewBody as previewBody"
                        + " where file.id in (:ids)")
                .setParameterList("ids", ids)
                .list();
    }

    @Transactional
    @Override
    public byte[] getFileBody(File file) {
        Query query = getCurrentSession()
                .createQuery("select e.body from File as e where e = :file")
                .setEntity("file", file);
        FileBody body = (FileBody) query.uniqueResult();
        if (body.getFilename() == null) {
            // Data base is file body source
            return body.getBody();
        }
        // TODO: add supporting of file system as file body source
        return new byte[0];
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public File updateFile(File toUpdate) throws EltilandManagerException {
        if (!fileValidator.isFileValid(toUpdate)) {
            throw new EltilandManagerException("Invalid instance during save new " + toUpdate.toString());
        }
        if (fileValidator.isStandardNameFile(toUpdate.getName())) {
            throw new EltilandManagerException("file name not must be like standard system file name");
        }
        try {
            return genericManager.update(toUpdate);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Perhaps, you forgot to validate some fields.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = FileException.class)
    @Override
    public void deleteFile(File file) throws FileException {
        Criteria criteria = getCurrentSession().createCriteria(File.class)
                .createAlias("previewBody", "pb")
                .add(Restrictions.isNull("pb.body"))
                .add(Restrictions.eq("id", file.getId()))
                .setProjection(Projections.count("id"));

        boolean hasStandardPreviewBody = true;
        if ((Long) criteria.uniqueResult() == 0) {
            hasStandardPreviewBody = false;
        }

        // remove body from File system
        genericManager.initialize(file, file.getBody());
        genericManager.initialize(file, file.getPreviewBody());
        if (file.getBody() != null) {
            fileUtility.deleteFileResource(file.getBody().getHash());
        }
        if (file.getPreviewBody() != null) {
            fileUtility.deleteFileResource(file.getPreviewBody().getHash());
        }

        // Don't forget to delete file body
        try {
            genericManager.delete(file);
            //always delete body OneToOne relation
            genericManager.delete(file.getBody());
            //delete preview only if has binary content
            //(otherwise preview body owned by standard icon file)
            if (!hasStandardPreviewBody) {
                genericManager.delete(file.getPreviewBody());
            }
        } catch (EltilandManagerException e) {
            throw new FileException(FileException.ERROR_FILE_REMOVE, e);
        }
    }

    @Override
    public String formatFileSize(long sizeBytes) {
        double gigs = 1.0 * sizeBytes / GIGABYTE;
        double megas = 1.0 * sizeBytes / MEGABYTE;
        double kilos = 1.0 * sizeBytes / KILOBYTE;
        if (gigs > 1) {
            return String.format("%.2f GB", gigs);
        }
        if (megas > 1) {
            return String.format("%.2f MB", megas);
        }
        if (kilos > 1) {
            return String.format("%.2f KB", kilos);
        }
        return String.format("%d B", sizeBytes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesOfCourseItem(CourseItem item) {
        Criteria criteria = getCurrentSession().createCriteria(File.class);
        criteria.add(Restrictions.eq("item", item));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getFilesOfWebinar(Webinar webinar) {
        Criteria criteria = getCurrentSession().createCriteria(File.class);
        criteria.add(Restrictions.eq("webinar", webinar));
        return criteria.list();
    }
}
