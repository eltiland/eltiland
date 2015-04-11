package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.listeners.ELTCourseListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for the test passing statistics.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_statistics", schema = "course")
public class ELTTestStatistics extends AbstractIdentifiable {
    private ELTTestCourseItem item;
    private int limit;
    private ELTCourseListener listener;

    private Set<ELTTestAttempt> attemptSet = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    public ELTTestCourseItem getItem() {
        return item;
    }

    public void setItem(ELTTestCourseItem item) {
        this.item = item;
    }

    @Column(name = "limit", nullable = false)
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener", nullable = false)
    public ELTCourseListener getListener() {
        return listener;
    }

    public void setListener(ELTCourseListener listener) {
        this.listener = listener;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "statistics")
    public Set<ELTTestAttempt> getAttemptSet() {
        return attemptSet;
    }

    public void setAttemptSet(Set<ELTTestAttempt> attemptSet) {
        this.attemptSet = attemptSet;
    }
}
