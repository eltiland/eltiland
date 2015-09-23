package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Webinar record entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_record", schema = "public")
public class WebinarRecord extends AbstractIdentifiable {
    private String name;
    private String link;
    private String password;
    private Webinar webinar;
    private BigDecimal price;
    private boolean open;
    private Set<WebinarRecordPayment> payments = new HashSet<>(0);

    @Column(name = "link", nullable = false, length = 255)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "password", nullable = false, length = 255)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar")
    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "record")
    public Set<WebinarRecordPayment> getPayments() {
        return payments;
    }

    public void setPayments(Set<WebinarRecordPayment> payments) {
        this.payments = payments;
    }

    @Column(name = "name", length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "open", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
