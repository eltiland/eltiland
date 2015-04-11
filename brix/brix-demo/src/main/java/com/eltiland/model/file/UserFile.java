package com.eltiland.model.file;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User's file item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "user_file", schema = "public")
public class UserFile extends AbstractIdentifiable {
    private User owner;
    private File file;
    private Date uploadDate;

    private Set<User> destinations = new HashSet<>(0);
    private Set<ELTCourse> courses = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file", nullable = false)
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "availableFiles")
    public Set<User> getDestinations() {
        return destinations;
    }

    public void setDestinations(Set<User> destinations) {
        this.destinations = destinations;
    }


    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "files")
    public Set<ELTCourse> getCourses() {
        return courses;
    }

    public void setCourses(Set<ELTCourse> courses) {
        this.courses = courses;
    }

    @Column(name = "upload_date", nullable = false)
    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
