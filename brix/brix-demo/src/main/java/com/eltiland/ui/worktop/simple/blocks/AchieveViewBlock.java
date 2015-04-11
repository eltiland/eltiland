package com.eltiland.ui.worktop.simple.blocks;

import com.eltiland.model.user.User;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;

/**
 * Achieve view block.
 *
 * @author Aleksey Plotnikov.
 */
public class AchieveViewBlock extends AbstractProfileBlock {
    /**
     * Panel Constructor.
     *
     * @param id               markup id.
     * @param userModel simple user model.
     */
    public AchieveViewBlock(String id, IModel<User> userModel) {
        super(id, userModel);

        add(new MultiLineLabel("achieveData", userModel.getObject().getAchievements()));
    }

    @Override
    protected String getHeader() {
        return getString("blockHeader");
    }
}
