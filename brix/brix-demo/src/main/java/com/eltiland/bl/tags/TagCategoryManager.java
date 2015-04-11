package com.eltiland.bl.tags;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.tags.TagCategory;

import java.util.List;

/**
 * Manager for Tag Category entity.
 *
 * @author Aleksey Plotnikov
 */
public interface TagCategoryManager {

    /**
     * Creates new tag category entity.
     *
     * @param category to create.
     * @return newly created and persisted tag category.
     */
    TagCategory createTagCategory(TagCategory category) throws EltilandManagerException;

    /**
     * Deletes tag category entity.
     *
     * @param category to delete.
     */
    void deleteTagCategory(TagCategory category) throws EltilandManagerException;

    /**
     * @param entity entity class simple name.
     * @return categories count for given entity class.
     */
    int getCategoryCount(String entity);

    /**
     * Return category list.
     *
     * @param entity     entity class simple name.
     * @param sortByName if TRUE - result will be sorted by name.
     * @param isAsc      ascending/descending sorting flag of sorting.
     * @return categories list.
     */
    List<TagCategory> getCategoryList(String entity, boolean sortByName, boolean isAsc);
}
