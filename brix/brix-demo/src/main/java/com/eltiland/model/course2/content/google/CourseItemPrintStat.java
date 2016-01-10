package com.eltiland.model.course2.content.google;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.listeners.ELTCourseListener;

import javax.persistence.*;

/**
 * Course block.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "printstat", schema = "course")
public class CourseItemPrintStat extends AbstractIdentifiable {
    private ELTCourseListener listener;
    private ELTGoogleCourseItem item;
    private Long currentPrint;
    private Long printLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener")
    public ELTCourseListener getListener() {
        return listener;
    }

    public void setListener(ELTCourseListener listener) {
        this.listener = listener;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item")
    public ELTGoogleCourseItem getItem() {
        return item;
    }

    public void setItem(ELTGoogleCourseItem item) {
        this.item = item;
    }

    @Column(name = "current_print")
    public Long getCurrentPrint() {
        return currentPrint;
    }

    public void setCurrentPrint(Long currentPrint) {
        this.currentPrint = currentPrint;
    }

    @Column(name = "print_limit")
    public Long getPrintLimit() {
        return printLimit;
    }

    public void setPrintLimit(Long printLimit) {
        this.printLimit = printLimit;
    }
}
