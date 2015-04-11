package com.eltiland.model.course.paidservice;

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
 * Table for course payments.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_payment", schema = "public")
public class CoursePayment extends AbstractIdentifiable implements PaidEntity, Exportable {

    @SpringBean
    private GenericManager genericManager;

    private User listener;
    private boolean status;
    private BigDecimal price;
    private Date date;
    private CoursePaidInvoice invoice;
    private CoursePaidTerm term;

    @Override
    @Column(name = "status", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
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
        genericManager.initialize(this, this.getInvoice());
        genericManager.initialize(this.getInvoice(), this.getInvoice().getCourse());

        return "Оплата за курс \"" + getInvoice().getCourse().getName() + "\"";
    }

    @Override
    @Column(name = "date")
    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Column(name = "price", nullable = false)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice")
    public CoursePaidInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(CoursePaidInvoice invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener")
    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term")
    public CoursePaidTerm getTerm() {
        return term;
    }

    public void setTerm(CoursePaidTerm term) {
        this.term = term;
    }
}
