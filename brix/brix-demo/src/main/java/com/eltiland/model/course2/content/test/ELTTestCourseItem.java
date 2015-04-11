package com.eltiland.model.course2.content.test;

import com.eltiland.model.course2.content.ELTCourseItem;

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
public class ELTTestCourseItem extends ELTCourseItem {
    private Set<ELTTestQuestion> questions = new HashSet<>(0);
    private Set<ELTTestVariant> variants = new HashSet<>(0);
    private Set<ELTTestResult> results = new HashSet<>(0);
    private Set<ELTTestStatistics> statistics = new HashSet<>(0);

    private int limit;
    private int minutes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<ELTTestQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<ELTTestQuestion> questions) {
        this.questions = questions;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<ELTTestVariant> getVariants() {
        return variants;
    }

    public void setVariants(Set<ELTTestVariant> variants) {
        this.variants = variants;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<ELTTestResult> getResults() {
        return results;
    }

    public void setResults(Set<ELTTestResult> results) {
        this.results = results;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<ELTTestStatistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(Set<ELTTestStatistics> statistics) {
        this.statistics = statistics;
    }

    @Column(name = "time_limit")
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Column(name = "minutes")
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
