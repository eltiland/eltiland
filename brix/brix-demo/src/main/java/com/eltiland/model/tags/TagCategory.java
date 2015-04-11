package com.eltiland.model.tags;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Tag category entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "tag_category", schema = "tags")
public class TagCategory extends AbstractIdentifiable implements Serializable {
    private String name;
    private String entity;
    private Set<Tag> tags = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @Column(name = "entity", nullable = false, length = 64)
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
