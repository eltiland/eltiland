package com.eltiland.model.course.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Jumps between questions in the test.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_test_jump_order", schema = "public")
public class TestJumpOrder extends AbstractIdentifiable implements Serializable {
    private TestJump jump;
    private TestQuestion question;
    private int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jump")
    public TestJump getJump() {
        return jump;
    }

    public void setJump(TestJump jump) {
        this.jump = jump;
    }

    @Column(name = "jump_order")
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question")
    public TestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(TestQuestion question) {
        this.question = question;
    }
}
