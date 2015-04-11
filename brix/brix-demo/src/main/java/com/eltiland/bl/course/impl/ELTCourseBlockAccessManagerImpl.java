package com.eltiland.bl.course.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseBlockAccessManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.validators.CourseBlockAccessValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import com.eltiland.model.user.User;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course block access manager implementation.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class ELTCourseBlockAccessManagerImpl extends ManagerImpl implements ELTCourseBlockAccessManager {

    @Autowired
    private GenericManager genericManager;
    @Autowired
    private CourseBlockAccessValidator courseBlockAccessValidator;

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseBlockAccess create(ELTCourseBlockAccess blockAccess) throws CourseException {
        courseBlockAccessValidator.isValidForCreate(blockAccess);
        try {
            return genericManager.saveNew(blockAccess);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = CourseException.class)
    public ELTCourseBlockAccess update(ELTCourseBlockAccess blockAccess) throws CourseException {
        courseBlockAccessValidator.isValidForUpdate(blockAccess);
        try {
            return genericManager.update(blockAccess);
        } catch (ConstraintException e) {
            throw new CourseException(CourseException.ERROR_BLOCK_ACCESS_UPDATE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ELTCourseBlockAccess find(User user, ELTCourseBlock block) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseBlockAccess.class);
        criteria.add(Restrictions.eq("listener", user));
        criteria.add(Restrictions.eq("block", block));
        return (ELTCourseBlockAccess) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ELTCourseBlockAccess> getAccessInformation(ELTCourseBlock block) {
        Criteria criteria = getCurrentSession().createCriteria(ELTCourseBlockAccess.class);
        criteria.setFetchMode("listener", FetchMode.JOIN);
        criteria.add(Restrictions.eq("block", block));
        return criteria.list();
    }
}
