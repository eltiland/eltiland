package com.eltiland.model.course2;

import com.eltiland.model.file.File;

import javax.persistence.*;
import java.util.Date;

/**
 * Training course entity.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("TRAINING")
public class TrainingCourse extends ELTCourse {
    private File physicalDoc;
    private File legalDoc;
    private String requisites;
    private Date startDate;
    private Date finishDate;
    private Date joinDate;
    private boolean open;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_doc", unique = true)
    public File getPhysicalDoc() {
        return physicalDoc;
    }

    public void setPhysicalDoc(File physicalDoc) {
        this.physicalDoc = physicalDoc;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_doc", unique = true)
    public File getLegalDoc() {
        return legalDoc;
    }

    public void setLegalDoc(File legalDoc) {
        this.legalDoc = legalDoc;
    }

    @Column(name = "requisites", length = 4096)
    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "finish_date")
    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    @Column(name = "join_date")
    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    @Column(name = "open", nullable = false, columnDefinition = "boolean default FALSE")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
