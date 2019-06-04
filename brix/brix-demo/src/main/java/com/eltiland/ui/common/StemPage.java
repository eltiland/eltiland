package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Стем".
 *
 * @author Aleksey Plotnikov.
 */
public class StemPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/stem";

    public StemPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("stem.html")));
    }
}
