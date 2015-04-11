package com.eltiland.ui.subscribe;

import com.eltiland.bl.HtmlCleaner;
import com.eltiland.ui.common.TwoColumnPage;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class SubscribePage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/gazeta";

    @SpringBean
    private HtmlCleaner htmlCleaner;

    /**
     * Constructor
     */
    public SubscribePage() {
        add(new MainTabMagazineSubscribePanel("subscribe"));
    }
}