package com.eltiland.model.user;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.IWithAvatar;
import com.eltiland.model.Pei;
import com.eltiland.model.file.File;
import com.eltiland.model.search.ChildSearchFilterFactory;
import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.Years;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Child entity.
 *
 * @author Aleksey Plotnikov
 */
@Entity
@Table(name = "child", schema = "public")
@FullTextFilterDefs({
        @FullTextFilterDef(name = "childSearchFilterFactory", impl = ChildSearchFilterFactory.class)
})
@Indexed
public class Child extends AbstractIdentifiable implements IWithAvatar {

    private String name;
    private Date birthDate;
    private User parent;
    private File avatar;

    private Set<Pei> peis = new HashSet<>(0);

    @Column(name = "name", nullable = false)
    @Field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public int getAge() {
        return getAgeFor(getBirthDate());
    }

    public static int getAgeFor(Date birthDate) {
        return Years.yearsBetween(new DateTime(birthDate), DateTime.now()).getYears();
    }

    @Temporal(value = TemporalType.DATE)
    @Column(name = "birth_date", nullable = false)
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    @DateBridge(resolution = Resolution.DAY)
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @IndexedEmbedded(prefix = "parent:", depth = 1)
    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    @OneToOne(fetch = FetchType.LAZY)
    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "child_pei", schema = "public",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "pei_id")
    )
    @IndexedEmbedded(prefix = "pei:")
    public Set<Pei> getPeis() {
        return peis;
    }

    public void setPeis(Set<Pei> peis) {
        this.peis = peis;
    }

    /**
     * Synthetic class for represent child search criteria.
     */
    public static class ChildSearchCriteria {
        private String searchQuery;
        private Pei pei;
        private Set<Child> excludeChildren = new HashSet<>(0);

        public String getSearchQuery() {
            return searchQuery;
        }

        public void setSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        public Pei getPei() {
            return pei;
        }

        public void setPei(Pei pei) {
            this.pei = pei;
        }

        public Set<Child> getExcludeChildren() {
            return excludeChildren;
        }

        public void setExcludeChildren(Set<Child> excludeChildren) {
            this.excludeChildren = excludeChildren;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChildSearchCriteria that = (ChildSearchCriteria) o;

            if (excludeChildren != null ? !excludeChildren.equals(that.excludeChildren) : that.excludeChildren != null)
                return false;
            if (pei != null ? !pei.equals(that.pei) : that.pei != null) return false;
            if (searchQuery != null ? !searchQuery.equals(that.searchQuery) : that.searchQuery != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = searchQuery != null ? searchQuery.hashCode() : 0;
            result = 31 * result + (pei != null ? pei.hashCode() : 0);
            result = 31 * result + (excludeChildren != null ? excludeChildren.hashCode() : 0);
            return result;
        }
    }
}
