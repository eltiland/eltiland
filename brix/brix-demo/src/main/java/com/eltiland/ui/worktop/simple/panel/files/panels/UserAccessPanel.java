package com.eltiland.ui.worktop.simple.panel.files.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for output user access information.
 *
 * @author Aleksey Plotnikov.
 */
public class UserAccessPanel extends BaseEltilandPanel<UserFile> {

    @SpringBean
    private GenericManager genericManager;

    private IModel<List<User>> userModel = new LoadableDetachableModel<List<User>>() {
        @Override
        protected List<User> load() {
            return new ArrayList<>(getModelObject().getDestinations());
        }
    };

    public UserAccessPanel(String id, IModel<UserFile> userFileIModel) {
        super(id, userFileIModel);

        genericManager.initialize(getModelObject(), getModelObject().getDestinations());
        add(new Label("header", getString("header")) {
            @Override
            public boolean isVisible() {
                return userModel.getObject().size() > 0;
            }
        });

        add(new ListView<User>("userList", userModel) {
            @Override
            protected void populateItem(ListItem<User> item) {
                item.add(new Label("name", item.getModelObject().getName()));
            }
        });
    }
}
