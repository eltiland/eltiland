package com.eltiland.model.magazine;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.export.Exportable;
import com.eltiland.model.payment.PaidEntity;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for client-buyer of the magazine.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "client", schema = "magazine")
public class Client extends AbstractIdentifiable implements PaidEntity, Exportable {
    @SpringBean
    private GenericManager genericManager;

    private String name;
    private String email;
    private String phone;
    private boolean active;
    private boolean status;
    private Date date;
    private String code;
    private Set<Magazine> magazines = new HashSet<>(0);
    private Set<Magazine> downloadedMagazines = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "email", nullable = false, length = 40)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone", length = 40)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "active", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "client_magazine", schema = "magazine",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "magazine_id")
    )
    public Set<Magazine> getMagazines() {
        return magazines;
    }

    public void setMagazines(Set<Magazine> magazines) {
        this.magazines = magazines;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Transient
    public BigDecimal getPrice() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getMagazines());
        BigDecimal total = BigDecimal.valueOf(0);
        for (Magazine magazine : getMagazines()) {
            total = total.add(magazine.getPrice());
        }
        return total;
    }

    @Override
    @Transient
    public String getDescription() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getMagazines());

        String description = "Оплата за журнал";
        if (this.getMagazines().size() > 1) {
            description += "ы";
        }
        description += ": ";
        boolean isFirst = true;
        for (Magazine magazine : getMagazines()) {
            if (!isFirst) {
                description += ", ";
            } else {
                isFirst = false;
            }
            description += magazine.getName();
        }
        return description;
    }

    @Override
    @Transient
    public void setPrice(BigDecimal price) {
    }

    @Override
    @Column(name = "paid")
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "client_magazine_download", schema = "magazine",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "magazine_id")
    )
    public Set<Magazine> getDownloadedMagazines() {
        return downloadedMagazines;
    }

    public void setDownloadedMagazines(Set<Magazine> downloadedMagazines) {
        this.downloadedMagazines = downloadedMagazines;
    }
}
