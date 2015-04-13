package com.eltiland.ui.course;

import com.eltiland.bl.CourseManager;
import com.eltiland.model.course.Course;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.menu.ELTTabMenu;
import com.eltiland.ui.common.components.menu.TabMenuData;
import com.eltiland.ui.course.control.content.CourseContentPanel;
import com.eltiland.ui.course.control.document.CourseDocumentPanel;
import com.eltiland.ui.course.control.users.CourseInvoicePanel;
import com.eltiland.ui.course.control.users.CourseListenerPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Course control page.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseControlPage_old extends BaseEltilandPage {

    @SpringBean
    private CourseManager courseManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseControlPage.class);
    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/courseControl1";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    private WebMarkupContainer infoContainer = new WebMarkupContainer("infoContainer");

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public CourseControlPage_old(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final IModel<Course> courseModel = new LoadableDetachableModel<Course>() {
            @Override
            protected Course load() {
                return courseManager.getCourseById(parameters.get(PARAM_ID).toLong());
            }
        };

        if (courseModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        ELTTabMenu tabBar = new ELTTabMenu("tabPanel") {
            @Override
            public List<TabMenuData> getMenuItems() {
                return new ArrayList<>(Arrays.asList(
                        new TabMenuData((short) 2, getMenuCaption((short) 2)),
                        new TabMenuData((short) 1, getMenuCaption((short) 1)),
                        new TabMenuData((short) 0, getMenuCaption((short) 0))));
            }

            @Override
            public void onClick(short index, AjaxRequestTarget target) {
                if (index == 0) {
                    infoContainer.replace(new CourseContentPanel("infoPanel", courseModel));
                } else if (index == 1) {
                    infoContainer.replace(new CourseInvoicePanel("infoPanel", courseModel));
                } else if (index == 2) {
                    infoContainer.replace(new CourseListenerPanel("infoPanel", courseModel));
                }
                target.add(infoContainer);
            }
        };

        add(infoContainer.setOutputMarkupId(true));
        add(tabBar);

        infoContainer.add(new CourseContentPanel("infoPanel", courseModel));
    }



    private String getMenuCaption(short index) {
        switch (index) {
            case 0:
                return getString("content.menu");
            case 1:
                return getString("invoice.menu");
            case 2:
                return getString("listener.menu");
            default:
                return "";
        }
    }
}
