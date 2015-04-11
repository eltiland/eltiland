package com.eltiland.model.file;

import com.eltiland.model.AbstractIdentifiable;
import com.eltiland.model.course2.ELTCourse;

import javax.persistence.*;

/**
 * Course / UserFile M-M relation table.
 */
@Entity
@Table(name = "course_file_access", schema = "public")
public class CourseFileAccess extends AbstractIdentifiable {
    private ELTCourse course;
    private UserFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    public ELTCourse getCourse() {
        return course;
    }

    public void setCourse(ELTCourse course) {
        this.course = course;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    public UserFile getFile() {
        return file;
    }

    public void setFile(UserFile file) {
        this.file = file;
    }
}
