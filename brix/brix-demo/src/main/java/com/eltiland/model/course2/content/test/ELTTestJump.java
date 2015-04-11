package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Jump between questions within test.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_jump", schema = "course")
public class ELTTestJump extends AbstractIdentifiable {
    private ELTTestResult result;
    private ELTTestQuestion dest;
    private Long index;

    private Set<ELTTestJumpOrder> orders = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result", nullable = false)
    public ELTTestResult getResult() {
        return result;
    }

    public void setResult(ELTTestResult result) {
        this.result = result;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dest", nullable = false)
    public ELTTestQuestion getDest() {
        return dest;
    }

    public void setDest(ELTTestQuestion dest) {
        this.dest = dest;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jump")
    public Set<ELTTestJumpOrder> getOrders() {
        return orders;
    }

    public void setOrders(Set<ELTTestJumpOrder> orders) {
        this.orders = orders;
    }
}
