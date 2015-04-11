package com.eltiland.bl.impl.tags;

import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.bl.tags.TagManager;
import com.eltiland.model.tags.Tag;
import com.eltiland.model.tags.TagCategory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manager for Tag Category entity.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class TagManagerImpl extends ManagerImpl implements TagManager {
    @Autowired
    private TagCategoryManager tagCategoryManager;

    @Override
    @Transactional(readOnly = true)
    public List<Tag> getTagList(TagCategory category, boolean sortByName, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(Tag.class);
        criteria.add(Restrictions.eq("category", category));
        if (sortByName) {
            criteria.addOrder(isAsc ? Order.asc("name") : Order.desc("name"));
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkTagExists(TagCategory category, String tagName) {
        Criteria criteria = getCurrentSession().createCriteria(Tag.class);
        criteria.add(Restrictions.eq("category", category));
        criteria.add(Restrictions.eq("name", tagName));

        return (criteria.list().size() != 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEntityHasAnyTag(String clazz) {
        List<TagCategory> categories = tagCategoryManager.getCategoryList(clazz, false, false);

        for (TagCategory category : categories) {
            List<Tag> tags = getTagList(category, false, false);
            if (!(tags.isEmpty())) {
                return true;
            }
        }
        return false;
    }
}
