package com.eltiland.bl.impl;

import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.CoursePaymentManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.user.User;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Component
public class CoursePaidInvoiceManagerImpl extends ManagerImpl implements CoursePaidInvoiceManager {
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private CoursePaymentManager coursePaymentManager;

    @Override
    @Transactional
    public CoursePaidInvoice createCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice)
            throws EltilandManagerException {
        coursePaidInvoice.setStatus(false);
        coursePaidInvoice.setCreationDate(DateUtils.getCurrentDate());

        if (coursePaidInvoice.getTerm() != null) {
            try {
                genericManager.saveNew(coursePaidInvoice.getTerm()) ;
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Cannot create paid invoice entity!", e);
            }
        }

        try {
            return genericManager.saveNew(coursePaidInvoice);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot create paid invoice entity!", e);
        }
    }

    @Override
    @Transactional
    public CoursePaidInvoice updateCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice)
            throws EltilandManagerException {
        try {
            return genericManager.update(coursePaidInvoice);
        } catch (ConstraintException e) {
            throw new EltilandManagerException("Cannot update paid invoice entity!", e);
        }
    }

    @Override
    @Transactional
    public void removeCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice)
            throws EltilandManagerException {
        genericManager.delete(coursePaidInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public CoursePaidInvoice fetchCoursePaidInvoice(CoursePaidInvoice coursePaidInvoice) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("id", coursePaidInvoice.getId()))
                .setFetchMode("course", FetchMode.JOIN)
                .setFetchMode("item", FetchMode.JOIN);
        return (CoursePaidInvoice) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCoursePaid(Course course) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("course", course))
                .add(Restrictions.isNull("item"))
                .add(Restrictions.eq("status", true))
                .setMaxResults(1)
                .addOrder(Order.desc("creationDate"));
        CoursePaidInvoice invoice = (CoursePaidInvoice) criteria.uniqueResult();

        return invoice != null && !(invoice.getPrice().intValue() == 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlockPaid(FolderCourseItem item) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("item", item))
                .add(Restrictions.eq("status", true))
                .setMaxResults(1)
                .addOrder(Order.desc("creationDate"));
        CoursePaidInvoice invoice = (CoursePaidInvoice) criteria.uniqueResult();

        return invoice != null && !(invoice.getPrice().intValue() == 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasNotApprovedInvoice(Course course) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("course", course))
                .add(Restrictions.isNull("item"))
                .setMaxResults(1)
                .addOrder(Order.desc("creationDate"));
        CoursePaidInvoice invoice = (CoursePaidInvoice) criteria.uniqueResult();

        return invoice != null && !invoice.getStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasNotApprovedInvoice(FolderCourseItem item) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("item", item))
                .setMaxResults(1)
                .addOrder(Order.desc("creationDate"));
        CoursePaidInvoice invoice = (CoursePaidInvoice) criteria.uniqueResult();

        return invoice != null && !invoice.getStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoursePaidInvoice> getListOfNotApprovedInvoices(int index, int count, String sProperty, boolean isAsc) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("status", false))
                .setFirstResult(index).setMaxResults(count);
        if (sProperty != null) {
            criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public int getCountOfNotApprovedInvoices() {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("status", false));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public CoursePaidInvoice getActualInvoice(Course course, FolderCourseItem item) {
        Criteria criteria = getCurrentSession()
                .createCriteria(CoursePaidInvoice.class)
                .add(Restrictions.eq("course", course))
                .add(Restrictions.ne("price", new BigDecimal(0)))
                .setMaxResults(1).addOrder(Order.desc("creationDate"));
        criteria.add((item == null) ? Restrictions.isNull("item") : Restrictions.eq("item", item));

        return (CoursePaidInvoice) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCourseEntirePaid(Course course, User user) {
        Criteria criteria = getCurrentSession()
                .createCriteria(FolderCourseItem.class)
                .add(Restrictions.eq("courseFull", course));
        List<FolderCourseItem> folders = criteria.list();

        // check for any paid block
        for (FolderCourseItem item : folders) {
            CoursePaidInvoice invoice = getActualInvoice(course, item);
            if (invoice != null) {
                if (coursePaymentManager.isEntityPaidByUser(user, invoice)) {
                    return false;
                }
            }
        }

        if (isCoursePaid(course)) { // check for paid entire course
            CoursePaidInvoice invoice = getActualInvoice(course, null);
            return !coursePaymentManager.isEntityPaidByUser(user, invoice);
        } else { // check for empty course or free block
            if (folders.isEmpty()) {
                return false;
            }
            for (FolderCourseItem item : folders) {
                if (!isBlockPaid(item)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderCourseItem> getPaidBlocksForCourse(Course course, User user) {
        Criteria criteria = getCurrentSession()
                .createCriteria(FolderCourseItem.class)
                .add(Restrictions.eq("courseFull", course));
        List<FolderCourseItem> folders = criteria.list();
        List<FolderCourseItem> result = new ArrayList<>();

        for (FolderCourseItem item : folders) {
            if (isBlockPaid(item)) {
                CoursePaidInvoice invoice = getActualInvoice(course, item);
                if (invoice != null) {
                    if (!(coursePaymentManager.isEntityPaidByUser(user, invoice))) {
                        result.add(item);
                    }
                }
            }
        }

        return result;
    }
}

