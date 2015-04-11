package com.eltiland.bl.impl.tags;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.tags.TagCategoryManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
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
public class TagCategoryManagerImpl extends ManagerImpl implements TagCategoryManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public TagCategory createTagCategory(TagCategory category) throws EltilandManagerException {
        try {
            return genericManager.saveNew(category);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Constraint violation during creating tag category", e);
        }
    }

    @Override
    @Transactional
    public void deleteTagCategory(TagCategory category) throws EltilandManagerException {
        genericManager.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCategoryCount(String entity) {
        Criteria criteria = getCurrentSession().createCriteria(TagCategory.class);
        criteria.add(Restrictions.eq("entity", entity));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagCategory> getCategoryList(String entity, boolean sortByName, boolean isAsc) {
        Criteria criteria = getCurrentSession().createCriteria(TagCategory.class);
        criteria.add(Restrictions.eq("entity", entity));

        if (sortByName) {
            criteria.addOrder(isAsc ? Order.asc("name") : Order.desc("name"));
        }
        return criteria.list();
    }
}
