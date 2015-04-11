package com.eltiland.bl.impl;

import com.eltiland.bl.CourseListenerManager;
import com.eltiland.bl.CourseSessionManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.user.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CourseListenerManagerImpl extends ManagerImpl implements CourseListenerManager {

    @Autowired
    private CourseSessionManager courseSessionManager;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public CourseListener getListenerById(Long id) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("session", "session", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("session.course", "course", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("id", id));
        return (CourseListener) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseListener getListener(Course course, User user) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.add(Restrictions.eq("listener", user));
        criteria.add(Restrictions.eq("session", courseSessionManager.getActiveSession(course)));
        return (CourseListener) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseListener getCurator(Course course, User user) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.add(Restrictions.eq("session", courseSessionManager.getActiveSession(course)));
        List<CourseListener> listeners = criteria.list();

        for (CourseListener listener : listeners) {
            genericManager.initialize(listener, listener.getUsers());
            if (listener.getUsers().contains(user)) {
                return listener;
            }
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public int getListenersCount(CourseSession session, String searchString, Boolean isConfirmed) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.add(Restrictions.eq("session", session));
        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("listener.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        if (isConfirmed != null) {
            criteria.add(isConfirmed ?
                    Restrictions.eq("status", CourseListener.Status.CONFIRMED) :
                    Restrictions.ne("status", CourseListener.Status.CONFIRMED));
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseListener> getListeners(CourseSession session, String searchString,
                                             int first, int count, String sProperty,
                                             boolean isAsc, Boolean isConfirmed) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.add(Restrictions.eq("session", session));
        criteria.createAlias("listener", "listener", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("listener.name", searchString, MatchMode.ANYWHERE).ignoreCase());
        }
        criteria.setFirstResult(first);
        criteria.setMaxResults(count);
        criteria.addOrder(isAsc ? Order.asc(sProperty) : Order.desc(sProperty));
        if (isConfirmed != null) {
            criteria.add(isConfirmed ?
                    Restrictions.eq("status", CourseListener.Status.CONFIRMED) :
                    Restrictions.ne("status", CourseListener.Status.CONFIRMED));
        }

        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseListener> getListeners(CourseSession session, Boolean isConfirmed) {
        Criteria criteria = getCurrentSession().createCriteria(CourseListener.class);
        criteria.add(Restrictions.eq("session", session));
        if (isConfirmed != null) {
            criteria.add(isConfirmed ?
                    Restrictions.eq("status", CourseListener.Status.CONFIRMED) :
                    Restrictions.ne("status", CourseListener.Status.CONFIRMED));
        }
        return criteria.list();
    }
}

