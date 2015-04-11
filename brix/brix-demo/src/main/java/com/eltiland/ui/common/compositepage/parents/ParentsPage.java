package com.eltiland.ui.common.compositepage.parents;

import com.eltiland.BrixPanel;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;

/**
 */
public class ParentsPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/forParents";

    /**
     * Construct.
     */
    public ParentsPage() {
        add(new BrixPanel("parents", UrlUtils.createBrixPathForPanel("PARENTS/parents.html")));
    }
}
