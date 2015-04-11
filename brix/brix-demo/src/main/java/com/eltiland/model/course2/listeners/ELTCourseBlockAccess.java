package com.eltiland.model.course2.listeners;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.IWithInterval;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity with information
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "block_access", schema = "course")
public class ELTCourseBlockAccess extends AbstractIdentifiable implements IWithInterval {
    private ELTCourseBlock block;
    private User listener;
    private boolean open;
    private Date startDate;
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block", nullable = false)
    public ELTCourseBlock getBlock() {
        return block;
    }

    public void setBlock(ELTCourseBlock block) {
        this.block = block;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listener", nullable = false)
    public User getListener() {
        return listener;
    }

    public void setListener(User listener) {
        this.listener = listener;
    }

    @Column(name = "open", nullable = false, columnDefinition = "boolean default TRUE")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
