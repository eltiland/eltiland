package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.payment.PaidEntity;
import com.eltiland.model.payment.WebinarPayment;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Webinar payment information entity for multiply users.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_multiply_payment", schema = "public")
public class WebinarMultiplyPayment extends AbstractIdentifiable implements WebinarPayment {

    private Set<User> users = new HashSet<>(0);
    private Webinar webinar;
    private BigDecimal price;
    private boolean status;
    private String payLink;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "webinar_user", schema = "public",
            joinColumns = @JoinColumn(name = "payment_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar")
    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    @Column(name = "paid", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name = "paylink")
    public String getPayLink() {
        return payLink;
    }

    public void setPayLink(String payLink) {
        this.payLink = payLink;
    }
}
