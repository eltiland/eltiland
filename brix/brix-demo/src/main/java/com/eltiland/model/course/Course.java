package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.file.File;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Course entity
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course", schema = "public")
public class Course extends AbstractIdentifiable implements Serializable {
    private String name;
    private String video;
    private User author;
    private boolean status;
    private boolean published;
    private BigDecimal price;
    private File icon;
    private Date creationDate;
    private boolean preJoin;
    private boolean autoJoin;
    private boolean fullAccess;
    private GoogleDriveFile startPage;
    private String supportMail;
    private boolean training;
    private boolean migrated;

    private Set<User> listeners = new HashSet<>(0);
    private Set<CourseItem> demoVersion = new HashSet<>(0);
    private Set<CourseItem> fullVersion = new HashSet<>(0);
    private Set<CoursePaidInvoice> invoices = new HashSet<>(0);
    private Set<CourseInvoice> accessInvoices = new HashSet<>(0);
    private Set<CourseSession> sessions = new HashSet<>(0);
    private Set<CourseUserData> dataSet = new HashSet<>(0);

    private Forum forum;

    @Column(name = "creationDate")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "prejoin")
    public boolean isPreJoin() {
        return preJoin;
    }

    public void setPreJoin(boolean preJoin) {
        this.preJoin = preJoin;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<CoursePaidInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(Set<CoursePaidInvoice> invoices) {
        this.invoices = invoices;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum")
    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    @Column(name = "autojoin", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isAutoJoin() {
        return autoJoin;
    }

    public void setAutoJoin(boolean autoJoin) {
        this.autoJoin = autoJoin;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<CourseInvoice> getAccessInvoices() {
        return accessInvoices;
    }

    public void setAccessInvoices(Set<CourseInvoice> accessInvoices) {
        this.accessInvoices = accessInvoices;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startPage")
    public GoogleDriveFile getStartPage() {
        return startPage;
    }

    public void setStartPage(GoogleDriveFile startPage) {
        this.startPage = startPage;
    }

    @Column(name = "full_access")
    public boolean isFullAccess() {
        return fullAccess;
    }

    public void setFullAccess(boolean fullAccess) {
        this.fullAccess = fullAccess;
    }

    @Column(name = "support_mail", length = 128)
    public String getSupportMail() {
        return supportMail;
    }

    public void setSupportMail(String supportMail) {
        this.supportMail = supportMail;
    }

    @Column(name = "training")
    public boolean isTraining() {
        return training;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<CourseSession> getSessions() {
        return sessions;
    }

    public void setSessions(Set<CourseSession> sessions) {
        this.sessions = sessions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<CourseUserData> getDataSet() {
        return dataSet;
    }

    public void setDataSet(Set<CourseUserData> dataSet) {
        this.dataSet = dataSet;
    }

    @Column(name = "migrated", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    /**
     * Enum for kind of the content.
     * DEMO - demo version , FULL - full version.
     */
    public static enum CONTENT_KIND {
        DEMO,
        FULL
    }

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "courses")
    public Set<User> getListeners() {
        return listeners;
    }

    public void setListeners(Set<User> listeners) {
        this.listeners = listeners;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courseDemo")
    public Set<CourseItem> getDemoVersion() {
        return demoVersion;
    }

    public void setDemoVersion(Set<CourseItem> demoVersion) {
        this.demoVersion = demoVersion;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "courseFull")
    public Set<CourseItem> getFullVersion() {
        return fullVersion;
    }

    public void setFullVersion(Set<CourseItem> fullVersion) {
        this.fullVersion = fullVersion;
    }

    @Column(name = "status", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Column(name = "published", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public File getIcon() {
        return icon;
    }

    public void setIcon(File icon) {
        this.icon = icon;
    }

    @Column(name = "video", length = 1024)
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
