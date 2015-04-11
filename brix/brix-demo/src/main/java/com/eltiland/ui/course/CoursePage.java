package com.eltiland.ui.course;

import com.eltiland.bl.*;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.column.image.ImagePanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.video.YoutubeVideoPlayer;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.CourseSendInvoicePanel;
import com.eltiland.ui.course.components.panels.CourseEditMessagePanel;
import com.eltiland.ui.forum.ForumThreadPage;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.worktop.BaseWorktopPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Course base page.
 *
 * @author Aleksey Plotnikov.
 */
public class CoursePage extends BaseEltilandPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoursePage.class);

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private CourseInvoiceManager courseInvoiceManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;


    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/course_old";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Content kind page parameter.
     */
    public static final String PARAM_KIND = "v";

    /**
     * Content kind parameters.
     */
    public static final String DEMO_KIND = "demo";
    public static final String FULL_KIND = "full";

    private boolean isListener = false;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<Course> courseModel = new GenericDBModel<>(Course.class);

    private Dialog<CourseSendInvoicePanel> courseSendInvoicePanelDialog =
            new Dialog<CourseSendInvoicePanel>("sendInvoiceDialog", 300) {
                @Override
                public CourseSendInvoicePanel createDialogPanel(String id) {
                    return new CourseSendInvoicePanel(id);
                }

                @Override
                public void registerCallback(CourseSendInvoicePanel panel) {
                    super.registerCallback(panel);
                    panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                        @Override
                        public void process(AjaxRequestTarget target) {
                            close(target);

                            CourseInvoice invoice = new CourseInvoice();
                            invoice.setApply(false);
                            invoice.setCreationDate(DateUtils.getCurrentDate());
                            invoice.setCourse(courseModel.getObject());
                            invoice.setListener(currentUserModel.getObject());

                            try {
                                genericManager.saveNew(invoice);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot create course invoice", e);
                                throw new WicketRuntimeException("Cannot create course invoice", e);
                            }

                            try {
                                emailMessageManager.sendCourseAccessInvoiceToAdmin(invoice);
                            } catch (EmailException e) {
                                LOGGER.error("Cannot send mail to admin", e);
                                throw new WicketRuntimeException("Cannot send mail to admin", e);
                            }

                            ELTAlerts.renderOKPopup(getString("invoiceSended"), target);
                        }
                    });
                }
            };

    private Dialog<CourseEditMessagePanel> editMessagePanelDialog =
            new Dialog<CourseEditMessagePanel>("editMessageDialog", 400) {
                @Override
                public CourseEditMessagePanel createDialogPanel(String id) {
                    return new CourseEditMessagePanel(id);
                }

                @Override
                public void registerCallback(CourseEditMessagePanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<String>() {
                        @Override
                        public void process(IModel<String> model, AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("sendMessage"), target);
                        }
                    });
                }
            };


    /**
     * Course base page.
     */
    public CoursePage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        Course course = courseManager.getCourseById(parameters.get(PARAM_ID).toLong());
        genericManager.initialize(course, course.getDemoVersion());
        genericManager.initialize(course, course.getFullVersion());
        genericManager.initialize(course, course.getListeners());
        genericManager.initialize(course, course.getAuthor());
        genericManager.initialize(course.getAuthor(), course.getAuthor().getAvatar());
        courseModel.setObject(course);

        isListener = courseModel.getObject().getListeners().contains(currentUserModel.getObject());

        if (courseModel.getObject() == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new ImagePanel("image", course.getAuthor().getAvatar(), true));

        add(new YoutubeVideoPlayer("video", new Model<>(courseModel.getObject().getVideo())));

        add(new Label("name", courseModel.getObject().getName()));
        add(new Label("author", courseModel.getObject().getAuthor().getName()));
        add(new Label("authorAbout", courseModel.getObject().getAuthor().getAchievements()));

        WebMarkupContainer demoInfoContainer = new WebMarkupContainer("demoInfo") {
            @Override
            public boolean isVisible() {
                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getDemoVersion());
                return !(courseModel.getObject().getDemoVersion().isEmpty());
            }
        };
        add(demoInfoContainer);

        final CoursePaidInvoice invoice = coursePaidInvoiceManager.getActualInvoice(courseModel.getObject(), null);
        WebMarkupContainer fullInfoContainer = new WebMarkupContainer("fullInfo") {
            @Override
            public boolean isVisible() {
                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getFullVersion());
                return (!(courseModel.getObject().getFullVersion().isEmpty()) && courseModel.getObject().isFullAccess())
                        && (invoice != null);
            }
        };
        add(fullInfoContainer);

        if (invoice != null) {
            fullInfoContainer.add(new Label("full_price",
                    String.format(getString("paid"), invoice.getPrice().toString())));
        } else {
            fullInfoContainer.add(new Label("full_price", ""));
        }

        final WebMarkupContainer joinContainer = new WebMarkupContainer("joinInfo");
        joinContainer.setVisible(isListener);

        add(joinContainer.setOutputMarkupPlaceholderTag(true));

        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getForum());

        add(new EltiAjaxLink("forumButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(ForumThreadPage.class,
                        new PageParameters().add(ForumThreadPage.PARAM_ID, courseModel.getObject().getForum().getId()));
            }

            @Override
            public boolean isVisible() {
                if (courseModel.getObject().isTraining()) {
                    return false;
                }

                User user = currentUserModel.getObject();
                if (user == null) {
                    return false;
                }
                genericManager.initialize(user, user.getCourses());
                return (courseModel.getObject().getForum() != null) &&
                        ((user.isSuperUser())
                                || (user.getId().equals(courseModel.getObject().getAuthor().getId()))
                                || (user.getCourses().contains(courseModel.getObject())));
            }
        });

        add(new EltiAjaxLink("demoButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseContentPage.class,
                        new PageParameters()
                                .add(PARAM_ID, courseModel.getObject().getId())
                                .add(PARAM_KIND, DEMO_KIND));
            }

            @Override
            public boolean isVisible() {
                if (courseModel.getObject().isTraining()) {
                    return false;
                }
                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getDemoVersion());
                return !(courseModel.getObject().getDemoVersion().isEmpty());
            }
        });

        add(new EltiAjaxLink("supportButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                editMessagePanelDialog.getDialogPanel().initData(
                        courseModel.getObject().getName(), courseModel.getObject().getSupportMail(), false);
                editMessagePanelDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null && courseModel.getObject().getSupportMail() != null;
            }
        });

        final EltiAjaxLink preJoinButton = new EltiAjaxLink("preJoinButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                User user = currentUserModel.getObject();
                Course course = courseModel.getObject();

                genericManager.initialize(user, user.getCourses());
                user.getCourses().add(course);
                try {
                    userManager.updateUser(user);
                } catch (UserException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }

                isListener = true;
                target.add(this);
                joinContainer.setVisible(true);
                target.add(joinContainer);
            }

            @Override
            public boolean isVisible() {
                if (courseModel.getObject().isTraining()) {
                    return false;
                }

                return ((currentUserModel.getObject() != null) &&
                        !isListener &&
                        (courseModel.getObject().isPreJoin()) &&
                        (courseModel.getObject().getFullVersion().isEmpty()));
            }
        };

        add(preJoinButton.setOutputMarkupPlaceholderTag(true));

        add(new EltiAjaxLink("paidButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (currentUserModel.getObject() == null) {
                    ELTAlerts.renderErrorPopup(getString("errorNotLogged"), target);
                } else {
                    if (courseInvoiceManager.checkInvoicePresent(
                            courseModel.getObject(), currentUserModel.getObject())) {
                        ELTAlerts.renderErrorPopup(getString("invoicePresent"), target);
                    } else {
                        if (courseInvoiceManager.checkAccessToCourse(
                                courseModel.getObject(), currentUserModel.getObject())) {
                            if (coursePaidInvoiceManager.isCourseEntirePaid(courseModel.getObject(),
                                    currentUserModel.getObject())) {
                                throw new RestartResponseException(CoursePayPage.class,
                                        new PageParameters().add(PARAM_ID, courseModel.getObject().getId()));
                            } else {
                                throw new RestartResponseException(CourseContentPage.class,
                                        new PageParameters()
                                                .add(PARAM_ID, courseModel.getObject().getId())
                                                .add(PARAM_KIND, FULL_KIND));
                            }
                        } else {
                            courseSendInvoicePanelDialog.show(target);
                        }
                    }
                }
            }

            @Override
            public boolean isVisible() {
                if (courseModel.getObject().isTraining()) {
                    return false;
                }

                genericManager.initialize(courseModel.getObject(), courseModel.getObject().getFullVersion());
                return !(courseModel.getObject().getFullVersion().isEmpty()) && courseModel.getObject().isFullAccess();
            }
        });
        add(new EltiAjaxLink("controlButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseControlPage.class,
                        new PageParameters().add(PARAM_ID, courseModel.getObject().getId()));
            }

            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null &&
                        !(courseModel.getObject().isTraining()) &&
                        (currentUserModel.getObject().isSuperUser() ||
                                currentUserModel.getObject().getId().equals(
                                        courseModel.getObject().getAuthor().getId()));
            }
        });
        add(new EltiAjaxLink("backButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(BaseWorktopPage.class,
                        new PageParameters().add(BaseWorktopPage.PARAM_ID, "3"));
            }

            @Override
            public boolean isVisible() {
                return currentUserModel.getObject() != null &&
                        currentUserModel.getObject().getId().equals(courseModel.getObject().getAuthor().getId());
            }
        });

        genericManager.initialize(courseModel.getObject(), courseModel.getObject().getStartPage());
        add(new ELTGoogleDriveEditor("content",
                new GenericDBModel<>(GoogleDriveFile.class, courseModel.getObject().getStartPage()),
                ELTGoogleDriveEditor.MODE.VIEW, GoogleDriveFile.TYPE.DOCUMENT));

        add(new EltiAjaxLink("backListButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(CourseListPage.class);
            }
        });

        add(courseSendInvoicePanelDialog);
        add(editMessagePanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse headerResponse) {
        if (getPageParameters().getNamedKeys().contains(PARAM_ID)) {
            String fullUrl = String.format("http://eltiland.ru%s?%s=%s", MOUNT_PATH, PARAM_ID,
                    getPageParameters().get(PARAM_ID).toString());
            Course course = courseManager.getCourseById(getPageParameters().get(PARAM_ID).toLong());
            if (course != null) {
                headerResponse.renderOnDomReadyJavaScript(String.format("createShare('%s', '%s');", fullUrl,
                        course.getName()));
            }
        }
        super.renderHead(headerResponse);
    }
}
