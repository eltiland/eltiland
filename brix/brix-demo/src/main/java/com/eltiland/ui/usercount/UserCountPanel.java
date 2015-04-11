package com.eltiland.ui.usercount;

import com.eltiland.bl.user.UserManager;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for output current user count on home page.
 *
 * @author Aleksey Plotnikov.
 */
public class UserCountPanel extends BaseEltilandPanel {

    @SpringBean
    private UserManager userManager;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public UserCountPanel(String id) {
        super(id);

        IModel<List<Integer>> numberList = new LoadableDetachableModel<List<Integer>>() {
            @Override
            protected List<Integer> load() {
                int count = userManager.getConfirmedUsersCount(true);
                List<Integer> result = new ArrayList<>();
                while (count > 0) {
                    result.add(count % 10);
                    count = count / 10;
                }
                if (!(result.isEmpty())) {
                    return Lists.reverse(result);
                } else {
                    return result;
                }
            }
        };

        WebMarkupContainer listContainer = new WebMarkupContainer("numbers");
        add(listContainer);
        listContainer.add(new AttributeModifier(
                "style", new Model<>(String.format("margin-left: %d%%", 42 - numberList.getObject().size() * 4))));

        listContainer.add(new ListView<Integer>("numberList", numberList) {
            @Override
            protected void populateItem(ListItem<Integer> item) {
                item.add(new Label("number", String.valueOf(item.getModelObject())));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_USERCOUNT);
    }
}
