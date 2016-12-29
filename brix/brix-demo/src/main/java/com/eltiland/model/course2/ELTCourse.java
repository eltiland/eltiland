package com.eltiland.model.course2;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.course2.listeners.ELTCourseUserData;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract entity (course).
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course", schema = "course")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_prototype", discriminatorType = DiscriminatorType.STRING)
public abstract class ELTCourse extends AbstractIdentifiable {
    private String name;
    private GoogleDriveFile startPage;
    private User author;
    private Date creationDate;
    private String supportEmail;
    private String video;
    private CourseStatus status;
    private File icon;
    private Boolean needConfirm;
    private BigDecimal price;
    private Long days;
    private TestCourseItem test;

    private Set<User> admins = new HashSet<>(0);
    private Set<ELTCourseBlock> content = new HashSet<>(0);
    private Set<ELTCourseListener> listeners = new HashSet<>(0);
    private Set<ELTCourseUserData> userDataSet = new HashSet<>(0);
    private Set<UserFile> files = new HashSet<>(0);


    @Column(name = "name", nullable = false, unique = true, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_page", unique = true)
    public GoogleDriveFile getStartPage() {
        return startPage;
    }

    public void setStartPage(GoogleDriveFile startPage) {
        this.startPage = startPage;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "creation_date", nullable = false)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "video", length = 128)
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Column(name = "support_email", length = 128)
    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(value = EnumType.STRING)
    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icon")
    public File getIcon() {
        return icon;
    }

    public void setIcon(File icon) {
        this.icon = icon;
    }

    @Column(name = "need_confirm", nullable = false)
    public Boolean isNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
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
            name = "admins", schema = "course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<User> admins) {
        this.admins = admins;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<ELTCourseBlock> getContent() {
        return content;
    }

    public void setContent(Set<ELTCourseBlock> content) {
        this.content = content;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<ELTCourseListener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<ELTCourseListener> listeners) {
        this.listeners = listeners;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    public Set<ELTCourseUserData> getUserDataSet() {
        return userDataSet;
    }

    public void setUserDataSet(Set<ELTCourseUserData> userDataSet) {
        this.userDataSet = userDataSet;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_file_access", schema = "public",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    public Set<UserFile> getFiles() {
        return files;
    }

    public void setFiles(Set<UserFile> files) {
        this.files = files;
    }

    @Column(name = "days")
    public Long getDays() {
        return days;
    }

    public void setDays(Long days) {
        this.days = days;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test")
    public TestCourseItem getTest() {
        return test;
    }

    public void setTest(TestCourseItem test) {
        this.test = test;
    }
}
