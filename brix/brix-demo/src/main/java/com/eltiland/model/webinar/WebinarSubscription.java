package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Абонемент на вебинары. Основная сущность.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_subscription", schema = "webinar")
public class WebinarSubscription extends AbstractIdentifiable implements Serializable {
    private String name;
    private String info;
    private BigDecimal price;
    private List<Webinar> webinars;
    private Date finalDate;

    private Set<WebinarSubscriptionPayment> payments = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "info", length = 1024)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "subscription_webinar", schema = "webinar",
            joinColumns = @JoinColumn(name = "subscription_id"),
            inverseJoinColumns = @JoinColumn(name = "webinar_id")
    )
    public List<Webinar> getWebinars() {
        return webinars;
    }

    public void setWebinars(List<Webinar> webinars) {
        this.webinars = webinars;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subscription")
    public Set<WebinarSubscriptionPayment> getPayments() {
        return payments;
    }

    public void setPayments(Set<WebinarSubscriptionPayment> payments) {
        this.payments = payments;
    }

    @Column(name = "final_date", nullable = false)
    public Date getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }
}
