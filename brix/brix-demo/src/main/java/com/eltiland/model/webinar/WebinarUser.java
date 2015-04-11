package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;

/**
 * WebinarMultiplyPayment/User relation.
 */
@Entity
@Table(name = "webinar_user", schema = "public")
public class WebinarUser extends AbstractIdentifiable {
    private WebinarMultiplyPayment payment;
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    public WebinarMultiplyPayment getPayment() {
        return payment;
    }

    public void setPayment(WebinarMultiplyPayment payment) {
        this.payment = payment;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

