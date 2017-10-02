package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Платеж за абонемент вебинаров.
 *
 * @author Alex Plotnikov
 */
@Entity
@Table(name = "webinar_sub_payment", schema = "webinar")
public class WebinarSubscriptionPayment extends AbstractIdentifiable implements PaidEntityNew {
    private WebinarSubscriptionPayment subscriptionPayment;
    private User user;
    private PaidStatus status;

    @Override
    public String getEntityName() {
        return null;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getUserEmail() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public Long getPaidId() {
        return null;
    }

    @Override
    public BigDecimal getPrice() {
        return null;
    }

    @Override
    public void setPrice(BigDecimal price) {

    }

    @Override
    public PaidStatus getStatus() {
        return null;
    }

    @Override
    public void setStatus(PaidStatus status) {

    }

    @Override
    public void setPayDate(Date date) {

    }


}
