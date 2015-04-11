package com.eltiland.bl.forum;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumThread;

/**
 * Manager of forum entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ForumThreadManager {
    /**
     * Creates and persists new Forum thread.
     *
     * @param thread forum thread.
     * @param text   text of the first message.
     * @return new created forum group.
     */
    ForumThread createThread(ForumThread thread, String text) throws EltilandManagerException;
}
