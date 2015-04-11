package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for training course user.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_listener", schema = "public")
public class CourseListener extends AbstractIdentifiable {

    public enum Status {NEW, APPROVED, PAYS, CONFIRMED}

    public enum Kind {PHYSICAL, LEGAL, MOSCOW}

    private User listener;
    private Status status;
    private Kind kind;
    private String offer;
    private CourseSession session;
    private File document;
    private String requisites;
    private boolean completed;
    private File authorDocument;

    private Set<User> users = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener")
    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "kind", nullable = false)
    @Enumerated(value = EnumType.STRING)
    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    @Column(name = "offer")
    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session")
    public CourseSession getSession() {
        return session;
    }

    public void setSession(CourseSession session) {
        this.session = session;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_listener_user", schema = "public",
            joinColumns = @JoinColumn(name = "listener_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document")
    public File getDocument() {
        return document;
    }

    public void setDocument(File document) {
        this.document = document;
    }

    @Column(name = "requisites", length = 4096)
    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_document")
    public File getAuthorDocument() {
        return authorDocument;
    }

    public void setAuthorDocument(File authorDocument) {
        this.authorDocument = authorDocument;
    }
}
