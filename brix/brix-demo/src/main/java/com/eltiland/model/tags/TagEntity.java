package com.eltiland.model.tags;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Tag-entity M_M link.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "tag_entity", schema = "tags")
public class TagEntity extends AbstractIdentifiable {
    private Long tag;
    private Long entity;

    @Column(name = "tag_id", nullable = false)
    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    @Column(name = "entity_id", nullable = false)
    public Long getEntity() {
        return entity;
    }

    public void setEntity(Long entity) {
        this.entity = entity;
    }
}
