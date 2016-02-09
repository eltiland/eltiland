package com.eltiland.ui.course.content2.components;

import com.eltiland.bl.PropertyManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.model.Property;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.course.CourseItemPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Item content panel for content page.
 *
 * @author Aleksey Plotnikov.
 */
public class ItemContentPanel extends BaseEltilandPanel<ELTCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private PropertyManager propertyManager;

    private IModel<List<ELTCourseItem>> subItemModel = new LoadableDetachableModel<List<ELTCourseItem>>() {
        @Override
        protected List<ELTCourseItem> load() {
            if (!(ItemContentPanel.this.getModelObject() instanceof ELTGroupCourseItem)) {
                return new ArrayList<>();
            } else {
                return courseItemManager.getItems((ELTGroupCourseItem) ItemContentPanel.this.getModelObject());
            }
        }
    };

    public ItemContentPanel(String id, IModel<ELTCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        final boolean isGroup = getModelObject() instanceof ELTGroupCourseItem;

        Label name = new Label("name", getModelObject().getName());
        add(name);
        if (!(getModelObject() instanceof ELTGroupCourseItem)) {
            name.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    if( getModelObject() instanceof ELTGoogleCourseItem ) {
                        if( ((ELTGoogleCourseItem)getModelObject()).isHasWarning()) {
                            EltiStaticAlerts.registerWarningPopupModal(
                                    propertyManager.getProperty(Property.COURSE_AUTHOR_WARNING));
                        }
                    }
                    throw new RestartResponseException(CourseItemPage.class,
                            new PageParameters().add(CourseItemPage.PARAM_ID, getModelObject().getId()));
                }
            });
        }
        WebMarkupContainer icon = new WebMarkupContainer("icon") {
            @Override
            public boolean isVisible() {
                return !isGroup;
            }
        };
        add(icon);
        if (!isGroup) {
            icon.add(new AttributeAppender("class",
                    new Model<>(getString(getModelObject().getClass().getSimpleName() + ".class")), " "));
        } else {
            name.add(new AttributeAppender("class", new Model<>("group"), " "));
        }

        add(new ListView<ELTCourseItem>("subItems", subItemModel) {
            @Override
            protected void populateItem(ListItem<ELTCourseItem> item) {
                item.add(new ItemContentPanel("item", item.getModel()));
            }
        });
    }
}
