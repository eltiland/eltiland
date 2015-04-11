package com.eltiland.model.course.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Jumps between questions in the test.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_test_jump", schema = "public")
public class TestJump extends AbstractIdentifiable implements Serializable {
    private TestCourseItem item;
    private TestQuestion dest;
    private TestResult result;
    private Set<TestJumpOrder> prevs = new HashSet<>(0);
    private int jumpOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public TestCourseItem getItem() {
        return item;
    }

    public void setItem(TestCourseItem item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dest")
    public TestQuestion getDest() {
        return dest;
    }

    public void setDest(TestQuestion dest) {
        this.dest = dest;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result")
    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jump")
    public Set<TestJumpOrder> getPrevs() {
        return prevs;
    }

    public void setPrevs(Set<TestJumpOrder> prevs) {
        this.prevs = prevs;
    }

    @Column(name = "jump_order", nullable = false, columnDefinition = "INTEGER DEFAULT '0'")
    public int getJumpOrder() {
        return jumpOrder;
    }

    public void setJumpOrder(int jumpOrder) {
        this.jumpOrder = jumpOrder;
    }
}
