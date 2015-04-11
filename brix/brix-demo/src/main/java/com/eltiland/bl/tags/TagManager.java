package com.eltiland.bl.tags;

import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;

import java.util.List;

/**
 * Manager for Tag entity.
 *
 * @author Aleksey Plotnikov
 */
public interface TagManager {
    /**
     * Return tag list for given category.
     *
     * @param category   category entity.
     * @param sortByName if TRUE - result will be sorted by name.
     * @param isAsc      ascending/descending sorting flag of sorting.
     * @return tag list.
     */
    List<Tag> getTagList(TagCategory category, boolean sortByName, boolean isAsc);

    /**
     * Check if tag already present in category.
     *
     * @param category category entity.
     * @param tagName  tag name.
     * @return TRUE if tag with given name present in given category.
     */
    boolean checkTagExists(TagCategory category, String tagName);

    /**
     * Check if entity has any tags.
     *
     * @param clazz entity to test.
     * @return TRUE if entity has any tags.
     */
    boolean isEntityHasAnyTag(String clazz);
}
