package com.eltiland.ui.common.pages;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Документы".
 *
 * @author Aleksey Plotnikov.
 */
public class DocumentsPage extends TwoColumnPage {
    public static final String MOUNT_PATH = "/documents";

    public DocumentsPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("documents.html")));
    }
}
