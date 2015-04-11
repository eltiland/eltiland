package com.eltiland.ui.worktop.simple.blocks;

import com.eltiland.model.user.User;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;

/**
 * About view block.
 *
 * @author Aleksey Plotnikov.
 */
public class AboutViewBlock extends AbstractProfileBlock {
    /**
     * Panel Constructor.
     *
     * @param id               markup id.
     * @param userModel simple user model.
     */
    public AboutViewBlock(String id, IModel<User> userModel) {
        super(id, userModel);

        add(new MultiLineLabel("infoData", userModel.getObject().getInformation()));
    }

    @Override
    protected String getHeader() {
        return getString("blockHeader");
    }
}
