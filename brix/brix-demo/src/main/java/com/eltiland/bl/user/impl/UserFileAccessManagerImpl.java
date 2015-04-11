package com.eltiland.bl.user.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.UserFileAccessManager;
import com.eltiland.bl.validators.UserFileAccessValidator;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.model.user.User;
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
public class UserFileAccessManagerImpl extends ManagerImpl implements UserFileAccessManager {

    @Autowired
    private UserFileAccessValidator userFileAccessValidator;
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = UserException.class)
    public UserFileAccess create(UserFileAccess fileAccess) throws UserException {
        userFileAccessValidator.isValid(fileAccess);
        try {
            return genericManager.saveNew(fileAccess);
        } catch (ConstraintException e) {
            throw new UserException(UserException.ERROR_USERFILE_CREATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public void delete(UserFileAccess fileAccess) throws UserException {
        try {
            genericManager.delete(fileAccess);
        } catch (EltilandManagerException e) {
            throw new UserException(UserException.ERROR_USERFILE_DELETE, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserFileAccess getAccessInformation(User user, UserFile file) {
        Criteria criteria = getCurrentSession().createCriteria(UserFileAccess.class);
        criteria.add(Restrictions.eq("client", user));
        criteria.add(Restrictions.eq("file", file));
        return (UserFileAccess) criteria.uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFileAccess> getAccessInformation(UserFile file) {
        Criteria criteria = getCurrentSession().createCriteria(UserFileAccess.class);
        criteria.add(Restrictions.eq("file", file));
        return criteria.list();
    }
}
