package com.eltiland.model.course;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.file.File;

import javax.persistence.*;

/**
 * Entity for training course documents.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "course_document", schema = "public")
public class CourseDocument extends AbstractIdentifiable {
    private File physicalDoc;
    private File legalDoc;
    private String requisites;
    private CourseSession courseSession;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physicalDoc")
    public File getPhysicalDoc() {
        return physicalDoc;
    }

    public void setPhysicalDoc(File physicalDoc) {
        this.physicalDoc = physicalDoc;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legalDoc")
    public File getLegalDoc() {
        return legalDoc;
    }

    public void setLegalDoc(File legalDoc) {
        this.legalDoc = legalDoc;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseSession")
    public CourseSession getCourseSession() {
        return courseSession;
    }

    public void setCourseSession(CourseSession courseSession) {
        this.courseSession = courseSession;
    }

    @Column(name = "requisites", length = 4096)
    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }
}
