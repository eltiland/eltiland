package com.eltiland.bl.user.impl;

import com.eltiland.bl.impl.ManagerImpl;
import com.eltiland.bl.user.ResetPassManager;
import com.eltiland.model.user.ResetCode;
import com.eltiland.model.user.User;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Manager for confirmation entities.
 *
 * @author Aleksey Plotnikov
 */
@Component
public class ResetPassManagerImpl extends ManagerImpl implements ResetPassManager {

    @Override
    @Transactional
    public ResetCode createResetCode(User user, Date endingDate) {
        String code = RandomStringUtils.randomAlphanumeric(10);

        ResetCode resetCode = new ResetCode();
        resetCode.setCode(code);
        resetCode.setUser(user);
        resetCode.setEndingDate(endingDate);

        getCurrentSession().persist(resetCode);

        user.setResetcode(resetCode);
        getCurrentSession().merge(user);

        return resetCode;
    }

    @Override
    @Transactional
    public void removeResetCode(ResetCode resetCode) {
        User user = resetCode.getUser();
        user.setResetcode(null);
        getCurrentSession().merge(user);

        getCurrentSession().delete(resetCode);
    }

    @Override
    @Transactional(readOnly = true)
    public ResetCode getResetInfoByCode(String code) {
        return (ResetCode) getCurrentSession().createQuery(
                "select resetCode from ResetCode as resetCode where resetCode.code = :code")
                .setParameter("code", code)
                .uniqueResult();
    }
}
