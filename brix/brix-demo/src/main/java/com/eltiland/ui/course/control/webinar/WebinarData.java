package com.eltiland.ui.course.control.webinar;

import java.io.Serializable;
import java.util.Date;

/**
 * Main data of the webinar for the course.
 *
 * @author Alex Plotnikov
 */
class WebinarData implements Serializable {
    private String name;
    private Date date;
    private Long duration;

    public WebinarData(String name, Date date, Long duration) {
        this.name = name;
        this.date = date;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Long getDuration() {
        return duration;
    }
}
