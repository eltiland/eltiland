package com.eltiland.bl.forum;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.forum.ForumGroup;

/**
 * Manager of forum group entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ForumGroupManager {

    /**
     * Creates and persists new Forum group.
     *
     * @param forumGroup forum group.
     * @return new created forum group.
     */
    ForumGroup createForumGroup(ForumGroup forumGroup) throws EltilandManagerException;

    /**
     * Removing Forum group.
     *
     * @param forumGroup forum group to delete.
     */
    void deleteForumGroup(ForumGroup forumGroup) throws EltilandManagerException;

    /**
     * Updates Forum group.
     *
     * @param forumGroup forum group to update
     * @return updated forum group.
     */
    ForumGroup updateForumGroup(ForumGroup forumGroup) throws EltilandManagerException;

    /**
     * @return Forum group entity by it's name.
     */
    ForumGroup getForumGroupByName(String name);
}
