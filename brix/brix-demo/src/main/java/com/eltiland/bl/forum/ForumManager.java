package com.eltiland.bl.forum;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.Forum;

/**
 * Manager of forum entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ForumManager {
    /**
     * Creates and persists new Forum group.
     *
     * @param forum forum group.
     * @return new created forum group.
     */
    Forum createForum(Forum forum) throws EltilandManagerException;
}
