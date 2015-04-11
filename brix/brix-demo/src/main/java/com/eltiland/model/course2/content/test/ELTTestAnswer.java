package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.Date;

/**
 * User test answers.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_answer", schema = "course")
public class ELTTestAnswer extends AbstractIdentifiable {
    private ELTTestQuestion question;
    private ELTTestVariant variant;
    private ELTTestAttempt attempt;
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question", nullable = false)
    public ELTTestQuestion getQuestion() {
        return question;
    }

    public void setQuestion(ELTTestQuestion question) {
        this.question = question;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant", nullable = false)
    public ELTTestVariant getVariant() {
        return variant;
    }

    public void setVariant(ELTTestVariant variant) {
        this.variant = variant;
    }

    @Column(name = "date", nullable = false)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt", nullable = false)
    public ELTTestAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(ELTTestAttempt attempt) {
        this.attempt = attempt;
    }
}
