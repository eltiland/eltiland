package com.eltiland.ui.course;

import com.eltiland.bl.CourseInvoiceManager;
import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.CourseManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestAttemptManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course.*;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.UserTestAttempt;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.panels.CourseEditMessagePanel;
import com.eltiland.ui.course.components.tree.CourseTree;
import com.eltiland.ui.course.components.tree.CourseTreeModel;
import com.eltiland.ui.course.components.tree.ELTTreeNode;
import com.eltiland.ui.course.components.tree.VirtualCourseRootItem;
import com.eltiland.ui.course.content.*;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Course content base page.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseContentPage extends BaseEltilandPage {

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private CourseInvoiceManager courseInvoiceManager;
    @SpringBean
    private TestAttemptManager testAttemptManager;
    @SpringBean
    private CourseItemManager courseItemManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentPage.class);

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/courseContent";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_KIND = "v";

    /**
     * Content kind parameters.
     */
    public static final String DEMO_KIND = "demo";
    public static final String FULL_KIND = "full";

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private WebMarkupContainer contentContainer = new WebMarkupContainer("contentContainer");

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
     * Course content base page.
     */
    public CourseContentPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        if (!parameters.getNamedKeys().contains(PARAM_KIND)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_KIND);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final boolean full = parameters.get(PARAM_KIND).toString().equals(FULL_KIND);
        User user = EltilandSession.get().getCurrentUser();
        if (user == null && full) {
            throw new RestartResponseException(
                    CoursePage.class, new PageParameters().add(PARAM_ID, parameters.get(PARAM_ID)));
        }

        final IModel<Course> courseModel = new LoadableDetachableModel<Course>() {
            @Override
            protected Course load() {
                Course course = courseManager.getCourseById(parameters.get(PARAM_ID).toLong());
                genericManager.initialize(course, course.getDemoVersion());
                genericManager.initialize(course, course.getFullVersion());
                return course;
            }
        };

        if (full) {
            if (!(courseInvoiceManager.checkAccessToCourse(courseModel.getObject(), user))) {
                throw new RestartResponseException(
                        CoursePage.class, new PageParameters().add(PARAM_ID, parameters.get(PARAM_ID)));
            }
        }

        if (full) {
            // make user listener, if user not listener.
            genericManager.initialize(user, user.getCourses());
            user.getCourses().add(courseModel.getObject());
            try {
                userManager.updateUser(user);
            } catch (UserException e) {
                EltiStaticAlerts.registerErrorPopup(e.getMessage());
            }

            // if course contains tests with limitation and user has not attempt record - create it
            genericManager.initialize(courseModel.getObject(), courseModel.getObject().getFullVersion());
            for (CourseItem item : courseModel.getObject().getFullVersion()) {
                if (item instanceof TestCourseItem) {
                    if (((TestCourseItem) item).getAttemptLimit() > 0) {
                        if (!(testAttemptManager.hasAttemptRecord((TestCourseItem) item))) {
                            UserTestAttempt attempt = new UserTestAttempt();
                            attempt.setUser(currentUserModel.getObject());
                            attempt.setTest((TestCourseItem) item);
                            attempt.setAttemptCount(0);
                            attempt.setAttemptLimit(((TestCourseItem) item).getAttemptLimit());

                            try {
                                genericManager.saveNew(attempt);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot create attempt entity", e);
                                throw new WicketRuntimeException("Cannot create attempt entity", e);
                            }
                        }
                    }
                }
            }
        }


        String header = courseModel.getObject().getName();
        if (!full) {
            header += getString("demoAddition");
        }

        add(new Label("headerLabel", header));

        CourseTree tree = new CourseTree("courseTree", new CourseTreeModel(courseModel.getObject(), !full)) {
            @Override
            protected void onNodeLinkClicked(AjaxRequestTarget target, ELTTreeNode node) {
                super.onNodeLinkClicked(target, node);
                if (node instanceof GoogleCourseItem) {
                    GoogleCourseItem item = (GoogleCourseItem) courseItemManager.getCourseItemById(this.getCurrendId());
                    contentContainer.replace(new GoogleContentPanel("content",
                            new GenericDBModel<>(GoogleCourseItem.class, item)));
                } else if (node instanceof TestCourseItem) {
                    TestCourseItem item = (TestCourseItem) courseItemManager.getCourseItemById(this.getCurrendId());
                    contentContainer.replace(new TestContentPanel("content",
                            new GenericDBModel<>(TestCourseItem.class, item)));
                } else if (node instanceof VideoCourseItem) {
                    VideoCourseItem item = (VideoCourseItem) courseItemManager.getCourseItemById(this.getCurrendId());
                    contentContainer.replace(new VideoContentPanel("content",
                            new GenericDBModel<>(VideoCourseItem.class, item)));
                } else if (node instanceof FolderCourseItem) {
                    contentContainer.replace(new GeneralInfoPanel("content",
                            new GenericDBModel<>(Course.class, ((FolderCourseItem) node).getParentCourse())));
                } else if (node instanceof VirtualCourseRootItem) {
                    contentContainer.replace(new GeneralInfoPanel("content",
                            new GenericDBModel<>(Course.class, ((VirtualCourseRootItem) node).getCourseObject())));
                } else if (node instanceof WebinarCourseItem) {
                    WebinarCourseItem item =
                            (WebinarCourseItem) courseItemManager.getCourseItemById(this.getCurrendId());
                    contentContainer.replace(new WebinarContentPanel("content",
                            new GenericDBModel<>(WebinarCourseItem.class, item)));
                }
                target.add(contentContainer);
                target.appendJavaScript("formatTreeItem()");
            }

            @Override
            protected void onJunctionLinkClicked(AjaxRequestTarget target, ELTTreeNode node) {
                super.onJunctionLinkClicked(target, node);
                target.appendJavaScript("formatTreeItem()");
            }
        };
        add(tree);

        add(new IconButton("sendButton", new ResourceModel("send"), ButtonAction.SEND) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                editMessagePanelDialog.getDialogPanel().initData(
                        courseModel.getObject().getName(), courseModel.getObject().getAuthor().getEmail(), true);
                editMessagePanelDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                return full;
            }
        });

        add(new IconButton("supportButton", new ResourceModel("support"), ButtonAction.SUPPORT) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                editMessagePanelDialog.getDialogPanel().initData(
                        courseModel.getObject().getName(), courseModel.getObject().getSupportMail(), false);
                editMessagePanelDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                String supportMail = courseModel.getObject().getSupportMail();
                return full && (supportMail != null && !supportMail.isEmpty());
            }
        });

        add(new IconButton("controlButton", new ResourceModel("control"), ButtonAction.SETTINGS) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseControlPage.class,
                        new PageParameters().add(CourseControlPage.PARAM_ID, courseModel.getObject().getId()));
            }

            @Override
            public boolean isVisible() {
                User user = currentUserModel.getObject();
                return user != null && (currentUserModel.getObject().isSuperUser() ||
                        currentUserModel.getObject().getId().equals(courseModel.getObject().getAuthor().getId()));
            }
        });

        add(contentContainer.setOutputMarkupId(true));
        contentContainer.add(new GeneralInfoPanel(
                "content", new GenericDBModel<>(Course.class, courseModel.getObject())));

        tree.getTreeState().expandAll();
        add(editMessagePanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript("formatTreeItem();");
    }
}
