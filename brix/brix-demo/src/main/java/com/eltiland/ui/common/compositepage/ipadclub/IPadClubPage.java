package com.eltiland.ui.common.compositepage.ipadclub;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 * Page for IPad club.
 */
public class IPadClubPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/ipadClub";

    public IPadClubPage() {
        add(new BrixPanel("ipadPanel", UrlUtils.createBrixPathForPage("ipadClub.html")));
    }
}
