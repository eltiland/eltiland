package com.eltiland.bl.impl.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumGroupManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumGroup;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager of forum group entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ForumGroupManagerImpl extends ManagerImpl implements ForumGroupManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public ForumGroup createForumGroup(ForumGroup forumGroup) throws EltilandManagerException {
        try {
            return genericManager.saveNew(forumGroup);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when creating forum group", e);
        }
    }

    @Override
    @Transactional
    public void deleteForumGroup(ForumGroup forumGroup) throws EltilandManagerException {
        genericManager.initialize(forumGroup, forumGroup.getForumSet());
        for (Forum forum : forumGroup.getForumSet()) {
            genericManager.delete(forum);
        }
        genericManager.delete(forumGroup);
    }

    @Override
    public ForumGroup updateForumGroup(ForumGroup forumGroup) throws EltilandManagerException {
        try {
            return genericManager.update(forumGroup);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation when updating forum group", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ForumGroup getForumGroupByName(String name) {
        Criteria criteria = getCurrentSession().createCriteria(ForumGroup.class);
        criteria.add(Restrictions.eq("name", name));
        return (ForumGroup) criteria.uniqueResult();
    }
}
