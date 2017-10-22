
package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionManager;
import com.eltiland.bl.validators.WebinarSubscriptionValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    public int getCount(Boolean active) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscription.class);
        if (active != null) {
            criteria.add(active ? Restrictions.ge("finalDate",
                    DateUtils.getCurrentDate()) : Restrictions.le("finalDate", DateUtils.getCurrentDate()));
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarRecord> getList(
            int index, Integer count, String sProperty, boolean isAscending, Boolean active) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscription.class);

        if (active != null) {
            criteria.add(active ? Restrictions.ge("finalDate",
                    DateUtils.getCurrentDate()) : Restrictions.le("finalDate", DateUtils.getCurrentDate()));
        }

        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Override
    @Transactional(rollbackFor = WebinarException.class)
    public WebinarSubscription create(WebinarSubscription subscription) throws WebinarException {
        webinarSubscriptionValidator.validate(subscription);
        setFinalDate(subscription);
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
        setFinalDate(item);
        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_UPDATE, e);
        }
    }

    private void setFinalDate(WebinarSubscription subscription) {
        genericManager.initialize(subscription, subscription.getWebinars());
        Date date = null;
        for (Webinar webinar : subscription.getWebinars()) {
            Date startDate = webinar.getStartDate();
            if (date == null || startDate.after(date)) {
                date = startDate;
            }
        }
        subscription.setFinalDate(date);
    }

}
