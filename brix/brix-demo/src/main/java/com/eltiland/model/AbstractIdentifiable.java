package com.eltiland.model;

import org.hibernate.search.annotations.Field;

import javax.persistence.*;

/**
 * Abstract class to enable an object to be identifiable.
 *
 * @author Aleksey Plotnikov
 */
@MappedSuperclass
public abstract class AbstractIdentifiable implements Identifiable {
    private Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "_id", unique = true, nullable = false, precision = 20, scale = 0, columnDefinition = "numeric")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractIdentifiable that = (AbstractIdentifiable) o;

        return !(id != null ? !id.equals(that.id) : !super.equals(that));
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
