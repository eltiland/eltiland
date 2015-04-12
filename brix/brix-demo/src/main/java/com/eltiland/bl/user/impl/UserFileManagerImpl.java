package com.eltiland.bl.user.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.bl.validators.UserFileValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.user.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manager for User Files.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class UserFileManagerImpl extends ManagerImpl implements UserFileManager {

    @Autowired
    private UserFileValidator userFileValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = UserException.class)
    public UserFile create(UserFile userFile) throws UserException {
        userFileValidator.isValid(userFile);
        try {
            return genericManager.saveNew(userFile);
        } catch (ConstraintException e) {
            throw new UserException(UserException.ERROR_USERFILE_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public void delete(UserFile userFile) throws UserException {
        try {
            genericManager.delete(userFile);
        } catch (EltilandManagerException e) {
            throw new UserException(UserException.ERROR_USERFILE_DELETE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public UserFile update(UserFile userFile) throws UserException {
        userFileValidator.isValid(userFile);
        try {
            return genericManager.update(userFile);
        } catch (ConstraintException e) {
            throw new UserException(UserException.ERROR_USERFILE_UPDATE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFile> getFileSearchList(User user, int index, int count,
                                            String searchString, String sortProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.add(Restrictions.eq("owner", user));
        criteria.createAlias("file", "file", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("file.name", searchString).ignoreCase());
        }
        if (sortProperty != null) {
            criteria.addOrder(isAscending ? Order.asc(sortProperty) : Order.desc(sortProperty));
        }
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getFileSearchCount(User user, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.add(Restrictions.eq("owner", user));
        criteria.createAlias("file", "file", JoinType.LEFT_OUTER_JOIN);
        if (searchString != null) {
            criteria.add(Restrictions.like("file.name", searchString).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFile> getAvailableFileSearchList(User user, int index, int count,
                                                     String searchString, String sortProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.createAlias("destinations", "dest", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("owner", "owner", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("dest.id", user.getId()));
        if (searchString != null) {
            criteria.add(Restrictions.like("file.name", searchString).ignoreCase());
        }
        if (sortProperty != null) {
            criteria.addOrder(isAscending ? Order.asc(sortProperty) : Order.desc(sortProperty));
        }
        criteria.setFirstResult(index);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableFileSearchCount(User user, String searchString) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.createAlias("destinations", "dest", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("file", "file", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("dest.id", user.getId()));
        if (searchString != null) {
            criteria.add(Restrictions.like("file.name", searchString).ignoreCase());
        }
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public UserFile getByAuthorAndFile(User user, File file) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.add(Restrictions.eq("owner", user));
        criteria.add(Restrictions.eq("file", file));
        return (UserFile) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFile> getListenerFiles(User owner, ELTCourse course) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.createAlias("courses", "course", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("owner", owner));
        criteria.add(Restrictions.eq("course.id", course.getId()));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFile> getFilesForUser(User owner, User listener) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.createAlias("destinations", "dest", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("owner", owner));
        criteria.add(Restrictions.eq("dest.id", listener.getId()));
        return criteria.list();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFile> getFilesForListener(User listener, ELTCourse course) {
        Criteria criteria = getCurrentSession().createCriteria(UserFile.class);
        criteria.createAlias("destinations", "dest", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("courses", "course", JoinType.LEFT_OUTER_JOIN);
        criteria.add(Restrictions.eq("dest.id", listener.getId()));
        criteria.add(Restrictions.eq("course.id", course.getId()));
        return criteria.list();
    }
}
