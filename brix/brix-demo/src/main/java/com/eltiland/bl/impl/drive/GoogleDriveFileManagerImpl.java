package com.eltiland.bl.impl.drive;

import com.eltiland.bl.drive.GoogleDriveFileManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.model.google.GoogleDriveFile;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Google Drive File manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class GoogleDriveFileManagerImpl extends ManagerImpl implements GoogleDriveFileManager {
    @Override
    @Transactional
    public GoogleDriveFile getFileByGoogleId(String googleId) {
        Criteria criteria = getCurrentSession().createCriteria(GoogleDriveFile.class);
        criteria.add(Restrictions.eq("googleId", googleId));
        return (GoogleDriveFile) criteria.uniqueResult();
    }
}
