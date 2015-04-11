package com.eltiland.model.course.test;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.user.User;

import javax.persistence.*;

/**
 * User/TestCourseItem relation.
 */
@Entity
@Table(name = "user_test_attempt", schema = "public")
public class UserTestAttempt extends AbstractIdentifiable {
    private User user;
    private TestCourseItem test;
    private int attemptCount;
    private int attemptLimit;
    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    public TestCourseItem getTest() {
        return test;
    }

    public void setTest(TestCourseItem test) {
        this.test = test;
    }

    @Column(name = "attempt_count", nullable = false)
    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    @Column(name = "attempt_limit", nullable = false)
    public int getAttemptLimit() {
        return attemptLimit;
    }

    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
