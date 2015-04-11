package com.eltiland.bl.impl;

import com.eltiland.bl.EmailManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.subscribe.Email;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Email entity manager.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class EmailManagerImpl extends ManagerImpl implements EmailManager {
    @Autowired
    private GenericManager genericManager;

    @Override
    @Transactional(rollbackFor = EmailException.class)
    public void deleteEmail(Email email) throws EmailException {
        try {
            genericManager.delete(email);
        } catch (EltilandManagerException e) {
            throw new EmailException(EmailException.DELETE_ERROR);
        }
    }

    @Override
    @Transactional
    public Email createEmail(Email email) throws EmailException {
        try {
            return genericManager.saveNew(email);
        } catch (ConstraintException e) {
            throw new EmailException(EmailException.CREATE_ERROR);
        }
    }

    @Override
    @Transactional
    public Email updateEmail(Email email) throws EmailException {
        try {
            return genericManager.update(email);
        } catch (ConstraintException e) {
            throw new EmailException(EmailException.UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int getEmailCount(boolean status) {
        Criteria criteria = getCurrentSession()
                .createCriteria(Email.class)
                .add(Restrictions.eq("status", status));
        return criteria.list().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Email> getEmailList(boolean status, int index, Integer count, String sProperty, boolean isAscending) {
        Criteria criteria = getCurrentSession()
                .createCriteria(Email.class)
                .add(Restrictions.eq("status", status))
                .setFirstResult(index)
                .setMaxResults(count)
                .addOrder(isAscending ? Order.asc(sProperty) : Order.desc(sProperty));
        return criteria.list();
    }
}
