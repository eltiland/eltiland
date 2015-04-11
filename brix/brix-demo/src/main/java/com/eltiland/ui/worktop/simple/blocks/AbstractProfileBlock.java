package com.eltiland.ui.worktop.simple.blocks;

import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * Abstract block for simple user panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractProfileBlock<T extends User> extends BaseEltilandPanel<T> {
    /**
     * Panel Constructor.
     *
     * @param id               markup id.
     * @param userModel simple user model.
     */
    protected AbstractProfileBlock(String id, IModel<T> userModel) {
        super(id, userModel);

        add(new Label("blockLabel", getHeader()));
    }

    protected abstract String getHeader();
}
