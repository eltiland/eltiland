package com.eltiland.model.user;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.IWithAvatar;
import com.eltiland.model.Video;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.TaskCourseItem;
import com.eltiland.model.course.paidservice.CoursePayment;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.forum.ForumMessage;
import com.eltiland.model.forum.ForumThread;
import com.eltiland.model.library.LibraryRecord;
import com.eltiland.model.search.UserTrainingCourseFactory;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main abstract user entity.
 *
 * @author Aleksey Plotnikov
 */
@Entity
@Table(name = "user", schema = "public")
@FullTextFilterDefs({
        @FullTextFilterDef(name = "userTrainingCourseFactory", impl = UserTrainingCourseFactory.class)
})
@Indexed
public class User extends AbstractIdentifiable implements Serializable, IWithAvatar {
    private String email;
    private String name;
    private String password;
    private String phone;
    private String skype;
    private Confirmation confirmation;
    private ResetCode resetcode;
    private Date confirmationDate;
    private File avatar;
    private String address;
    private String information;
    private String achievements;
    private boolean superUser = false;
    private String appointment;
    private String organization;
    private Integer experience;

    private boolean isActive;
    private boolean isChecked;

    private Set<Course> authCourses = new HashSet<>(0);
    private Set<Course> courses = new HashSet<>(0);
    private Set<TaskCourseItem> completedTasks = new HashSet<>(0);
    private Set<Video> videos = new HashSet<>(0);
    private Set<WebinarUserPayment> webinarUserPayments = new HashSet<>(0);
    private Set<CoursePayment> coursePayments = new HashSet<>(0);
    private Set<ForumThread> forumThreads = new HashSet<>(0);
    private Set<ForumMessage> forumMessages = new HashSet<>(0);
    private Set<CourseInvoice> courseInvoices = new HashSet<>(0);
    private Set<WebinarRecordPayment> webinarRecordPayments = new HashSet<>(0);
    private Set<WebinarMultiplyPayment> webinarMultiplyPayments = new HashSet<>(0);
    private Set<TestCourseItem> testCourseItems = new HashSet<>(0);
    private Set<LibraryRecord> libraryRecords = new HashSet<>(0);
    private Set<CourseListener> listenerSet = new HashSet<>(0);
    private Set<CourseListener> invitorSet = new HashSet<>(0);
    private Set<Child> children;

    private Set<ELTCourse> authorCourses = new HashSet<>(0);
    private Set<ELTCourse> adminCourses = new HashSet<>(0);
    private Set<ELTCourseListener> listeners = new HashSet<>(0);
    private Set<ELTCourseBlockAccess> accessSet = new HashSet<>(0);
    private Set<UserFile> userFiles = new HashSet<>(0);
    private Set<UserFile> availableFiles = new HashSet<>(0);

