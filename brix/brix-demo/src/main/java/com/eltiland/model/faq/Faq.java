package com.eltiland.model.faq;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;

/**
 * Question/Answer entity
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "faq", schema = "public")
public class Faq extends AbstractIdentifiable {
    private String question;
    private String answer;
    private int number;
    private FaqCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    public FaqCategory getCategory() {
        return category;
    }

    public void setCategory(FaqCategory category) {
        this.category = category;
    }

    @Column(name = "number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Column(name = "question", length = 2048, nullable = false)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Column(name = "answer", length = 2048, nullable = false)
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
