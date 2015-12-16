package com.eltiland.model.webinar;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.WebinarCourseItem;
import com.eltiland.model.file.File;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Webinar entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "webinar", schema = "public")
@Indexed
public class Webinar extends AbstractIdentifiable implements Serializable {

    public enum Status {OPENED, CLOSED, DELETED}

    private String name;
    private String description;
    private String shortDesc;
    private String managername;
    private String managersurname;
    private String password;
    private int duration;
    private Date startDate;
    private Date registrationDeadline;
    private BigDecimal price;
    private Long webinarid;
    private Set<WebinarUserPayment> webinarUserPayments = new HashSet<>(0);
    private Status status;
    private boolean approved;
    private boolean course;
    private WebinarCourseItem courseItem;
    private WebinarRecord record;
    private boolean certSended;
    private Set<File> files = new HashSet<>(0);
    private Set<WebinarMultiplyPayment> multiplyPayments = new HashSet<>(0);
    private Boolean needConfirm;

    @Column(name = "name", nullable = false, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = false, length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "shortDesc", nullable = false, length = 1024)
    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    @Column(name = "startDate", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "registrationDeadline", nullable = false)
    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "webinar")
    public Set<WebinarUserPayment> getWebinarUserPayments() {
        return webinarUserPayments;
    }

    public void setWebinarUserPayments(Set<WebinarUserPayment> webinarUserPayments) {
        this.webinarUserPayments = webinarUserPayments;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "duration", nullable = false)
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "webinarid")
    @Field
    public Long getWebinarid() {
        return webinarid;
    }

    public void setWebinarid(Long webinarid) {
        this.webinarid = webinarid;
    }

    @Column(name = "managername", nullable = false, length = 255)
    public String getManagername() {
        return managername;
    }

    public void setManagername(String managername) {
        this.managername = managername;
    }

    @Column(name = "managersurname", nullable = false, length = 255)
    public String getManagersurname() {
        return managersurname;
    }

    public void setManagersurname(String managersurname) {
        this.managersurname = managersurname;
    }

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "approved", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @OneToOne(mappedBy = "webinar")
    public WebinarCourseItem getCourseItem() {
        return courseItem;
    }

    public void setCourseItem(WebinarCourseItem courseItem) {
        this.courseItem = courseItem;
    }

    @OneToOne(mappedBy = "webinar")
    public WebinarRecord getRecord() {
        return record;
    }

    public void setRecord(WebinarRecord record) {
        this.record = record;
    }

    @Column(name = "course", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCourse() {
        return course;
    }

    public void setCourse(boolean course) {
        this.course = course;
    }

    @Column(name = "cert_sended", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCertSended() {
        return certSended;
    }

    public void setCertSended(boolean certSended) {
        this.certSended = certSended;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "webinar")
    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "webinar")
    public Set<WebinarMultiplyPayment> getMultiplyPayments() {
        return multiplyPayments;
    }

    public void setMultiplyPayments(Set<WebinarMultiplyPayment> multiplyPayments) {
        this.multiplyPayments = multiplyPayments;
    }

    @Column(name = "confirm", nullable = false, columnDefinition = "boolean default FALSE")
    public Boolean isNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
    }
}
