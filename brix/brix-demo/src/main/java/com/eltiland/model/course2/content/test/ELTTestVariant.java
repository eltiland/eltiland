package com.eltiland.model.course2.content.test;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity for test variant of the answers.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "test_variant", schema = "course")
public class ELTTestVariant extends AbstractIdentifiable {
    private ELTTestCourseItem item;
    private ELTTestQuestion parent;
    private String text;
    private Long index;
    private Long score;

    private Set<ELTTestAnswer> answers = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", nullable = false)
    public ELTTestCourseItem getItem() {
        return item;
    }

    public void setItem(ELTTestCourseItem item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent", nullable = false)
    public ELTTestQuestion getParent() {
        return parent;
    }

    public void setParent(ELTTestQuestion parent) {
        this.parent = parent;
    }

    @Column(name = "text", length = 1024)
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Column(name = "index", nullable = false)
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @Column(name = "score", nullable = false)
    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "variant")
    public Set<ELTTestAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<ELTTestAnswer> answers) {
        this.answers = answers;
    }
}
