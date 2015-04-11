package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User test attempt.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_attempt", schema = "course")
public class ELTTestAttempt extends AbstractIdentifiable {
    private ELTTestStatistics statistics;
    private Date startDate;

    private Set<ELTTestAnswer> answerSet = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statistics", nullable = false)
    public ELTTestStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(ELTTestStatistics statistics) {
        this.statistics = statistics;
    }

    @Column(name = "start_date", nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attempt")
    public Set<ELTTestAnswer> getAnswerSet() {
        return answerSet;
    }

    public void setAnswerSet(Set<ELTTestAnswer> answerSet) {
        this.answerSet = answerSet;
    }
}
