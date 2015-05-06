package com.eltiland.model.webinar;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.export.Exportable;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.payment.WebinarPayment;
import com.eltiland.model.search.WebinarUserSearchFilterFactory;
import com.eltiland.model.user.User;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Webinar payment information entity for concrete user.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar_user_payment", schema = "public")
@FullTextFilterDefs({
        @FullTextFilterDef(name = "webinarUserFilterFactory", impl = WebinarUserSearchFilterFactory.class)
})
@Indexed
public class WebinarUserPayment extends AbstractIdentifiable implements Exportable, PaidEntityNew {
    @SpringBean
    private GenericManager genericManager;

    public enum Role {MODERATOR, MEMBER, OBSERVER}

    private Webinar webinar;
    private User userProfile;
    private String userName;
    private String userSurname;
    private String patronymic;

    private PaidStatus status;
    private String userEmail;
    private BigDecimal price;
    private Role role;
    private Date registrationDate;
    private Date date;
    private Long userid;
    private String paylink;
    private String webinarlink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar")
    @IndexedEmbedded(prefix = "webinar:")
    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfile")
    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    @Transient
    public String getEntityName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getWebinar());
        return getWebinar().getName();
    }

    @Column(name = "userName", length = 255)
    @Field
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "usersurname", length = 255)
    @Field
    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    @Column(name = "patronymic", length = 255)
    @Field
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Column(name = "userEmail", length = 255)
    @Field
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    @Transient
    public String getName() {
        return getUserSurname() + " " + getUserName() + " " + getPatronymic();
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
        genericManager.initialize(this, this.getWebinar());

        return "Оплата за вебинар \"" + getWebinar().getName() + "\"";
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Column(name = "date")
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Field
    public PaidStatus getStatus() {
        return status;
    }

    public void setStatus(PaidStatus status) {
        this.status = status;
    }

    @Override
    @Transient
    public void setPayDate(Date date) {
        setDate(date);
    }

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Field
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Column(name = "registrationDate", nullable = false)
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }


    @Column(name = "userid")
    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    @Column(name = "paylink")
    public String getPaylink() {
        return paylink;
    }

    public void setPaylink(String paylink) {
        this.paylink = paylink;
    }

    @Column(name = "webinarlink")
    public String getWebinarlink() {
        return webinarlink;
    }

    public void setWebinarlink(String webinarlink) {
        this.webinarlink = webinarlink;
    }

    /**
     * Synthetic class for represent webinar user search criteria.
     */
    public static class WebinarSearchCriteria {
        private String searchQuery;
        private Webinar webinar;

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public Webinar getWebinar() {
            return webinar;
        }

        public void setWebinar(Webinar webinar) {
            this.webinar = webinar;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WebinarUserPayment.WebinarSearchCriteria that = (WebinarUserPayment.WebinarSearchCriteria) o;

            if (webinar != null ? !webinar.equals(that.webinar) : that.webinar != null) return false;
            if (searchQuery != null ? !searchQuery.equals(that.searchQuery) : that.searchQuery != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = searchQuery != null ? searchQuery.hashCode() : 0;
            result = 31 * result + (webinar != null ? webinar.hashCode() : 0);
            return result;
        }
    }
}
