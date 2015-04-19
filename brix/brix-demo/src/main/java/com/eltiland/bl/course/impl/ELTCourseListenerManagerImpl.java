package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseListenerValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.utils.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseListenerManagerImpl extends ManagerImpl implements ELTCourseListenerManager {

    @Autowired
    private CourseListenerValidator courseListenerValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public ELTCourseListener getById(Long id) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.setFetchMode("course", FetchMode.JOIN);
        criteria.setFetchMode("course.author", FetchMode.JOIN);
        criteria.setFetchMode("listener", FetchMode.JOIN);
        criteria.add(Restrictions.eq("id", id));
        return (ELTCourseListener) criteria.uniqueResult();
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseListener create(ELTCourseListener listener) throws CourseException {
        courseListenerValidator.isValid(listener);
        try {
            return genericManager.saveNew(listener);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_LISTENER_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public void delete(ELTCourseListener listener) throws CourseException {
        genericManager.initialize(listener, listener.getListeners());
        try {
            for (ELTCourseListener child : listener.getListeners()) {
                genericManager.delete(child);
            }
            genericManager.delete(listener);
        } catch (EltilandManagerException e) {
            throw new CourseException(CourseException.ERROR_LISTENER_DELETE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseListener update(ELTCourseListener listener) throws CourseException {
        courseListenerValidator.isValid(listener);
        try {
            return genericManager.update(listener);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_LISTENER_UPDATE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ELTCourseListener getItem(User user, ELTCourse course) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("listener", user));
        return (ELTCourseListener) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseListener> getList(ELTCourse course, String searchString, Integer index, Integer count,
                                           String sProperty, boolean isAscending,
                                           Boolean isListener, Boolean onlyParents) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.add(Restrictions.eq("course", course));
        if (isListener != null) {
            if (isListener) {
                criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
            } else {
                criteria.add(Restrictions.ne("status", PaidStatus.CONFIRMED));
            }
        }
        if (onlyParents != null) {
            if (onlyParents) {
                criteria.add(Restrictions.isNull("parent"));
            }
        }

        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("listener.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        criteria.addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseListener> getList(ELTCourse course, Boolean isListener, Boolean onlyParents) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.add(Restrictions.eq("course", course));
        if (isListener != null) {
            if (isListener) {
                criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
            } else {
                criteria.add(Restrictions.ne("status", PaidStatus.CONFIRMED));
            }
        }
        if (onlyParents != null) {
            if (onlyParents) {
                criteria.add(Restrictions.isNull("parent"));
            }
        }
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCount(ELTCourse course, String searchString, Boolean isListener, Boolean onlyParents) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseListener.class);
        criteria.add(Restrictions.eq("course", course));
        if (isListener != null) {
            if (isListener) {
                criteria.add(Restrictions.eq("status", PaidStatus.CONFIRMED));
            } else {
                criteria.add(Restrictions.ne("status", PaidStatus.CONFIRMED));
            }
        }
        if (onlyParents != null) {
            if (onlyParents) {
                criteria.add(Restrictions.isNull("parent"));
            }
        }

        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("listener.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccess(User user, ELTCourse course) {
        ELTCourseListener listener = getItem(user, course);
        if (listener == null || !listener.getStatus().equals(PaidStatus.CONFIRMED)) {
            return false;
        } else {
            if (listener.getDays() == null) {
                return true;
            } else {
                DateTime limit = (new DateTime(listener.getPayDate().getTime())).
                        plusDays(listener.getDays().intValue());
                DateTime current = new DateTime(DateUtils.getCurrentDate());
                return current.isBefore(limit.toDate().getTime());
            }
        }
    }
}
