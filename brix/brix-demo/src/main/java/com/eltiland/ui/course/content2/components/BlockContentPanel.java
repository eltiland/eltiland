package com.eltiland.ui.course.content2.components;

import com.eltiland.bl.course.ELTCourseBlockAccessManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseBlockAccess;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;
import java.util.List;

/**
 * Panel for block information on content page.
 *
 * @author Aleksey Plotnikov.
 */
public class BlockContentPanel extends BaseEltilandPanel<ELTCourseBlock> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private ELTCourseBlockAccessManager courseBlockAccessManager;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public BlockContentPanel(String id, IModel<ELTCourseBlock> eltCourseBlockIModel) {
        super(id, eltCourseBlockIModel);

        ELTCourseBlockAccess blockAccess =
                courseBlockAccessManager.find(currentUserModel.getObject(), getModelObject());

        Label name = new Label("name", getModelObject().getName());
        add(name);

        // Block access checking
        Date currentDate = DateUtils.getCurrentDate();
        final boolean isAccess;

        if (blockAccess != null) {
            if (blockAccess.isOpen()) {
                Date startDate = blockAccess.getStartDate();
                Date endDate = blockAccess.getEndDate();
                isAccess = startDate != null && endDate != null &&
                        currentDate.after(startDate) && currentDate.before(endDate);
            } else {
                isAccess = false;
            }
        } else {
            isAccess = true;
        }

        String nameValue = getModelObject().getName();
        if (!isAccess) {
            name.add(new AttributeAppender("class", new Model<String>("name_closed"), " "));
            nameValue += getString("access_closed");
        }
        name.setDefaultModelObject(nameValue);

        add(new ListView<ELTCourseItem>("items", new LoadableDetachableModel<List<? extends ELTCourseItem>>() {
            @Override
            protected List<? extends ELTCourseItem> load() {
                return courseItemManager.getItems(BlockContentPanel.this.getModelObject());
            }
        }) {
            @Override
            protected void populateItem(ListItem<ELTCourseItem> item) {
                item.add(new ItemContentPanel("item", item.getModel()));
            }

            @Override
            public boolean isVisible() {
                return isAccess;
            }
        });
    }
}
