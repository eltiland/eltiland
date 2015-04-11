package com.eltiland.bl.impl.forum;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.forum.ForumThreadManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.utils.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manager of forum thread entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ForumThreadManagerImpl extends ManagerImpl implements ForumThreadManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public ForumThread createThread(ForumThread thread, String text) throws EltilandManagerException {
        User user = EltilandSession.get().getCurrentUser();
        if (user == null) {
            throw new EltilandManagerException("Cannot create forum thread without author");
        }
        thread.setAuthor(user);
        thread.setDate(DateUtils.getCurrentDate());

        try {
            thread = genericManager.saveNew(thread);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create forum thread entity - constraint violation", e);
        }

        ForumMessage message = new ForumMessage();
        message.setAuthor(user);
        message.setDate(DateUtils.getCurrentDate());
        message.setThread(thread);
        message.setContent(text);
        message.setHeader(thread.getName());

        try {
            genericManager.saveNew(message);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create first thread message - constraint violation", e);
        }

        return thread;
    }
}
