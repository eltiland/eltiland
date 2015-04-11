package com.eltiland.bl.user.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.CourseFileAccessManager;
import com.eltiland.bl.validators.CourseFileAccessValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.CourseFileAccess;
import com.eltiland.model.file.UserFile;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User/File M-M relation manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class CourseFileAccessManagerImpl extends ManagerImpl implements CourseFileAccessManager {

    @Autowired
    private CourseFileAccessValidator courseFileAccessValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = UserException.class)
    public CourseFileAccess create(CourseFileAccess access) throws UserException {
        courseFileAccessValidator.isValid(access);
        try {
            return genericManager.saveNew(access);
        } catch (ConstraintException e) {
            throw new UserException(UserException.ERROR_USERFILE_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public void delete(CourseFileAccess access) throws UserException {
        try {
            genericManager.delete(access);
        } catch (EltilandManagerException e) {
            throw new UserException(UserException.ERROR_USERFILE_DELETE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseFileAccess getAccessInformation(ELTCourse course, UserFile file) {
        Criteria criteria = getCurrentSession().createCriteria(CourseFileAccess.class);
        criteria.add(Restrictions.eq("course", course));
        criteria.add(Restrictions.eq("file", file));
        return (CourseFileAccess) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseFileAccess> getAccessInformation(UserFile file) {
        Criteria criteria = getCurrentSession().createCriteria(CourseFileAccess.class);
        criteria.add(Restrictions.eq("file", file));
        return criteria.list();
    }
}
