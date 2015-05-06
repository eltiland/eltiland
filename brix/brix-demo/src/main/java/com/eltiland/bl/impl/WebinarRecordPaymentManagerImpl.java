package com.eltiland.bl.impl;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.session.EltilandSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Class for managing Webinars record payment invoices.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarRecordPaymentManagerImpl extends ManagerImpl implements WebinarRecordPaymentManager {

    @Autowired
    private FileManager fileManager;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public boolean hasRecordInvoicesForCurrentUser(WebinarRecord record) {
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null) {
            return false;
        } else {
            Criteria criteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
            criteria.add(Restrictions.eq("userProfile", currentUser));
            criteria.add(Restrictions.eq("status", PaidStatus.NEW));
            criteria.add(Restrictions.eq("record", record));
            return criteria.list().size() > 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarRecordPayment getPaymentByLink(String link) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
        criteria.add(Restrictions.eq("payLink", link));
        return (WebinarRecordPayment) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public int getPaymentsCount(String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
        criteria.createAlias("userProfile", "user", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record", "record", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record.webinar", "webinar", JoinType.LEFT_OUTER_JOIN);

        if (searchString != null) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("user.email", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("webinar.name", searchString, MatchMode.ANYWHERE).ignoreCase());
            criteria.add(searchCriteria);
        }
        criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarRecordPayment> getPaymentsList(
            int index, Integer count, String sProperty, boolean isAscending, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
        criteria.createAlias("userProfile", "user", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record", "record", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("record.webinar", "webinar", JoinType.LEFT_OUTER_JOIN);

        if (searchString != null) {
            Disjunction searchCriteria = Restrictions.disjunction();
            searchCriteria.add(Restrictions.like("user.name", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("user.email", searchString, MatchMode.ANYWHERE).ignoreCase());
            searchCriteria.add(Restrictions.like("webinar.name", searchString, MatchMode.ANYWHERE).ignoreCase());
            criteria.add(searchCriteria);
        }

        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public WebinarRecordPayment getPaymentForUser(WebinarRecord webinarRecord, User user) {
        Criteria criteria = getCurrentSession().createCriteria(WebinarRecordPayment.class);
        criteria.add(Restrictions.eq("record", webinarRecord));
        criteria.add(Restrictions.eq("userProfile", user));
        return (WebinarRecordPayment) criteria.uniqueResult();
    }
}
