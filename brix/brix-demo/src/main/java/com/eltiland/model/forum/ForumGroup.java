package com.eltiland.model.forum;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for forum group.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "group", schema = "forum")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "_prototype", discriminatorType = DiscriminatorType.STRING)
public abstract class ForumGroup extends AbstractIdentifiable {
    private String name;
    private Set<Forum> forumSet = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "forumgroup")
    public Set<Forum> getForumSet() {
        return forumSet;
    }

    public void setForumSet(Set<Forum> forumSet) {
        this.forumSet = forumSet;
    }
}
