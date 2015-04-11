package com.eltiland.ui.common.compositepage.teachers;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 */
public class TeachersPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/forTeachers";

    public TeachersPage() {
        add(new BrixPanel("FAQCmsPanel", UrlUtils.createBrixPathForPanel("TEACHERS/FAQ.html")));
        add(new BrixPanel("aboutTeacherLibraryCmsPanel", UrlUtils.createBrixPathForPanel("TEACHERS/aboutTeacherLibrary.html")));
        add(new BrixPanel("aboutMoodleCmsPanel", UrlUtils.createBrixPathForPanel("TEACHERS/aboutMoodle.html")));
        add(new BrixPanel("QandACmsPanel", UrlUtils.createBrixPathForPanel("TEACHERS/QandA.html")));
        add(new BrixPanel("aboutNewspaperCmsPanel", UrlUtils.createBrixPathForPanel("TEACHERS/aboutNewspaper.html")));
    }
}
