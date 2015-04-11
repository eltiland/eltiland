package com.eltiland.model.forum;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course.Course;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for forum group.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "forum", schema = "forum")
public class Forum extends AbstractIdentifiable {
    private ForumGroup forumgroup;
    private String name;
    private String description;
    private Set<ForumThread> threads = new HashSet<>(0);
    private Course course;

    @Column(name = "description", length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forumgroup")
    public ForumGroup getForumgroup() {
        return forumgroup;
    }

    public void setForumgroup(ForumGroup forumgroup) {
        this.forumgroup = forumgroup;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "forum")
    public Set<ForumThread> getThreads() {
        return threads;
    }

    public void setThreads(Set<ForumThread> threads) {
        this.threads = threads;
    }

    @OneToOne(mappedBy = "forum")
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
