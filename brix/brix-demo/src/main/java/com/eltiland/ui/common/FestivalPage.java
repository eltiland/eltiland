package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.utils.UrlUtils;

/**
 * Page for "Фестиваль".
 *
 * @author Aleksey Plotnikov.
 */
public class FestivalPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/multfestival";


    public FestivalPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("festival.html")));
    }
}
