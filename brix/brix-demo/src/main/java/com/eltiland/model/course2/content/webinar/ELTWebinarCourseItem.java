package com.eltiland.model.course2.content.webinar;

import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.webinar.Webinar;

import javax.persistence.*;

/**
 * Webinar course item.
 *
 * @author Aleksey Plotnikov.
 */
@Entity
@DiscriminatorValue("WEBINAR")
public class ELTWebinarCourseItem extends ELTCourseItem {
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