    @Column(name = "name", nullable = false)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email", unique = true, nullable = false)
    @Field
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public Confirmation getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Confirmation confirmation) {
        this.confirmation = confirmation;
    }

    @Column(name = "confirmationDate")
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.YES)
    @DateBridge(resolution = Resolution.DAY)
    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public ResetCode getResetcode() {
        return resetcode;
    }

    public void setResetcode(ResetCode resetcode) {
        this.resetcode = resetcode;
    }

    @Column(name = "superuser", nullable = false)
    public boolean isSuperUser() {
        return superUser;
    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }

    @Transient
    public boolean isConfirmed() {
        return getConfirmation() == null && getConfirmationDate() != null;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    public Set<Course> getAuthCourses() {
        return authCourses;
    }

    public void setAuthCourses(Set<Course> authCourses) {
        this.authCourses = authCourses;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "course_user", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "task_user", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    public Set<TaskCourseItem> getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Set<TaskCourseItem> completedTasks) {
        this.completedTasks = completedTasks;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    public Set<Video> getVideos() {
        return videos;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    public Set<WebinarUserPayment> getWebinarUserPayments() {
        return webinarUserPayments;
    }

    public void setWebinarUserPayments(Set<WebinarUserPayment> webinarUserPayments) {
        this.webinarUserPayments = webinarUserPayments;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<CoursePayment> getCoursePayments() {
        return coursePayments;
    }

    public void setCoursePayments(Set<CoursePayment> coursePayments) {
        this.coursePayments = coursePayments;
    }

    @Column(name = "skype", length = 32)
    @Field
    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    @Column(name = "address")
    @Field
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "information")
    @Field
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Column(name = "achieve")
    @Field
    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    public Set<ForumThread> getForumThreads() {
        return forumThreads;
    }

    public void setForumThreads(Set<ForumThread> forumThreads) {
        this.forumThreads = forumThreads;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    public Set<ForumMessage> getForumMessages() {
        return forumMessages;
    }

    public void setForumMessages(Set<ForumMessage> forumMessages) {
        this.forumMessages = forumMessages;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<CourseInvoice> getCourseInvoices() {
        return courseInvoices;
    }

    public void setCourseInvoices(Set<CourseInvoice> courseInvoices) {
        this.courseInvoices = courseInvoices;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile")
    public Set<WebinarRecordPayment> getWebinarRecordPayments() {
        return webinarRecordPayments;
    }

    public void setWebinarRecordPayments(Set<WebinarRecordPayment> webinarRecordPayments) {
        this.webinarRecordPayments = webinarRecordPayments;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<WebinarMultiplyPayment> getWebinarMultiplyPayments() {
        return webinarMultiplyPayments;
    }

    public void setWebinarMultiplyPayments(Set<WebinarMultiplyPayment> webinarMultiplyPayments) {
        this.webinarMultiplyPayments = webinarMultiplyPayments;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "user_test_attempt", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "test_id")
    )
    public Set<TestCourseItem> getTestCourseItems() {
        return testCourseItems;
    }

    public void setTestCourseItems(Set<TestCourseItem> testCourseItems) {
        this.testCourseItems = testCourseItems;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "publisher")
    public Set<LibraryRecord> getLibraryRecords() {
        return libraryRecords;
    }

    public void setLibraryRecords(Set<LibraryRecord> libraryRecords) {
        this.libraryRecords = libraryRecords;
    }

    @Column(name = "appointment", length = 255)
    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<CourseListener> getListenerSet() {
        return listenerSet;
    }

    public void setListenerSet(Set<CourseListener> listenerSet) {
        this.listenerSet = listenerSet;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    public Set<CourseListener> getInvitorSet() {
        return invitorSet;
    }

    public void setInvitorSet(Set<CourseListener> invitorSet) {
        this.invitorSet = invitorSet;
    }

    @Column(name = "organization", length = 255)
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<Child> getChildren() {
        return children;
    }

    public void setChildren(Set<Child> children) {
        this.children = children;
    }

    @Column(name = "active", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Column(name = "experience")
    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
    public Set<ELTCourse> getAuthorCourses() {
        return authorCourses;
    }

    public void setAuthorCourses(Set<ELTCourse> authorCourses) {
        this.authorCourses = authorCourses;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "admins")
    public Set<ELTCourse> getAdminCourses() {
        return adminCourses;
    }

    public void setAdminCourses(Set<ELTCourse> adminCourses) {
        this.adminCourses = adminCourses;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<ELTCourseListener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<ELTCourseListener> listeners) {
        this.listeners = listeners;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "listener")
    public Set<ELTCourseBlockAccess> getAccessSet() {
        return accessSet;
    }

    public void setAccessSet(Set<ELTCourseBlockAccess> accessSet) {
        this.accessSet = accessSet;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    public Set<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(Set<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_file_access", schema = "public",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    public Set<UserFile> getAvailableFiles() {
        return availableFiles;
    }

    public void setAvailableFiles(Set<UserFile> availableFiles) {
        this.availableFiles = availableFiles;
    }

    @Column(name = "checked", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }


    /**
     * Synthetic class for represent webinar user search criteria.
     */
    public static class UserTrainingCourseCriteria {
        private String searchQuery;
        private List<Long> ids;

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public List<Long> getIds() {
            return ids;
        }

        public void setIds(List<Long> ids) {
            this.ids = ids;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            User.UserTrainingCourseCriteria that = (User.UserTrainingCourseCriteria) o;

            if (ids != null ? !ids.equals(that.ids) : that.ids != null) return false;
            if (searchQuery != null ? !searchQuery.equals(that.searchQuery) : that.searchQuery != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = searchQuery != null ? searchQuery.hashCode() : 0;
            result = 31 * result + (ids != null ? ids.hashCode() : 0);
            return result;
        }
    }
}