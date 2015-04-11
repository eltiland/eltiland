package com.eltiland;

import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 */
public class CMSPage extends TwoColumnPage {

    public static final String MOUNT_PATH = "/c";

    public CMSPage(PageParameters params) {
        super(params);
        add(new BrixPanel("brixPanel", UrlUtils.createBrixPathForPage(params)));
    }
}
