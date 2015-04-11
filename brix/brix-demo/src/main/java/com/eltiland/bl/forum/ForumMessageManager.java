package com.eltiland.bl.forum;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;

import java.util.List;

/**
 * Manager of forum message entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ForumMessageManager {

    /**
     * Creates and persists new forum message.
     *
     * @param message messag eto create.
     * @return persisted message.
     */
    ForumMessage createForumMessage(ForumMessage message) throws EltilandManagerException;

    /**
     * Updates forum message.
     *
     * @param message message to update.
     * @return updated message.
     */
    ForumMessage updateForumMessage(ForumMessage message) throws EltilandManagerException;

    /**
     * Deletes forum message.
     *
     * @param message message to delete.
     */
    void deleteForumMessage(ForumMessage message) throws EltilandManagerException;

    /**
     * Get all top level messages in forum thread.
     *
     * @param thread forum thread.
     * @return list of top level messages of given thread.
     */
    List<ForumMessage> getAllTopLevelMessages(ForumThread thread);

    /**
     * Get all child messages for given message.
     *
     * @param message forum message.
     * @return list of messages-childs of given message.
     */
    List<ForumMessage> getChildMessages(ForumMessage message);

    /**
     * Get depth level for given message.
     *
     * @param message forum message.
     * @return depth leve for given message.
     */
    int getDepthLevel(ForumMessage message);

    /**
     * Get count of messages for given forum.
     *
     * @param forum forum entity.
     * @return count of messages for given forum.
     */
    int getMessageCountForForum(Forum forum);

    /**
     * Get last message for given forum.
     *
     * @param forum forum entity.
     * @return last message for given forum.
     */
    ForumMessage getLastMessageForForum(Forum forum);

    /**
     * Get last message for given thread.
     *
     * @param thread thread entity.
     * @return last message for given thread.
     */
    ForumMessage getLastMessageForThread(ForumThread thread);
}
