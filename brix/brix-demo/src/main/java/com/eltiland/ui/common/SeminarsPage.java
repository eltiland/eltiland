package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.utils.UrlUtils;

/**
 * Страница семинаров.
 *
 * @author Aleksey Plotnikov.
 */
public class SeminarsPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/seminars";

    public SeminarsPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("seminars.html")));
    }
}
