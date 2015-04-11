package com.eltiland.bl.impl.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager of forum entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ForumManagerImpl extends ManagerImpl implements ForumManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public Forum createForum(Forum forum) throws EltilandManagerException {
        try {
            return genericManager.saveNew(forum);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when creating forum", e);
        }
    }
}
