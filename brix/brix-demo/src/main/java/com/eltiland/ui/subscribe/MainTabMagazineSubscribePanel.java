package com.eltiland.ui.subscribe;

import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.model.Model;

/**
 * User: vadim.didenko
 * Date: 07.09.12
 * Time: 16:13
 */
public class MainTabMagazineSubscribePanel extends BaseEltilandPanel {
    public MainTabMagazineSubscribePanel(String id) {
        super(id);
        add(new RequestMagazineSubscribePanel("groupTab", new Model<>(true)));
    }
}
