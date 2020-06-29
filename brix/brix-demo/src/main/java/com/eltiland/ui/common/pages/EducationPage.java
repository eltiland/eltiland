package com.eltiland.ui.common.pages;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Образование".
 *
 * @author Aleksey Plotnikov.
 */
public class EducationPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/education";

    public EducationPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("education.html")));
    }
}
