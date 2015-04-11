package com.eltiland.model.course;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Abstract element course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
public abstract class ElementCourseItem extends CourseItem {
    @Override
    @Transient
    public boolean getAllowsChildren() {
        return false;
    }
}