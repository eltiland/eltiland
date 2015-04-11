package com.eltiland.bl.impl.drive;

import com.eltiland.bl.drive.GooglePageManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.model.google.GooglePage;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Google Drive API manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class GooglePageManagerImpl extends ManagerImpl implements GooglePageManager {
    @Override
    @Transactional
    public GooglePage getPageByName(String name) {
        Criteria criteria = getCurrentSession().createCriteria(GooglePage.class);
        criteria.add(Restrictions.eq("name", name));
        return (GooglePage) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public int getPagesCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(GooglePage.class);
        if (searchString != null) {
            criteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GooglePage> getPagesList(
            String searchString, int first, int count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(GooglePage.class);

        if (searchString != null) {
            criteria.add(Restrictions.like("name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
