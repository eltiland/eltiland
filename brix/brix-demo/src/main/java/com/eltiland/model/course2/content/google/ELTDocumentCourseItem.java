package com.eltiland.model.course2.content.google;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Google document item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("DOC")
public class ELTDocumentCourseItem extends ELTGoogleCourseItem {
    private boolean printable;
    private Long printLimit;
    private Set<CourseItemPrintStat> printStatistics = new HashSet<>(0);

    @Column(name = "printable", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isPrintable() {
        return printable;
    }

    public void setPrintable(boolean printable) {
        this.printable = printable;
    }

    @Column(name = "print_limit")
    public Long getLimit() {
        return printLimit;
    }

    public void setLimit(Long limit) {
        this.printLimit = limit;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    public Set<CourseItemPrintStat> getPrintStatistics() {
        return printStatistics;
    }

    public void setPrintStatistics(Set<CourseItemPrintStat> printStatistics) {
        this.printStatistics = printStatistics;
    }
}
