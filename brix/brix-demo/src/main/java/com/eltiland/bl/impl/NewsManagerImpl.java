package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.NewsManager;
import com.eltiland.bl.validators.NewsItemValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.NewsException;
import com.eltiland.model.NewsItem;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link com.eltiland.bl.NewsManager}.
 *
 * @see com.eltiland.bl.NewsManager
 */
@Component
public class NewsManagerImpl extends ManagerImpl implements NewsManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private NewsItemValidator newsItemValidator;

    @Override
    @Transactional(readOnly = true)
    public NewsItem getNewsItem(Long id) {
        return genericManager.getObject(NewsItem.class, id);
    }

    @Override
    @Transactional(rollbackFor = NewsException.class)
    public NewsItem createNewsItem(NewsItem toCreate) throws NewsException {
        newsItemValidator.validate(toCreate);
        try {
            genericManager.saveNew(toCreate);
        } catch (ConstraintException e) {
            throw new NewsException(NewsException.CREATE_ERROR);
        }
        return toCreate;
    }

    @Override
    @Transactional
    public NewsItem updateNewsItem(NewsItem toUpdate) throws NewsException {
        newsItemValidator.validate(toUpdate);
        try {
            return genericManager.update(toUpdate);
        } catch (ConstraintException e) {
            throw new NewsException(NewsException.UPDATE_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteNewsItem(NewsItem toDelete) throws NewsException {
        try {
            genericManager.delete(toDelete);
        } catch (EltilandManagerException e) {
            throw new NewsException(NewsException.DELETE_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsItem> getNewsList(int index, Integer count, String sProperty, boolean isAscending,
                                      String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(NewsItem.class);
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("title", "%" + searchString + "%"))
                        .add(Restrictions.ilike("body", "%" + searchString + "%"))
                );
            }
        }
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NewsItem> getNewsList(int index, Integer count, List<String> sorts, boolean isAscending,
                                      String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(NewsItem.class);
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                criteria.add(Restrictions.disjunction()
                                .add(Restrictions.ilike("title", "%" + searchString + "%"))
                                .add(Restrictions.ilike("body", "%" + searchString + "%"))
                );
            }
        }
        if (sorts != null) {
            for (String property : sorts) {
                criteria.addOrder(isAscending ? Order.asc(property) : Order.desc(property));
            }
        }
        return criteria.setFirstResult(index).setMaxResults(count).list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getNewsListCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(NewsItem.class);
        if (searchString != null) {
            if (!searchString.isEmpty()) {
                criteria.add(Restrictions.disjunction()
                        .add(Restrictions.ilike("title", "%" + searchString + "%"))
                        .add(Restrictions.ilike("body", "%" + searchString + "%"))
                );
            }
        }
        return criteria.list().size();
    }
}
