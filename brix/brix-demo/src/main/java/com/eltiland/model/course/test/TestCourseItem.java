package com.eltiland.model.course.test;

import com.eltiland.model.course.ElementCourseItem;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Test course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("TEST")
public class TestCourseItem extends ElementCourseItem {
    private Set<TestVariant> variants = new HashSet<>(0);
    private Set<TestResult> results = new HashSet<>(0);
    private Set<TestQuestion> questions = new HashSet<>(0);
    private Set<TestJump> jumps = new HashSet<>(0);
    private int attemptLimit;
    private Set<User> users = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<TestVariant> getVariants() {
        return variants;
    }

    public void setVariants(Set<TestVariant> variants) {
        this.variants = variants;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<TestResult> getResults() {
        return results;
    }

    public void setResults(Set<TestResult> results) {
        this.results = results;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<TestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<TestQuestion> questions) {
        this.questions = questions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<TestJump> getJumps() {
        return jumps;
    }

    public void setJumps(Set<TestJump> jumps) {
        this.jumps = jumps;
    }

    @Column(name = "test_limit")
    public int getAttemptLimit() {
        return attemptLimit;
    }

    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "testCourseItems")
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
