package com.eltiland.model.course2.content.google;

import com.eltiland.model.AbstractIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Course block.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@Table(name = "item_content", schema = "course")
public class CourseItemContent extends AbstractIdentifiable {
    private String body;

    @Column(name = "body", length = 65535)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

