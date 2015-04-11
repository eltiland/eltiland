package com.eltiland.model.course2.content.video;

import com.eltiland.model.course2.content.ELTCourseItem;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Course item with youtube videos.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("VIDEO")
public class ELTVideoCourseItem extends ELTCourseItem {
    private Set<ELTVideoItem> items = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<ELTVideoItem> getItems() {
        return items;
    }

    public void setItems(Set<ELTVideoItem> items) {
        this.items = items;
    }
}
