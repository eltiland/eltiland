package com.eltiland.ui.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseBlockManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.course.content2.components.BlockContentPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * Page, displaying content of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseNewContentPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseBlockManager courseBlockManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private ELTCourseManager courseManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseNewContentPage.class);

    public static final String DEMO_VERSION = "demo";
    public static final String FULL_VERSION = "full";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/courseCt";

    /**
     * Kind of the course version (demo/full)
     */
    public static final String PARAM_VERSION = "v";

    private ContentStatus status;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public CourseNewContentPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }
        if (!parameters.getNamedKeys().contains(PARAM_VERSION)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_VERSION);
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

        status = (parameters.get(PARAM_VERSION).toString().equals(DEMO_VERSION))
                ? ContentStatus.DEMO : ContentStatus.FULL;
        final boolean demo = status.equals(ContentStatus.DEMO);

        boolean isAdminAccess = false;
        // check for superadmin or admn access to the course
        if (currentUserModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        } else {
            if (!currentUserModel.getObject().isSuperUser()) {
                List<ELTCourse> admins = courseManager.getAdminCourses(currentUserModel.getObject(), null);
                if (admins.contains(courseModel.getObject())) {
                    isAdminAccess = true;
                }
            } else {
                isAdminAccess = true;
            }
        }

        if (!isAdminAccess) {
            // check for the access to the course
            boolean isFree = courseModel.getObject().getPrice() == null ||
                    courseModel.getObject().getPrice().equals(BigDecimal.ZERO);
            ELTCourseListener listener =
                    courseListenerManager.getItem(currentUserModel.getObject(), courseModel.getObject());
            boolean hasAccess = listener != null && listener.getStatus().equals(PaidStatus.CONFIRMED);
            if (!demo && !isFree && !hasAccess) {
                throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        add(new Label("name", courseModel.getObject().getName()));
        Label kind = new Label("kind", getString(demo ? "demo" : "full"));
        kind.add(new AttributeAppender("class", new Model<>(demo ? "demo_kind" : "full_kind"), " "));
        add(kind);

        add(new ListView<ELTCourseBlock>("blocks", new LoadableDetachableModel<List<? extends ELTCourseBlock>>() {
            @Override
            protected List<? extends ELTCourseBlock> load() {
                return courseBlockManager.getSortedBlockList(courseModel.getObject(), status);
            }
        }) {
            @Override
            protected void populateItem(ListItem<ELTCourseBlock> item) {
                item.add(new BlockContentPanel("block", item.getModel()));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE_PAGE);
    }
}
