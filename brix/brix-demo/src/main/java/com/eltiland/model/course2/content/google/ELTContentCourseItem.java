package com.eltiland.model.course2.content.google;

import javax.persistence.*;

/**
 * Google document item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("CNT")
public class ELTContentCourseItem extends ELTDocumentCourseItem {
    private CourseItemContent content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content")
    public CourseItemContent getContent() {
        return content;
    }

    public void setContent(CourseItemContent content) {
        this.content = content;
    }
}
