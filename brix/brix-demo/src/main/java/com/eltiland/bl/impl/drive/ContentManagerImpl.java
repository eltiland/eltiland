package com.eltiland.bl.impl.drive;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.ContentManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.google.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Google Content manager
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ContentManagerImpl extends ManagerImpl implements ContentManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = GoogleDriveException.class)
    public Content create(Content content) throws GoogleDriveException {
        try {
            genericManager.saveNew(content);
        } catch (ConstraintException e) {
            throw new GoogleDriveException(GoogleDriveException.ERROR_CONTENT_CREATE, e);
        }
        return content;
    }
}
