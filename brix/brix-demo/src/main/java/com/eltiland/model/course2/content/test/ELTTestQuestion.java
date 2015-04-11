package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for test question.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_question", schema = "course")
public class ELTTestQuestion extends AbstractIdentifiable {
    private ELTTestCourseItem item;
    private String name;
    private boolean section;
    private Long index;
    private ELTTestQuestion parent;

    private Set<ELTTestQuestion> questions = new HashSet<>(0);
    private Set<ELTTestVariant> variants = new HashSet<>(0);
    private Set<ELTTestResult> results = new HashSet<>(0);
    private Set<ELTTestJump> jumps = new HashSet<>(0);
    private Set<ELTTestJumpOrder> orders = new HashSet<>(0);
    private Set<ELTTestAnswer> answers = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    public ELTTestCourseItem getItem() {
        return item;
    }

    public void setItem(ELTTestCourseItem item) {
        this.item = item;
    }

    @Column(name = "name", length = 1024)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "section", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ELTTestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<ELTTestQuestion> questions) {
        this.questions = questions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ELTTestVariant> getVariants() {
        return variants;
    }

    public void setVariants(Set<ELTTestVariant> variants) {
        this.variants = variants;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    public Set<ELTTestResult> getResults() {
        return results;
    }

    public void setResults(Set<ELTTestResult> results) {
        this.results = results;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    public ELTTestQuestion getParent() {
        return parent;
    }

    public void setParent(ELTTestQuestion parent) {
        this.parent = parent;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dest")
    public Set<ELTTestJump> getJumps() {
        return jumps;
    }

    public void setJumps(Set<ELTTestJump> jumps) {
        this.jumps = jumps;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    public Set<ELTTestJumpOrder> getOrders() {
        return orders;
    }

    public void setOrders(Set<ELTTestJumpOrder> orders) {
        this.orders = orders;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    public Set<ELTTestAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<ELTTestAnswer> answers) {
        this.answers = answers;
    }
}
