package com.eltiland.model.course.test;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Results for tests.
 */
@Entity
@Table(name = "course_test_result", schema = "public")
public class TestResult extends TestEntity implements Serializable {
    private String result;
    private int minValue;
    private int maxValue;
    private TestCourseItem item;
    private TestQuestion question;
    private boolean jumpFinish;
    private Set<TestJump> jumps = new HashSet<>(0);
    private boolean rightResult;

    private static final int VALUE_LEN = 1024;

    @Column(name = "value", length = VALUE_LEN)
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Column(name = "min", nullable = false)
    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Column(name = "max", nullable = false)
    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public TestCourseItem getItem() {
        return item;
    }

    public void setItem(TestCourseItem item) {
        this.item = item;
    }

    @Override
    @Transient
    public String getTextValue() {
        return getResult();
    }

    @Override
    @Transient
    public void setTextValue(String value) {
        setResult(value);
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

    @Override
    @Transient
    public int getNumber() {
        return 0;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question")
    public TestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(TestQuestion question) {
        this.question = question;
    }

    @Column(name = "finish", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isJumpFinish() {
        return jumpFinish;
    }

    public void setJumpFinish(boolean jumpFinish) {
        this.jumpFinish = jumpFinish;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "result")
    public Set<TestJump> getJumps() {
        return jumps;
    }

    public void setJumps(Set<TestJump> jumps) {
        this.jumps = jumps;
    }

    @Column(name = "right_result", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isRightResult() {
        return rightResult;
    }

    public void setRightResult(boolean rightResult) {
        this.rightResult = rightResult;
    }
}
