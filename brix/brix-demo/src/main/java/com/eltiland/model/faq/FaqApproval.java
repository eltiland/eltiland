package com.eltiland.model.faq;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Entity for questions/answers, which are under approval.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "faqapproval", schema = "public")
public class FaqApproval extends AbstractIdentifiable {
    private String question;
    private String answer;
    private String userEMail;
    private Date creationdate;
    private boolean answered;

    @Column(name = "user_email", nullable = false)
    public String getUserEMail() {
        return userEMail;
    }

    public void setUserEMail(String userEMail) {
        this.userEMail = userEMail;
    }

    @Column(name = "answer", length = 2048, nullable = true)
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Column(name = "question", length = 2048, nullable = false)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Column(name = "creationdate", nullable = false)
    public Date getCreationDate() {
        return creationdate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationdate = creationDate;
    }

    @Column(name = "is_answered", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
