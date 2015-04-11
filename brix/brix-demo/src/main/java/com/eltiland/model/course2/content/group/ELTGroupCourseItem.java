package com.eltiland.model.course2.content.group;

import com.eltiland.model.course2.content.ELTCourseItem;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Course item - group.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("GROUP")
public class ELTGroupCourseItem extends ELTCourseItem {
    private Set<ELTCourseItem> items = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ELTCourseItem> getItems() {
        return items;
    }

    public void setItems(Set<ELTCourseItem> items) {
        this.items = items;
    }
}
