package com.eltiland.bl.user.impl;

import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.User;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Manager for confirmation entities.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ConfirmationManagerImpl extends ManagerImpl implements ConfirmationManager {
    @Override
    @Transactional
    public Confirmation createConfirmation(User user, Date endingDate) {
        String code = RandomStringUtils.randomAlphanumeric(10);

        Confirmation confirmation = new Confirmation();
        confirmation.setCode(code);
        confirmation.setUser(user);
        confirmation.setEndingDate(endingDate);

        getCurrentSession().persist(confirmation);

        user.setConfirmation(confirmation);
        getCurrentSession().merge(user);

        return confirmation;
    }

    @Override
    @Transactional
    public void removeConfirmation(Confirmation confirmation) {
        User user = confirmation.getUser();
        user.setConfirmation(null);
        getCurrentSession().merge(user);

        getCurrentSession().delete(confirmation);
    }

    @Override
    @Transactional(readOnly = true)
    public Confirmation getConfirmationByCode(String code) {
        return (Confirmation) getCurrentSession().createQuery(
                "select confirmation from Confirmation as confirmation where confirmation.code = :code")
                .setParameter("code", code)
                .uniqueResult();
    }

    @Override
    @Transactional(readOnly = true)
    public Confirmation getConfirmationByUser(User user) {
        Criteria criteria = getCurrentSession().createCriteria(Confirmation.class)
                .createAlias("user", "user", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.eq("user.id", user.getId()));

        return (Confirmation) criteria.uniqueResult();
    }
}
