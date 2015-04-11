package com.eltiland.model.library;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.search.LibraryCollectionSearchFilterFactory;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FullTextFilterDef;
import org.hibernate.search.annotations.FullTextFilterDefs;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * Library collection record entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "collection", schema = "library")
@FullTextFilterDefs({
        @FullTextFilterDef(name = "librarySearchFilterFactory", impl = LibraryCollectionSearchFilterFactory.class)
})
@Indexed
public class LibraryCollection extends AbstractIdentifiable implements Serializable {
    private String name;
    private String description;
    private Set<LibraryRecord> records = new HashSet<>(0);
    private LibraryCollection parent;
    private Set<LibraryCollection> subCollections = new HashSet<>(0);

    @Column(name = "name", nullable = false, length = 256)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "record_collection", schema = "library",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "record_id")
    )
    public Set<LibraryRecord> getRecords() {
        return records;
    }

    public void setRecords(Set<LibraryRecord> records) {
        this.records = records;
    }

    @Column(name = "description", length = 1024)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<LibraryCollection> getSubCollections() {
        return subCollections;
    }

    public void setSubCollections(Set<LibraryCollection> subCollections) {
        this.subCollections = subCollections;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public LibraryCollection getParent() {
        return parent;
    }

    public void setParent(LibraryCollection parent) {
        this.parent = parent;
    }

    @Transient
    @Field(name = "parentId")
    public Long getParentId() {
        if (getParent() == null) {
            return (long) 0;
        } else {
            return getParent().getId();
        }
    }

    @Transient
    @Field(name = "weight")
    public float getWeight() {
        float indexWeight = 1, weight = 0;
        byte name[] = new byte[0];
        try {
            name = getName().toLowerCase().getBytes("CP866");
        } catch (UnsupportedEncodingException ignored) {
        }
        for (byte aName : name) {
            indexWeight /= 255;
            weight += (aName & 0xff) * indexWeight;
        }
        return weight;
    }

    /**
     * Synthetic class for represent library collection search criteria.
     */
    public static class LibraryCollectionSearchCriteria {
        private LibraryCollection parent;
        private boolean topLevel;

        public boolean isTopLevel() {
            return topLevel;
        }

        public void setTopLevel(boolean topLevel) {
            this.topLevel = topLevel;
        }

        public LibraryCollection getParent() {
            return parent;
        }

        public void setParent(LibraryCollection parent) {
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LibraryCollection.LibraryCollectionSearchCriteria that =
                    (LibraryCollection.LibraryCollectionSearchCriteria) o;

            return !(parent != null ? !parent.equals(that.parent) : that.parent != null);
        }

        @Override
        public int hashCode() {
            return parent != null ? parent.hashCode() : 0;
        }
    }
}
