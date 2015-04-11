package com.eltiland.model.webinar;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.export.Exportable;
import com.eltiland.model.payment.PaidEntity;
import com.eltiland.model.user.User;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Webinar record payment information entity for concrete user.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_record_payment", schema = "public")
public class WebinarRecordPayment extends AbstractIdentifiable implements PaidEntity, Exportable {

    @SpringBean
    private GenericManager genericManager;

    private BigDecimal price;
    private WebinarRecord record;
    private User userProfile;
    private String payLink;
    private boolean status;
    private Date date;

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Transient
    public String getName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getUserProfile());
        return userProfile.getName();
    }

    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    @Transient
    public String getDescription() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getRecord());
        genericManager.initialize(this.getRecord(), this.getRecord().getWebinar());

        return "Оплата за запись вебинара \""+ getRecord().getWebinar().getName() + "\"";
    }

    @Override
    @Column(name = "date")
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    @Column(name = "status", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record")
    public WebinarRecord getRecord() {
        return record;
    }

    public void setRecord(WebinarRecord record) {
        this.record = record;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfile")
    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    @Column(name = "paylink")
    public String getPayLink() {
        return payLink;
    }

    public void setPayLink(String payLink) {
        this.payLink = payLink;
    }
}
