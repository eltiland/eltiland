package com.eltiland.bl.impl;

import com.eltiland.bl.CourseSessionManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
@Component
public class CourseSessionManagerImpl extends ManagerImpl implements CourseSessionManager {

    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(readOnly = true)
    public CourseSession getActiveSession(Course course) {
        Criteria criteria = getCurrentSession().createCriteria(CourseSession.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("active", true));
        return (CourseSession) criteria.uniqueResult();
    }

    @Override
    @Transactional
    public void setActiveSession(CourseSession session) throws EltilandManagerException {
        genericManager.initialize(session, session.getCourse());

        Criteria criteria = getCurrentSession().createCriteria(CourseSession.class);
        criteria.add(Restrictions.eq("course", session.getCourse()));
        List<CourseSession> sessions = criteria.list();

        for (CourseSession session1 : sessions) {
            if (session1.isActive() && (!session.getId().equals(session1.getId()))) {
                session1.setActive(false);
                try {
                    genericManager.update(session1);
                } catch (ConstraintException e) {
                    throw new EltilandManagerException("Constraint violation when saving session", e);
                }
            }
        }

        if (!session.isActive()) {
            session.setActive(true);
            try {
                genericManager.update(session);
            } catch (ConstraintException e) {
                throw new EltilandManagerException("Constraint violation when saving session", e);
            }
        }
    }
}

