package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Jump order.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_jump_order", schema = "course")
public class ELTTestJumpOrder extends AbstractIdentifiable {
    private ELTTestJump jump;
    private Long index;
    private ELTTestQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jump", nullable = false)
    public ELTTestJump getJump() {
        return jump;
    }

    public void setJump(ELTTestJump jump) {
        this.jump = jump;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question", nullable = false)
    public ELTTestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ELTTestQuestion question) {
        this.question = question;
    }
}
