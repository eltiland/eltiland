package com.eltiland.ui.common.compositepage.children;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page "For Children" with static content.
 */
public class ChildrenPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/forChildren";

    private static final String IPAD_CLUB_PATH = "ipadClub.html";

    private static final String ABOUT_GAME_PAGE_PATH = "CHILDREN/aboutGame.html";

    private static final String ABOUT_NEWSPARER_PAGE_PATH = "CHILDREN/aboutNewspaper.html";

    private static final String ABOUT_CYT_PAGE_PATH = "CHILDREN/aboutCYT.html";
    /**
     * Construct.
     */
    public ChildrenPage() {
        add(new BrixPanel("child.ipad.page", UrlUtils.createBrixPathForPage(IPAD_CLUB_PATH)));
        add(new BrixPanel("about.game.page", UrlUtils.createBrixPathForPanel(ABOUT_GAME_PAGE_PATH)));
        add(new BrixPanel("about.newspaper.page", UrlUtils.createBrixPathForPanel(ABOUT_NEWSPARER_PAGE_PATH)));
        add(new BrixPanel("about.cyt.page", UrlUtils.createBrixPathForPanel(ABOUT_CYT_PAGE_PATH)));
    }
}
