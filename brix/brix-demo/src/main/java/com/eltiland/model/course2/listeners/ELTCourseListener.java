package com.eltiland.model.course2.listeners;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.model.course2.content.test.ELTTestStatistics;
import com.eltiland.model.export.Exportable;
import com.eltiland.model.payment.PaidEntityNew;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Course listener.
 *
 * @author Aleksey
 */
@Entity
@Table(name = "listener", schema = "course")
public class ELTCourseListener extends AbstractIdentifiable implements PaidEntityNew, Exportable {

    @SpringBean
    private GenericManager genericManager;

    private ELTCourse course;
    private User listener;
    private boolean completed;
    private String offer;
    private String requisites;
    private ListenerType type;
    private ELTCourseListener parent;

    /* paid and export stuff */
    private PaidStatus status;
    private BigDecimal price;
    private Date payDate;
    private Long days;

    private Set<ELTCourseListener> listeners = new HashSet<>(0);
    private Set<ELTTestStatistics> statistics = new HashSet<>(0);
    private Set<CourseItemPrintStat> printStatistics = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course", nullable = false)
    public ELTCourse getCourse() {
        return course;
    }

    public void setCourse(ELTCourse course) {
        this.course = course;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener", nullable = false)
    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    public PaidStatus getStatus() {
        return status;
    }

    public void setStatus(PaidStatus status) {
        this.status = status;
    }

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Column(name = "offer", length = 128)
    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    @Column(name = "requisistes", length = 4096)
    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    @Column(name = "type", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    public ListenerType getType() {
        return type;
    }

    public void setType(ListenerType type) {
        this.type = type;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public ELTCourseListener getParent() {
        return parent;
    }

    public void setParent(ELTCourseListener parent) {
        this.parent = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ELTCourseListener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<ELTCourseListener> listeners) {
        this.listeners = listeners;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<ELTTestStatistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(Set<ELTTestStatistics> statistics) {
        this.statistics = statistics;
    }

    @Override
    @Transient
    public String getEntityName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getCourse());
        return this.getCourse().getName();
    }

    @Override
    @Transient
    public String getUserName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getListener());
        return this.getListener().getName();
    }

    @Override
    @Transient
    public String getUserEmail() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getListener());
        return this.getListener().getEmail();
    }

    @Override
    @Transient
    public Long getPaidId() {
        return getId();
    }

    @Override
    @Transient
    public String getName() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getListener());
        return getListener().getName();
    }

    @Override
    @Transient
    public String getDescription() {
        Injector.get().inject(this);
        genericManager.initialize(this, this.getCourse());
        return "Оплата за курс \"" + getCourse().getName() + "\"";
    }

    @Override
    @Transient
    public Date getDate() {
        return getPayDate();
    }


    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "pay_date")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @Column(name = "days")
    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<CourseItemPrintStat> getPrintStatistics() {
        return printStatistics;
    }

    public void setPrintStatistics(Set<CourseItemPrintStat> printStatistics) {
        this.printStatistics = printStatistics;
    }
}
