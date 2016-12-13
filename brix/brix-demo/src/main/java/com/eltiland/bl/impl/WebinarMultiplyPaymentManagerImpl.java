package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarMultiplyPaymentManager;
import com.eltiland.bl.webinars.WebinarServiceManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.utils.DateUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Class for managing Webinar's Users.
 *
 * @author Aleksey Plotnikov.
 */
@Component
public class WebinarMultiplyPaymentManagerImpl extends ManagerImpl implements WebinarMultiplyPaymentManager {

    @Autowired
    private GenericManager genericManager;
    @Qualifier("webinarServiceV3Impl")
    @Autowired
    private WebinarServiceManager webinarServiceManager;

    @Override
    @Transactional(readOnly = true)
    public WebinarMultiplyPayment getPaymentByLink(String payLink) {
        Query query = getCurrentSession().createQuery(
                "select payment from WebinarMultiplyPayment as payment " +
                        "left join fetch payment.webinar " +
                        "where payment.payLink = :payLink")
                .setParameter("payLink", payLink);
        return (WebinarMultiplyPayment) query.uniqueResult();
    }

    @Override
    @Transactional
    public boolean payWebinarUserPayment(WebinarMultiplyPayment payment) throws EltilandManagerException {
        genericManager.initialize(payment, payment.getUsers());
        genericManager.initialize(payment, payment.getWebinar());
        boolean result = false;

        int count = payment.getUsers().size();
        for (User user : payment.getUsers()) {
            WebinarUserPayment userPayment = new WebinarUserPayment();
            userPayment.setDate(DateUtils.getCurrentDate());
            userPayment.setPaylink(payment.getPayLink());
            userPayment.setUserProfile(user);

            String name[] = user.getName().split(" ");
            userPayment.setUserName(name[1]);
            userPayment.setUserSurname(name[0]);
            userPayment.setPatronymic(name[2]);
            userPayment.setDate(DateUtils.getCurrentDate());
            userPayment.setPrice(payment.getPrice().divide(BigDecimal.valueOf(count)));
            userPayment.setRegistrationDate(DateUtils.getCurrentDate());
            userPayment.setUserEmail(user.getEmail());
            userPayment.setWebinar(payment.getWebinar());
            userPayment.setRole(WebinarUserPayment.Role.MEMBER);
            userPayment.setStatus(PaidStatus.CONFIRMED);
            try {
                genericManager.saveNew(userPayment);
            } catch (ConstraintException e) {
                throw new EltilandManagerException(
                        "Cannot create userPayment - most likely it's constraint violation", e);
            }

            try {
                result = webinarServiceManager.addUser(userPayment);
            } catch (WebinarException e) {
                e.printStackTrace();
            }
            if (!result) {
                return false;
            }
            try {
                genericManager.update(userPayment);
            } catch (ConstraintException e) {
                throw new EltilandManagerException(
                        "Cannot update userPayment - most likely it's constraint violation", e);
            }
        }

        payment.setStatus(true);
        try {
            genericManager.update(payment);
        } catch (ConstraintException e) {
            throw new EltilandManagerException(
                    "Cannot update userPayment - most likely it's constraint violation", e);
        }
        return result;
    }
}
