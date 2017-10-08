
package com.eltiland.bl.impl;

import com.eltiland.bl.WebinarSubscriptionManager;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarSubscription;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Class for managing Webinar's Subscription.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarSubscriptionManagerImpl extends ManagerImpl implements WebinarSubscriptionManager {

    @Override
    @Transactional(readOnly = true)
    public int getCount() {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscription.class);
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarRecord> getList(int index, Integer count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscription.class);
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
