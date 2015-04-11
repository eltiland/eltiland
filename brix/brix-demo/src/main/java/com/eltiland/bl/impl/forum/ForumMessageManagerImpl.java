package com.eltiland.bl.impl.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumMessageManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manager of forum thread entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ForumMessageManagerImpl extends ManagerImpl implements ForumMessageManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public ForumMessage createForumMessage(ForumMessage message) throws EltilandManagerException {
        User user = EltilandSession.get().getCurrentUser();
        if (user == null) {
            throw new EltilandManagerException("Cannot create forum message without author");
        }
        message.setAuthor(user);
        message.setDate(DateUtils.getCurrentDate());
        try {
            return genericManager.saveNew(message);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create forum message", e);
        }
    }

    @Override
    @Transactional
    public ForumMessage updateForumMessage(ForumMessage message) throws EltilandManagerException {
        try {
            return genericManager.update(message);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update forum message", e);
        }
    }

    @Override
    public void deleteForumMessage(ForumMessage message) throws EltilandManagerException {
        genericManager.initialize(message, message.getChildren());
        for (ForumMessage child : message.getChildren()) {
            deleteMessage(child);
        }
        deleteMessage(message);
    }

    @Transactional
    private void deleteMessage(ForumMessage message) throws EltilandManagerException {
        genericManager.delete(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumMessage> getAllTopLevelMessages(ForumThread thread) {
        Criteria criteria = getCurrentSession().createCriteria(ForumMessage.class);
        criteria.add(Restrictions.eq("thread", thread));
        criteria.add(Restrictions.isNull("parent"));
        criteria.addOrder(Order.asc("date"));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForumMessage> getChildMessages(ForumMessage message) {
        Criteria criteria = getCurrentSession().createCriteria(ForumMessage.class);
        criteria.add(Restrictions.eq("parent", message));
        criteria.addOrder(Order.asc("date"));
        return criteria.list();
    }

    @Override
    public int getDepthLevel(ForumMessage message) {
        int depth = 0;

        genericManager.initialize(message, message.getParent());
        while (message.getParent() != null) {
            message = message.getParent();
            depth++;
            genericManager.initialize(message, message.getParent());
        }

        return depth;
    }

    @Override
    @Transactional(readOnly = true)
    public int getMessageCountForForum(Forum forum) {
        Query query = getCurrentSession().createQuery("select count(message) from ForumMessage as message "
                + "left join message.thread as thread "
                + "left join thread.forum as forum "
                + "where forum = :forum")
                .setParameter("forum", forum);
        Long count = (Long) query.uniqueResult();
        return count.intValue();
    }

    @Override
    @Transactional(readOnly = true)
    public ForumMessage getLastMessageForForum(Forum forum) {
        Query query = getCurrentSession().createQuery("select message from ForumMessage as message "
                + "left join message.thread as thread "
                + "left join thread.forum as forum "
                + "left join fetch message.author as author "
                + "where forum = :forum order by message.date desc")
                .setParameter("forum", forum)
                .setMaxResults(1);
        ForumMessage message = (ForumMessage) query.uniqueResult();
        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public ForumMessage getLastMessageForThread(ForumThread thread) {
        Criteria criteria = getCurrentSession().createCriteria(ForumMessage.class);
        criteria.add(Restrictions.eq("thread", thread));
        criteria.addOrder(Order.desc("date"));
        criteria.setMaxResults(1);
        return (ForumMessage) criteria.uniqueResult();
    }
}
