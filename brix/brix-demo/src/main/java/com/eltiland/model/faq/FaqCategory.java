package com.eltiland.model.faq;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Category for Question/Answer record
 *
 * @author Pavel Androschuk.
 */

@Entity
@Table(name = "faq_category", schema = "public")
public class FaqCategory extends AbstractIdentifiable {
    private String name;
    private int number;
    private Set<Faq> faqs = new HashSet<>(0);

    @Column(name = "number", nullable = false)
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Column(name = "name", nullable = false, length = 80)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    public Set<Faq> getFaqs() {
        return faqs;
    }

    public void setFaqs(Set<Faq> faqs) {
        this.faqs = faqs;
    }

    public String toString() {
        return getName();
    }
}
