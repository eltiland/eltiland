package com.eltiland.ui.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.control.admin.CourseAdminPanel;
import com.eltiland.ui.course.control.data.CourseContentPanel;
import com.eltiland.ui.course.control.document.CourseDocumentPanel;
import com.eltiland.ui.course.control.general.CourseGeneralPanel;
import com.eltiland.ui.course.control.listeners.CourseInvoicePanel;
import com.eltiland.ui.course.control.listeners.CourseListenersPanel;
import com.eltiland.ui.course.control.print.CoursePrintPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Course control page.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseControlPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager courseManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseControlPage.class);
    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/courseControl";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    private WebMarkupContainer infoContainer = new WebMarkupContainer("infoContainer");

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public CourseControlPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final IModel<ELTCourse> courseModel = new LoadableDetachableModel<ELTCourse>() {
            @Override
            protected ELTCourse load() {
                return genericManager.getObject(ELTCourse.class, parameters.get(PARAM_ID).toLong());
            }
        };

        if (courseModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        if (currentUserModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (!currentUserModel.getObject().isSuperUser()) {
                List<ELTCourse> admins = courseManager.getAdminCourses(currentUserModel.getObject(), null);
                if (!(admins.contains(courseModel.getObject()))) {
                    throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        }

        add(new Label("name", courseModel.getObject().getName()));

        ELTTabMenu tabBar = new ELTTabMenu("tabPanel") {
            @Override
            public List<TabMenuData> getMenuItems() {
                List<TabMenuData> menuItems = new ArrayList<>();

                menuItems.add(new TabMenuData((short) 6, getMenuCaption((short) 6)));
                menuItems.add(new TabMenuData((short) 5, getMenuCaption((short) 5)));
                menuItems.add(new TabMenuData((short) 4, getMenuCaption((short) 4)));
                if (courseModel.getObject() instanceof TrainingCourse) {
                    menuItems.add(new TabMenuData((short) 3, getMenuCaption((short) 3)));
                }
                menuItems.add(new TabMenuData((short) 2, getMenuCaption((short) 2)));
                menuItems.add(new TabMenuData((short) 1, getMenuCaption((short) 1)));
                menuItems.add(new TabMenuData((short) 0, getMenuCaption((short) 0)));
                return menuItems;
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                if (index == 0) {
                    infoContainer.replace(new CourseGeneralPanel("infoPanel", courseModel));
                } else if (index == 1) {
                    infoContainer.replace(new CourseContentPanel("infoPanel", courseModel));
                } else if (index == 2) {
                    infoContainer.replace(new CourseInvoicePanel("infoPanel", courseModel));
                } else if (index == 3) {
                    infoContainer.replace(new CourseDocumentPanel("infoPanel",
                            new GenericDBModel<>(TrainingCourse.class, (TrainingCourse) courseModel.getObject())));
                } else if (index == 4) {
                    infoContainer.replace(new CourseListenersPanel("infoPanel", courseModel));
                } else if (index == 5) {
                    infoContainer.replace(new CourseAdminPanel("infoPanel", courseModel));
                } else if (index == 6) {
                    infoContainer.replace(new CoursePrintPanel("infoPanel", courseModel));
                }
                target.add(infoContainer);
            }
        };

        add(infoContainer.setOutputMarkupId(true));
        add(tabBar);

        infoContainer.add(new CourseGeneralPanel("infoPanel", courseModel));
    }

    private String getMenuCaption(short index) {
        switch (index) {
            case 0:
                return getString("general.menu");
            case 1:
                return getString("content.menu");
            case 2:
                return getString("invoice.menu");
            case 3:
                return getString("document.menu");
            case 4:
                return getString("listener.menu");
            case 5:
                return getString("admin.menu");
            case 6:
                return getString("print.menu");
            default:
                return "";
        }
    }
}
