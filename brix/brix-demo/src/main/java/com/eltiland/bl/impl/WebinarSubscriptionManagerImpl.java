
package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionManager;
import com.eltiland.bl.validators.WebinarSubscriptionValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarSubscription;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private WebinarSubscriptionValidator webinarSubscriptionValidator;

    @Autowired
    private GenericManager genericManager;

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

    @Override
    @Transactional(rollbackFor = WebinarException.class)
    public WebinarSubscription create(WebinarSubscription subscription) throws WebinarException {
        webinarSubscriptionValidator.validate(subscription);
        try {
            return genericManager.saveNew(subscription);
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = WebinarException.class)
    public WebinarSubscription update(WebinarSubscription item) throws WebinarException {
        webinarSubscriptionValidator.validate(item);
        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_UPDATE, e);
        }
    }
}
