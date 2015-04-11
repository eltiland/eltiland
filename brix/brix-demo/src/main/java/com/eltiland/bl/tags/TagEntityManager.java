package com.eltiland.bl.tags;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.Tag;

import java.util.List;

/**
 * tag/entity link manager.
 *
 * @author Aleksey Plotnikov.
 */
public interface TagEntityManager {

    /**
     * Check if entity has specified tag.
     *
     * @param entity_id ID of tagable entity.
     * @param tag_id    tag id.
     * @return TRUE if entity has specified tag.
     */
    boolean checkTagPresent(Long entity_id, Long tag_id);

    /**
     * Removing tag of given entity.
     *
     * @param entity_id ID of tagable entity.
     * @param tag_id    tag id.
     */
    void deleteTag(Long entity_id, Long tag_id) throws EltilandManagerException;

    /**
     * Get list of tags for given entity.
     *
     * @param entity_id ID of tagable entity.
     * @return list of the tags.
     */
    List<Tag> getEntityTags(Long entity_id);

    /**
     * Return list of entities ids, which have one of the specified tags.
     *
     * @param tags tag list.
     * @return entities list.
     */
    List<Long> getEntityIds(List<Tag> tags);

    /**
     * Delete all tag entities with given tag.
     *
     * @param tag_id tag id.
     */
    void deleteTagEntity(Long tag_id) throws EltilandManagerException;

    /**
     * Delete all tag entities for entity with given id.
     *
     * @param entity_id entity id.
     */
    void deleteTagEntityById(Long entity_id) throws EltilandManagerException;
}
