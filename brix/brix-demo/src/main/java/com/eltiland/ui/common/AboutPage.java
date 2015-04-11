package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.utils.UrlUtils;

/**
 * Eltiland about project page.
 *
 * @author Aleksey PLotnikov.
 */
public class AboutPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/about";

    /**
     * Page constrctor.
     */
    public AboutPage() {
        add(new BrixPanel("aboutPanel", UrlUtils.createBrixPathForPage("aboutproject.html")));
    }
}
