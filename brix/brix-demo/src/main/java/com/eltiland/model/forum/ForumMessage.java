package com.eltiland.model.forum;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for forum message.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "message", schema = "forum")
public class ForumMessage extends AbstractIdentifiable {
    private User author;
    private Date date;
    private ForumThread thread;
    private ForumMessage parent;
    private Set<ForumMessage> children = new HashSet<>(0);
    private String header;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread")
    public ForumThread getThread() {
        return thread;
    }

    public void setThread(ForumThread thread) {
        this.thread = thread;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public ForumMessage getParent() {
        return parent;
    }

    public void setParent(ForumMessage parent) {
        this.parent = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ForumMessage> getChildren() {
        return children;
    }

    public void setChildren(Set<ForumMessage> children) {
        this.children = children;
    }

    @Column(name = "header", nullable = false, length = 256)
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
