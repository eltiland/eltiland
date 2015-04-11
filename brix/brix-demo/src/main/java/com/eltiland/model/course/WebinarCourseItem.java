package com.eltiland.model.course;

import com.eltiland.model.webinar.Webinar;

import javax.persistence.*;

/**
 * Webinar course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("WEBINAR")
public class WebinarCourseItem extends ElementCourseItem {

    private Webinar webinar;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webinar")
    public Webinar getWebinar() {
        return webinar;
    }

    public void setWebinar(Webinar webinar) {
        this.webinar = webinar;
    }
}
