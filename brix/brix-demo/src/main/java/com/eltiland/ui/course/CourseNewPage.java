package com.eltiland.ui.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ContentStatus;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.TrainingPage;
import com.eltiland.ui.common.column.image.ImagePanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.video.YoutubeVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.CourseVersionButton;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Course start page.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseNewPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager courseManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseNewPage.class);

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/course";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    public CourseNewPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final IModel<ELTCourse> courseIModel = new LoadableDetachableModel<ELTCourse>() {
            @Override
            protected ELTCourse load() {
                return genericManager.getObject(ELTCourse.class, parameters.get(PARAM_ID).toLong());
            }
        };

        if (courseIModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        final boolean isTraining = courseIModel.getObject() instanceof TrainingCourse;

        add(new Label("name", courseIModel.getObject().getName()));

        genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getAuthor());
        genericManager.initialize(courseIModel.getObject().getAuthor(),
                courseIModel.getObject().getAuthor().getAvatar());
        add(new ImagePanel("image", courseIModel.getObject().getAuthor().getAvatar(), true));
        add(new Label("author", courseIModel.getObject().getAuthor().getName()));
        add(new Label("authorAbout", courseIModel.getObject().getAuthor().getAchievements()));
        add(new YoutubeVideoPlayer("video", new Model<>(courseIModel.getObject().getVideo())));

        add(new IconButton("control", new ResourceModel("control.label"), ButtonAction.SETTINGS) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                setResponsePage(CourseControlPage.class,
                        new PageParameters().add(CourseControlPage.PARAM_ID, courseIModel.getObject().getId()));
            }

            @Override
            public boolean isVisible() {
                if (currentUserModel.getObject() == null) {
                    return false;
                } else {
                    if (!currentUserModel.getObject().isSuperUser()) {
                        List<ELTCourse> admins = courseManager.getAdminCourses(currentUserModel.getObject(), null);
                        if (!(admins.contains(courseIModel.getObject()))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        });

        genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getStartPage());
        add(new ELTGoogleDriveEditor("content",
                new GenericDBModel<>(GoogleDriveFile.class, courseIModel.getObject().getStartPage()),
                ELTGoogleDriveEditor.MODE.VIEW, GoogleDriveFile.TYPE.DOCUMENT));

        add(new IconButton("back", new ResourceModel("back.label"), ButtonAction.BACK) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                if (isTraining) {
                    setResponsePage(TrainingPage.class);
                } else {
                    setResponsePage(CourseListPage.class);
                }
            }
        });

        genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getContent());
        if (!isTraining) {
            genericManager.initialize(courseIModel.getObject(),
                    ((AuthorCourse) courseIModel.getObject()).getDemoContent());
        }
        add(new CourseVersionButton("demo", courseIModel, ContentStatus.DEMO) {
            @Override
            public boolean isVisible() {
                return !isTraining && ((AuthorCourse) courseIModel.getObject()).getDemoContent() != null;
            }
        });
        add(new CourseVersionButton("full", courseIModel, ContentStatus.FULL) {
            @Override
            public boolean isVisible() {
                genericManager.initialize(courseIModel.getObject(), courseIModel.getObject().getContent());
                return courseIModel.getObject().getContent() != null && courseIModel.getObject().getContent().size() > 0;
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE_START_PAGE);
    }
}
