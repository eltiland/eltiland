package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for test results for the question/sections of the questions.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_result", schema = "course")
public class ELTTestResult extends AbstractIdentifiable {
    private ELTTestCourseItem item;
    private ELTTestQuestion parent;
    private String text;
    private boolean correct;
    private int min;
    private int max;
    private boolean jumpFinish;

    private Set<ELTTestJump> jumps = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    public ELTTestCourseItem getItem() {
        return item;
    }

    public void setItem(ELTTestCourseItem item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public ELTTestQuestion getParent() {
        return parent;
    }

    public void setParent(ELTTestQuestion parent) {
        this.parent = parent;
    }

    @Column(name = "text", length = 1024)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "correct", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Column(name = "min", nullable = false)
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Column(name = "max", nullable = false)
    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Column(name = "jump_finish", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isJumpFinish() {
        return jumpFinish;
    }

    public void setJumpFinish(boolean jumpFinish) {
        this.jumpFinish = jumpFinish;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "result")
    public Set<ELTTestJump> getJumps() {
        return jumps;
    }

    public void setJumps(Set<ELTTestJump> jumps) {
        this.jumps = jumps;
    }
}
