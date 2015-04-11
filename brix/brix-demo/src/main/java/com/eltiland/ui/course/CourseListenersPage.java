package com.eltiland.ui.course;

import com.eltiland.bl.CourseManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.course.Course;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.listeners.CourseListenersGridPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Page for output list of course users.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseListenersPage extends BaseEltilandPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseListenersPage.class);

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/courseUsers";

    /**
     * Course id parameter.
     */
    public static final String PARAM_ID = "id";

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<Course> courseModel = new GenericDBModel<>(Course.class);

    public CourseListenersPage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        Course course = courseManager.getCourseById(parameters.get(PARAM_ID).toLong());
        if (course == null) {
            String errMsg = String.format("Wrong ID parameter");
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        } else {
            courseModel.setObject(course);
        }

        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getAuthor());


        if (currentUserModel.getObject() == null ||
                ((!(currentUserModel.getObject().isSuperUser())) &&
                        !(courseModel.getObject().getAuthor().getId().equals(currentUserModel.getObject().getId())))) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new Label("header", String.format(getString("header"), courseModel.getObject().getName())));

        add(new CourseListenersGridPanel("grid", courseModel));

//        add(new CourseListenersDataTablePanel("grid",
//                new EltiDataProviderBase<User>() {
//                    @Override
//                    public Iterator iterator(int first, int count) {
//                        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getListeners());
//                        if (courseModel.getObject().getListeners().size() > 0) {
//                            return userManager.getCourseListeners(
//                                    courseModel.getObject(), first, count,
//                                    getSort().getProperty(), getSort().isAscending()).iterator();
//                        } else {
//                            return Collections.<User>emptyIterator();
//                        }
//                    }
//
//                    @Override
//                    public int size() {
//                        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getListeners());
//                        return courseModel.getObject().getListeners().size();
//                    }
//                }, 30) {
//            @Override
//            protected boolean isPaid() {
//                return false;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            protected IModel<Course> getCourseModel() {
//                return null;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            protected void updateInfo(AjaxRequestTarget target) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            protected void editPrice(AjaxRequestTarget target, IModel<CoursePayment> paymentIModel) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE);
        response.renderCSSReference(ResourcesUtils.CSS_TABLE_STYLE);
    }
}
