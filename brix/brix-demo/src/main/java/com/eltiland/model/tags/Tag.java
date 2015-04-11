package com.eltiland.model.tags;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Tag entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "tag", schema = "tags")
public class Tag extends AbstractIdentifiable implements Serializable {
    private String name;
    private TagCategory category;

    @Column(name = "name", nullable = false, length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    public TagCategory getCategory() {
        return category;
    }

    public void setCategory(TagCategory category) {
        this.category = category;
    }
}
