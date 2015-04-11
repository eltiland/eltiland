package com.eltiland.bl.impl;

import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePaidTerm;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.user.User;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 */
@Component
public class CoursePaymentManagerImpl extends ManagerImpl implements CoursePaymentManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional
    public CoursePayment createPayment(CoursePayment payment) throws EltilandManagerException {
        payment.setStatus(false);
        try {
            return genericManager.saveNew(payment);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Error creating new course payment", e);
        }
    }

    @Override
    @Transactional
    public CoursePayment getPayment(User listener, CoursePaidInvoice invoice, boolean createIfNoExists)
            throws EltilandManagerException {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePayment.class)
                .add(Restrictions.eq("listener", listener))
                .add(Restrictions.eq("invoice", invoice))
                .setMaxResults(1)
                .addOrder(Order.desc("id"));

        CoursePayment result = (CoursePayment) criteria.uniqueResult();
        if (createIfNoExists && (result == null)) {
            CoursePayment payment = new CoursePayment();
            payment.setListener(listener);
            payment.setInvoice(invoice);
            payment.setPrice(invoice.getPrice());
            payment.setTerm(invoice.getTerm());
            return createPayment(payment);
        } else {
            return result;
        }
    }

    @Override
    @Transactional
    public CoursePayment getPayment(User listener, CoursePaidInvoice invoice) throws EltilandManagerException {
        return getPayment(listener, invoice, false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEntityPaidByUser(User listener, CoursePaidInvoice invoice) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePayment.class)
                .add(Restrictions.eq("listener", listener))
                .add(Restrictions.eq("invoice", invoice))
                .setMaxResults(1)
                .addOrder(Order.desc("id"));
        CoursePayment payment = (CoursePayment) criteria.uniqueResult();
        if (payment == null || !payment.getStatus()) {
            return false;
        } else {
            CoursePaidTerm term = payment.getTerm();
            if (term == null || ((term.getYears() == 0) && (term.getMonths() == 0) && (term.getDays() == 0))) {
                return true;
            } else {
                Date currentDate = DateUtils.getCurrentDate();
                DateTime payDateTime = new DateTime(payment.getDate());
                payDateTime.plusYears(term.getYears());
                payDateTime.plusMonths(term.getMonths());
                payDateTime.plusDays(term.getDays());
                return currentDate.after(payDateTime.toDate());
            }
        }
    }

    @Override
    @Transactional
    public void payCoursePayment(CoursePayment payment)
            throws ConstraintException, EltilandManagerException, UserException {
        payment.setStatus(true);
        payment.setDate(DateUtils.getCurrentDate());
        genericManager.update(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public int getPaidPaymentCount() {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePayment.class)
                .add(Restrictions.ne("price", BigDecimal.ZERO))
                .add(Restrictions.eq("status", true));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursePayment> getListOfPaidPayments(int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePayment.class)
                .add(Restrictions.ne("price", BigDecimal.ZERO))
                .add(Restrictions.eq("status", true))
                .setFirstResult(index)
                .setMaxResults(count)
                .addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}

