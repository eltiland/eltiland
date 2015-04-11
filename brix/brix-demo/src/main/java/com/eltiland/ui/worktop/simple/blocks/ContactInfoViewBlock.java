package com.eltiland.ui.worktop.simple.blocks;

import com.eltiland.model.user.User;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Contact info view block.
 *
 * @author Aleksey Plotnikov.
 */
public class ContactInfoViewBlock extends AbstractProfileBlock {
    /**
     * Panel Constructor.
     *
     * @param id               markup id.
     * @param userModel simple user model.
     */
    public ContactInfoViewBlock(String id, IModel<User> userModel) {
        super(id, userModel);

        User user = userModel.getObject();

        add(new Label("emailData", user.getEmail()));
        add(new Label("addressData", new Model<String>()) {
            @Override
            public boolean isVisible() {
                User user = (User) ContactInfoViewBlock.this.getModelObject();
                boolean isVisible = user.getAddress() != null && !(user.getAddress().isEmpty());
                if (isVisible) {
                    setDefaultModelObject(user.getAddress());
                }
                return isVisible;
            }
        });
        add(new Label("phoneData", new Model<String>()) {
            @Override
            public boolean isVisible() {
                User user = (User) ContactInfoViewBlock.this.getModelObject();
                boolean isVisible = user.getPhone() != null && !(user.getPhone().isEmpty());
                if (isVisible) {
                    setDefaultModelObject(user.getPhone());
                }
                return isVisible;
            }
        });
        add(new Label("skypeData", new Model<String>()) {
            @Override
            public boolean isVisible() {
                User user = (User) ContactInfoViewBlock.this.getModelObject();
                boolean isVisible = user.getSkype() != null && !(user.getSkype().isEmpty());
                if (isVisible) {
                    setDefaultModelObject(user.getSkype());
                }
                return isVisible;
            }
        });

    }

    @Override
    protected String getHeader() {
        return getString("blockHeader");
    }
}
