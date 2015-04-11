package com.eltiland.bl.impl;

import com.eltiland.bl.CourseInvoiceManager;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.user.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CourseInvoiceManagerImpl extends ManagerImpl implements CourseInvoiceManager {
    @Override
    @Transactional(readOnly = true)
    public boolean checkAccessToCourse(Course course, User user) {
        if (user.isSuperUser() || course.getAuthor().getId().equals(user.getId())) {
            return true;
        } else if (course.isAutoJoin()) {
            return true;
        } else {
            Criteria criteria = getCurrentSession().createCriteria(CourseInvoice.class);
            criteria.add(Restrictions.eq("course", course));
            criteria.add(Restrictions.eq("listener", user));
            criteria.add(Restrictions.eq("apply", true));

            return (criteria.uniqueResult() != null);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkInvoicePresent(Course course, User user) {
        Criteria criteria = getCurrentSession().createCriteria(CourseInvoice.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("listener", user));
        criteria.add(Restrictions.eq("apply", false));

        return (criteria.uniqueResult() != null);
    }

    @Override
    @Transactional(readOnly = true)
    public int getInvoicesCount() {
        Criteria criteria = getCurrentSession().createCriteria(CourseInvoice.class);
        criteria.add(Restrictions.eq("apply", false));

        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseInvoice> getCourseInvoiceList(int index, Integer count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(CourseInvoice.class);
        criteria.add(Restrictions.eq("apply", false));
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));

        return criteria.list();
    }
}