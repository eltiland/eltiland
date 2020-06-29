package com.eltiland.ui.common.pages;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Сведения".
 *
 * @author Aleksey Plotnikov.
 */
public class InfoPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/info";

    public InfoPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("info.html")));
    }
}
