package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.SubscriberManager;
import com.eltiland.bl.validators.SubscriberValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.SubscriberException;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Subscriber entity manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class SubscriberManagerImpl extends ManagerImpl implements SubscriberManager {
    @Autowired
    private GenericManager genericManager;

    @Autowired
    private SubscriberValidator subscriberValidator;

    @Override
    @Transactional
    public Subscriber createSubscriber(Subscriber subscriber) throws SubscriberException {
        try {
            String code = RandomStringUtils.randomAlphanumeric(10);
            subscriber.setUnsubscribe(code);
            subscriber.setCreationDate(DateUtils.getCurrentDate());
            subscriberValidator.validate(subscriber);
            return genericManager.saveNew(subscriber);
        } catch (ConstraintException e) {
            throw new SubscriberException(SubscriberException.CREATE_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteSubscriber(Subscriber subscriber) throws SubscriberException {
        try {
            genericManager.delete(subscriber);
        } catch (EltilandManagerException e) {
            throw new SubscriberException(SubscriberException.DELETE_ERROR);
        }
    }

    @Override
    @Transactional
    public Subscriber updateSubscriber(Subscriber subscriber) throws SubscriberException {
        subscriberValidator.validate(subscriber);
        try {
            return genericManager.update(subscriber);
        } catch (ConstraintException e) {
            throw new SubscriberException(SubscriberException.UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getActiveSubscriberCount() {
        Criteria criteria = getCurrentSession()
                .createCriteria(Subscriber.class);
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscriber> getActiveSubscriberList(int first, int count, String sortProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession()
                .createCriteria(Subscriber.class);
        criteria.addOrder(isAsc ? Order.asc(sortProperty) : Order.desc(sortProperty));
        return criteria.setFirstResult(first).setMaxResults(count).list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscriber> getActiveSubscriberList() {
        Criteria criteria = getCurrentSession()
                .createCriteria(Subscriber.class)
                .add(Restrictions.eq("disabled", false));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkSubscriberByEmail(String email) {
        Criteria criteria = getCurrentSession()
                .createCriteria(Subscriber.class)
                .add(Restrictions.eq("email", email));
        return criteria.uniqueResult() != null;
    }

    @Override
    @Transactional(readOnly = true)
    public Subscriber getSubscriberByUnCode(String unCode) {
        Criteria criteria = getCurrentSession()
                .createCriteria(Subscriber.class)
                .add(Restrictions.eq("unsubscribe", unCode));
        return (Subscriber) criteria.uniqueResult();
    }
}
