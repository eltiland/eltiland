package com.eltiland.ui.worktop.simple.panel;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import com.eltiland.ui.course.components.CourseItemPanel;
import com.eltiland.ui.worktop.simple.panel.course.CourseInvoicePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General courses panel for profile.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ProfileCoursePanel extends BaseEltilandPanel<User> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager eltCourseManager;

    private boolean isAuthor = false;
    private int index;

    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer") {
        @Override
        public boolean isVisible() {
            return index == 1 || index == 0;
        }
    };

    private CourseInvoicePanel invoicePanel = new CourseInvoicePanel("invoicePanel") {
        @Override
        protected void onCreate(ELTCourse course, AjaxRequestTarget target) {
            ELTAlerts.renderOKPopup(getString("courseCreatedMessage"), target);
            target.add(invoicePanel);
            target.add(sendLabel);
        }

        @Override
        public boolean isVisible() {
            return index == 2 && !(eltCourseManager.hasInvoices());
        }
    };

    private final Label sendLabel = new Label("alreadySend", new ResourceModel("courseAlreadySendedMessage")) {
        @Override
        public boolean isVisible() {
            return index == 2 && eltCourseManager.hasInvoices();
        }
    };

    private IModel<List<ELTCourse>> courseList = new LoadableDetachableModel<List<ELTCourse>>() {
        @Override
        protected List<ELTCourse> load() {
            if (getModelObject() == null) {
                return new ArrayList<>();
            } else {
                if (!isAuthor) {
                    return (List<ELTCourse>) eltCourseManager.getListenerCourses(
                            getModelObject(), isTraining() ? TrainingCourse.class : AuthorCourse.class, isModule());
                } else {
                    return eltCourseManager.getAdminCourses(getModelObject(),
                            isTraining() ? TrainingCourse.class : AuthorCourse.class);
                }
            }
        }
    };

    public ProfileCoursePanel(String id, IModel<User> userModel) {
        super(id, userModel);
        index = 0;

        add(new ELTTabMenu("tab_menu") {
            @Override
            public List<TabMenuData> getMenuItems() {
                return new ArrayList<>(Arrays.asList(
                        new TabMenuData((short) 2, getMenuCaption((short) 2)),
                        new TabMenuData((short) 1, getMenuCaption((short) 1)),
                        new TabMenuData((short) 0, getMenuCaption((short) 0))));
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                ProfileCoursePanel.this.index = index;
                if (index != 2) {
                    isAuthor = index == 1;
                    courseList.detach();
                }
                target.add(listContainer);
                target.add(invoicePanel);
                target.add(sendLabel);
            }
        });

        add(listContainer.setOutputMarkupPlaceholderTag(true));

        listContainer.add(new ListView<ELTCourse>("courseList", courseList) {
            @Override
            protected void populateItem(ListItem<ELTCourse> item) {
                item.add(new CourseItemPanel("coursePanel", item.getModel(), ProfileCoursePanel.this.getModel()));
            }
        });

        add(invoicePanel);
        add(sendLabel.setOutputMarkupPlaceholderTag(true));
        invoicePanel.initCourseKind(isTraining());
        invoicePanel.setOutputMarkupPlaceholderTag(true);
    }

    private String getMenuCaption(short index) {
        switch (index) {
            case 2:
                return getString("invoice.menu");
            case 1:
                return getString("author.menu");
            case 0:
                return getString("listener.menu");
            default:
                return "";
        }
    }

    protected abstract boolean isTraining();

    protected boolean isModule() {
        return false;
    }
}
