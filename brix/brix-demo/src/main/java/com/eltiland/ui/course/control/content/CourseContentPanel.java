package com.eltiland.ui.course.control.content;

import com.eltiland.bl.CourseManager;
import com.eltiland.bl.forum.ForumGroupManager;
import com.eltiland.bl.forum.ForumManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.forum.Forum;
import com.eltiland.model.forum.ForumGroup;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.selector.SelectorPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.CourseContentPage;
import com.eltiland.ui.course.CourseListenersPage;
import com.eltiland.ui.course.components.control.CourseControlPanel;
import com.eltiland.ui.course.components.editPanels.CourseContentEditPanel;
import com.eltiland.ui.course.components.tree.CourseTree;
import com.eltiland.ui.course.components.tree.CourseTreeModel;
import com.eltiland.ui.course.components.tree.ELTTreeNode;
import com.eltiland.ui.course.components.tree.VirtualCourseRootItem;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * Document management panel for training courses.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseContentPanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseContentPanel.class);

    @SpringBean
    private CourseManager courseManager;
    @SpringBean
    private ForumGroupManager forumGroupManager;
    @SpringBean
    private ForumManager forumManager;

    private WebMarkupContainer currentPage;
    private WebMarkupContainer contentContainer = new WebMarkupContainer("contentContainer");

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     */
    public CourseContentPanel(String id, final IModel<Course> courseIModel) {
        super(id, courseIModel);

        final IModel<Course> courseModel = new LoadableDetachableModel<Course>() {
            @Override
            protected Course load() {
                return courseManager.getCourseById(courseIModel.getObject().getId());
            }
        };

        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null ||
                (!(currentUser.isSuperUser()) &&
                        !(currentUser.getId().equals(courseModel.getObject().getAuthor().getId())))) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        final Label courseLabel = new Label("name", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return courseModel.getObject().getName();
            }
        });

        courseLabel.setOutputMarkupId(true);
        add(courseLabel);

        SimpleSelector demoLink = new CourseSelectorLink("demo", courseModel) {

            private CourseContentEditPanel panel = new CourseContentEditPanel("contentPanel",
                    new GenericDBModel<>(Course.class, courseModel.getObject()),
                    Course.CONTENT_KIND.DEMO) {
                @Override
                public void updateTree(AjaxRequestTarget target) {
                    Course course = courseManager.getCourseById(courseModel.getObject().getId());
                    updateTreeControl(target, new GenericDBModel<>(Course.class, course));
                    target.appendJavaScript("formatTreeItem();");
                }
            };

            @Override
            public void replacePanel() {
                courseModel.detach();
                contentContainer.replace(panel);
            }

            @Override
            public boolean isDemo() {
                return true;
            }

            @Override
            protected void onItemClicked(AjaxRequestTarget target, CourseItem item) {
                super.onItemClicked(target, item);
                panel.updateCurrentItem(target, item, this.getCurrentId());
                target.appendJavaScript("formatTreeItem();");
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                panel.cleanPanel(target);
            }
        };
        add(demoLink.setOutputMarkupId(true));

        SimpleSelector fullLink = new CourseSelectorLink("full", courseModel) {
            private CourseContentEditPanel panel = new CourseContentEditPanel("contentPanel",
                    new GenericDBModel<>(Course.class, courseModel.getObject()),
                    Course.CONTENT_KIND.FULL) {
                @Override
                public void updateTree(AjaxRequestTarget target) {
                    Course course = courseManager.getCourseById(courseModel.getObject().getId());
                    updateTreeControl(target, new GenericDBModel<>(Course.class, course));
                    target.appendJavaScript("formatTreeItem();");
                }
            };

            @Override
            public void replacePanel() {
                courseModel.detach();
                contentContainer.replace(panel);
            }

            @Override
            public boolean isDemo() {
                return false;
            }

            @Override
            protected void onItemClicked(AjaxRequestTarget target, CourseItem item) {
                super.onItemClicked(target, item);
                panel.updateCurrentItem(target, item, this.getCurrentId());
                target.appendJavaScript("formatTreeItem();");
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                panel.cleanPanel(target);
            }

            @Override
            public void updateTreeControl(AjaxRequestTarget target, IModel<Course> courseIModel) {
                super.updateTreeControl(target, courseIModel);
                panel.updatePreJoinContainer(target);
            }
        };
        add(fullLink.setOutputMarkupId(true));

        contentContainer.add(new EmptyPanel("contentPanel"));
        add(contentContainer.setOutputMarkupId(true));

        add(new EltiAjaxLink("usersButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseListenersPage.class, new PageParameters().
                        add(CourseListenersPage.PARAM_ID, courseIModel.getObject().getId()));
            }
        });

        add(new EltiAjaxLink("createForumButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("forumCreateConfirmation")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                ForumGroup group = forumGroupManager.getForumGroupByName(getString("groupName"));
                Forum forum = new Forum();
                forum.setForumgroup(group);
                forum.setCourse(courseModel.getObject());
                forum.setName(String.format(getString("forumName"), courseModel.getObject().getName()));

                try {
                    forumManager.createForum(forum);
                    courseModel.getObject().setForum(forum);
                    courseManager.updateCourse(courseModel.getObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create forum for course", e);
                    throw new WicketRuntimeException("Cannot create forum for course", e);
                }

                ELTAlerts.renderOKPopup(getString("forumCreatedMessage"), target);
                setVisible(false);
                target.add(this);
            }

            @Override
            public boolean isVisible() {
                return (courseModel.getObject().getForum() == null);
            }
        });

        currentPage = null;
    }

    private abstract class SimpleSelector extends SelectorPanel {

        /**
         * Link constructor.
         *
         * @param id markup id.
         */
        public SimpleSelector(String id) {
            super(id);
        }

        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!isActive()) {
                setCurrentSelection(target, this);
                replacePanel();
                target.add(contentContainer);
            }
        }

        protected boolean isActive() {
            return (currentPage != null) && (currentPage.getMarkupId().equals(this.getMarkupId()));
        }

        public abstract void replacePanel();
    }


    private abstract class CourseSelectorLink extends SimpleSelector {

        private IModel<Course> courseIModel = new GenericDBModel<>(Course.class);
        private IModel<CourseItem> courseItemIModel = new GenericDBModel<>(CourseItem.class);

        private CourseTree tree;
        private CourseControlPanel controlPanel;
        private boolean isToggled = false;
        private boolean isSelectedItem = false;

        /**
         * Link constructor.
         *
         * @param id          markup id.
         * @param courseModel model of course.
         */
        public CourseSelectorLink(String id, IModel<Course> courseModel) {
            super(id);

            courseIModel.setObject(courseModel.getObject());

            WebMarkupContainer infoContainer = new WebMarkupContainer("itemContainer");
            add(infoContainer);

            infoContainer.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    isToggled = !isToggled;
                    target.add(CourseSelectorLink.this);
                }
            });

            WebMarkupContainer preview = new WebMarkupContainer("preview");
            infoContainer.add(preview);
            preview.add(new AjaxEventBehavior("onclick") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    throw new RestartResponseException(CourseContentPage.class,
                            new PageParameters().add(CourseContentPage.PARAM_ID, courseIModel.getObject().getId())
                                    .add(CourseContentPage.PARAM_KIND, isDemo() ?
                                            CourseContentPage.DEMO_KIND : CourseContentPage.FULL_KIND));
                }
            });
            preview.add(new AttributeModifier("title", getString("preview")));
            preview.add(new TooltipBehavior());

            tree = new CourseTree("tree", new CourseTreeModel(courseIModel.getObject(), isDemo())) {
                @Override
                public boolean isVisible() {
                    return isToggled && isActive();
                }

                @Override
                protected void onNodeLinkClicked(AjaxRequestTarget target, ELTTreeNode node) {
                    super.onNodeLinkClicked(target, node);
                    onItemClicked(target, (node instanceof VirtualCourseRootItem) ? null : (CourseItem) node);
                }

                @Override
                protected void onJunctionLinkClicked(AjaxRequestTarget target, ELTTreeNode node) {
                    super.onJunctionLinkClicked(target, node);
                    target.appendJavaScript("formatTreeItem();");
                }
            };

            add(tree.setOutputMarkupPlaceholderTag(true));

            controlPanel = new CourseControlPanel("controlPanel", courseIModel,
                    isDemo() ? Course.CONTENT_KIND.DEMO : Course.CONTENT_KIND.FULL) {
                @Override
                public boolean isVisible() {
                    return isToggled && isActive();
                }

                @Override
                protected boolean deleteEnabled() {
                    return isSelectedItem;
                }

                @Override
                protected CourseItem getCurrentItem() {
                    return courseItemIModel.getObject();
                }

                @Override
                protected void updateTree(AjaxRequestTarget target) {
                    Course course = courseManager.getCourseById(courseIModel.getObject().getId());
                    updateTreeControl(target, new GenericDBModel<>(Course.class, course));
                    target.appendJavaScript("formatTreeItem();");
                }

                @Override
                protected void onDelete(AjaxRequestTarget target) {
                    CourseSelectorLink.this.onDelete(target);
                }
            };
            add(controlPanel.setOutputMarkupPlaceholderTag(true));
        }

        public boolean isDemo() {
            return false;
        }

        public Long getCurrentId() {
            return tree.getCurrendId();
        }

        protected void onItemClicked(AjaxRequestTarget target, CourseItem item) {
            isSelectedItem = (item != null);
            courseItemIModel.setObject(item);
            target.add(controlPanel);
        }

        public void updateTreeControl(AjaxRequestTarget target, IModel<Course> courseIModel) {
            this.courseIModel.setObject(courseIModel.getObject());
            tree.setModelObject((new CourseTreeModel(courseIModel.getObject(), isDemo())));
            target.add(tree);
            tree.getTreeState().expandAll();
        }

        protected abstract void onDelete(AjaxRequestTarget target);
    }

    private void setCurrentSelection(AjaxRequestTarget target, WebMarkupContainer selection) {
        selection.add(new AttributeModifier("class", "menuitem menuitempushed"));
        if (currentPage != null) {
            currentPage.add(new AttributeModifier("class", "menuitem menuitemfree"));
            target.add(currentPage);
        }
        target.add(selection);
        currentPage = selection;
    }
}
