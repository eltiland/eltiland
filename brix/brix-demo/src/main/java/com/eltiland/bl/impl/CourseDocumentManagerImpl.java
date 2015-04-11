package com.eltiland.bl.impl;

import com.eltiland.bl.CourseDocumentManager;
import com.eltiland.model.course.CourseDocument;
import com.eltiland.model.course.CourseSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 */
@Component
public class CourseDocumentManagerImpl extends ManagerImpl implements CourseDocumentManager {
    @Override
    @Transactional
    public CourseDocument getDocumentForSession(CourseSession session) {
        Criteria criteria = getCurrentSession().createCriteria(CourseDocument.class);
        criteria.createAlias("physicalDoc", "physicalDoc", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("legalDoc", "legalDoc", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("courseSession", session));
        return (CourseDocument) criteria.uniqueResult();
    }
}