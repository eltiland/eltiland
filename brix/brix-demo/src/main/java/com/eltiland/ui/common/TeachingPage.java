package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Развиваем дома".
 *
 * @author Aleksey Plotnikov.
 */
public class TeachingPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/teach";

    public TeachingPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("teaching.html")));
    }
}
