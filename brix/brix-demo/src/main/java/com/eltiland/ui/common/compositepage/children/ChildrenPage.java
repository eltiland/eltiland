package com.eltiland.ui.common.compositepage.children;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page "For Children" with static content.
 */
public class ChildrenPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/forChildren";

    /**
     * Construct.
     */
    public ChildrenPage() {
        add(new BrixPanel("aboutGameCmsPanel", UrlUtils.createBrixPathForPanel("CHILDREN/aboutGame.html")));
        add(new BrixPanel("aboutNewspaperCmsPanel", UrlUtils.createBrixPathForPanel("CHILDREN/aboutNewspaper.html")));
        add(new BrixPanel("aboutCYTCmsPanel", UrlUtils.createBrixPathForPanel("CHILDREN/aboutCYT.html")));
    }
}
