package com.eltiland.ui.course.control.data.panel.item;

import java.io.Serializable;
import java.util.Date;

/**
 * Main data of the webinar for the course.
 *
 * @author Alex Plotnikov
 */
public class WebinarData implements Serializable {
    private String name;
    private Date date;

    public WebinarData(String name, Date date) {
        this.name = name;
        this.date = date;
    }
}
