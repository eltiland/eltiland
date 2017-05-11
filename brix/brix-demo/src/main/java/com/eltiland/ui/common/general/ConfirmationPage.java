package com.eltiland.ui.common.general;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.OneColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for confirmation.
 *
 * @author Aleksey Plotnikov.
 */
public class ConfirmationPage extends OneColumnPage {

    public static final String MOUNT_PATH = "/confirmation";

    public ConfirmationPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("confirmation.html")));
    }
}
