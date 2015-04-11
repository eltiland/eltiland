package com.eltiland.model.course.test;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Questions for tests.
 */
@Entity
@Table(name = "course_test_question", schema = "public")
public class TestQuestion extends TestEntity implements Serializable {
    private TestCourseItem item;
    private TestQuestion parentItem;
    private Set<TestQuestion> children = new HashSet<>(0);
    private Set<TestVariant> variants = new HashSet<>(0);
    private Set<TestResult> results = new HashSet<>(0);
    private Set<TestJump> jumps = new HashSet<>(0);
    private Set<TestJumpOrder> orders = new HashSet<>(0);
    private String text;
    private int number;
    private boolean section;

    private static final int VALUE_LEN = 1024;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public TestCourseItem getItem() {
        return item;
    }

    public void setItem(TestCourseItem item) {
        this.item = item;
    }

    @Column(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    @Transient
    public String getTextValue() {
        return getText();
    }

    @Override
    @Transient
    public void setTextValue(String value) {
        setText(value);
    }

    @Override
    @Transient
    public TestCourseItem getTestItem() {
        return getItem();
    }

    @Override
    @Transient
    public void setTestItem(TestCourseItem item) {
        setItem(item);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentItem")
    public TestQuestion getParentItem() {
        return parentItem;
    }

    public void setParentItem(TestQuestion parentItem) {
        this.parentItem = parentItem;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentItem")
    public Set<TestQuestion> getChildren() {
        return children;
    }

    public void setChildren(Set<TestQuestion> children) {
        this.children = children;
    }

    @Column(name = "section", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isSection() {
        return section;
    }

    public void setSection(boolean section) {
        this.section = section;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    public Set<TestVariant> getVariants() {
        return variants;
    }

    public void setVariants(Set<TestVariant> variants) {
        this.variants = variants;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    public Set<TestResult> getResults() {
        return results;
    }

    public void setResults(Set<TestResult> results) {
        this.results = results;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dest")
    public Set<TestJump> getJumps() {
        return jumps;
    }

    public void setJumps(Set<TestJump> jumps) {
        this.jumps = jumps;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    public Set<TestJumpOrder> getOrders() {
        return orders;
    }

    public void setOrders(Set<TestJumpOrder> orders) {
        this.orders = orders;
    }
}
