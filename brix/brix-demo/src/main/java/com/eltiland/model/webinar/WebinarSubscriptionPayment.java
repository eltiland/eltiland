package com.eltiland.model.webinar;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
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
    @SpringBean
    private GenericManager genericManager;

    private WebinarSubscription subscription;
    private User userProfile;
    private String userName;
    private String userSurname;
    private String patronymic;

    private PaidStatus status;
    private String payLink;
    private BigDecimal price;
    private Date registrationDate;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription")
    public WebinarSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(WebinarSubscription subscription) {
        this.subscription = subscription;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfile")
    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    @Column(name = "userName", length = 255)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "usersurname", length = 255)
    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    @Column(name = "patronymic", length = 255)
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    public PaidStatus getStatus() {
        return status;
    }

    public void setStatus(PaidStatus status) {
        this.status = status;
    }

    @Column(name = "paylink")
    public String getPayLink() {
        return payLink;
    }

    public void setPayLink(String payLink) {
        this.payLink = payLink;
    }

    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "registrationDate", nullable = false)
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    /*** Paid Entity STUFF ***/

    @Override
    @Transient
    public String getEntityName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getSubscription());
        return getSubscription().getName();
    }

    @Override
    @Transient
    public String getUserEmail() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getUserProfile());
        return getUserProfile().getEmail();
    }

    @Override
    @Transient
    public String getDescription() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getSubscription());

        return "Оплата за подписку на вебинары \"" + getSubscription().getName() + "\"";
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Transient
    public void setPayDate(Date date) {
        setDate(date);
    }
}
