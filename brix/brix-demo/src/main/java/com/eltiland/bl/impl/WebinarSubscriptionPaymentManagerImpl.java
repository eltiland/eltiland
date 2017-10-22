package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarSubscriptionPaymentManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.validators.WebinarSubscriptionPaymentValidator;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarSubscription;
import com.eltiland.model.webinar.WebinarSubscriptionPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Class for managing Webinars subscription payment invoices.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarSubscriptionPaymentManagerImpl extends ManagerImpl implements WebinarSubscriptionPaymentManager {
    @Autowired
    private WebinarSubscriptionPaymentValidator webinarSubscriptionPaymentValidator;
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private WebinarUserPaymentManager webinarUserPaymentManager;

    @Qualifier("webinarServiceV3Impl")
    @Autowired
    private WebinarServiceManager webinarServiceManager;

    @Override
    @Transactional(readOnly = true)
    public WebinarSubscriptionPayment getPayment(WebinarSubscription subscription, User user) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscriptionPayment.class);
        criteria.add(Restrictions.eq("userProfile", user));
        criteria.add(Restrictions.eq("subscription", subscription));
        return (WebinarSubscriptionPayment) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public WebinarSubscriptionPayment create(WebinarSubscriptionPayment subscription) throws WebinarException {
        webinarSubscriptionPaymentValidator.validate(subscription);
        try {
            return genericManager.saveNew(subscription);
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_CREATE, e);
        }
    }

    @Override
    @Transactional
    public WebinarSubscriptionPayment update(WebinarSubscriptionPayment item) throws WebinarException {
        webinarSubscriptionPaymentValidator.validate(item);
        try {
            return genericManager.update(item);
        } catch (ConstraintException e) {
            throw new WebinarException(WebinarException.ERROR_WEBINAR_SUB_PAYMENT_UPDATE, e);
        }
    }

    @Override
    @Transactional
    public void pay(WebinarSubscriptionPayment payment) throws WebinarException {
        genericManager.initialize(payment, payment.getSubscription());
        genericManager.initialize(payment.getSubscription(), payment.getSubscription().getWebinars());

        payment.setStatus(PaidStatus.CONFIRMED);
        payment.setDate(DateUtils.getCurrentDate());

        for (Webinar webinar : payment.getSubscription().getWebinars()) {
            WebinarUserPayment p = new WebinarUserPayment();
            p.setRegistrationDate(payment.getRegistrationDate());
            p.setPatronymic(payment.getPatronymic());
            p.setDate(payment.getDate());
            p.setStatus(PaidStatus.CONFIRMED);
            p.setWebinar(webinar);
            p.setUserName(payment.getUserName());
            p.setUserSurname(payment.getUserSurname());
            p.setPrice(payment.getPrice());
            p.setUserEmail(payment.getUserEmail());
            p.setUserProfile(payment.getUserProfile());
            p.setRole(WebinarUserPayment.Role.MEMBER);

            try {
                webinarUserPaymentManager.createUser(p);
            } catch (EltilandManagerException | EmailException e) {
                throw new WebinarException(e.getMessage(), e);
            }

            try {
                webinarUserPaymentManager.payWebinarUserPayment(p);
            } catch (EltilandManagerException e) {
                throw new WebinarException(e.getMessage(), e);
            }
        }
        payment.setStatus(PaidStatus.CONFIRMED);
        update(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCount() {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscriptionPayment.class);
        criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarSubscriptionPayment> getList(int index, Integer count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarSubscriptionPayment.class);
        criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
        criteria.setFetchMode("subscription", FetchMode.JOIN);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        return criteria.list();
    }
}
