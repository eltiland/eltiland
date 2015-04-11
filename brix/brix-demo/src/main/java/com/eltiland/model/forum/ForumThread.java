package com.eltiland.model.forum;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for forum thread.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "thread", schema = "forum")
public class ForumThread extends AbstractIdentifiable {
    private User author;
    private String name;
    private Set<ForumMessage> messages = new HashSet<>(0);
    private Forum forum;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Column(name = "name", nullable = false, length = 256)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "thread")
    public Set<ForumMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<ForumMessage> messages) {
        this.messages = messages;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum")
    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
