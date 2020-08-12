package com.eltiland.ui.common.pages;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Сведения".
 *
 * @author Aleksey Plotnikov.
 */
public class PrivacyPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/privacy";

    public PrivacyPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("privacy.html")));
    }
}
