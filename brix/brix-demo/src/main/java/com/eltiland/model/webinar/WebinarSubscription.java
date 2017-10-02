package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Абонемент на вебинары. Основная сущность.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_subscription", schema = "webinar")
public class WebinarSubscription extends AbstractIdentifiable {
    private String name;
    private String info;
    private BigDecimal price;
    private List<Webinar> webinars;

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
}
