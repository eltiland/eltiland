package com.eltiland.ui.worktop.simple;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.components.SelectorLink;
import com.eltiland.ui.worktop.simple.panel.ProfileCoursePanel;
import com.eltiland.ui.worktop.simple.panel.ProfileEditPanel;
import com.eltiland.ui.worktop.simple.panel.ProfileFilesPanel;
import com.eltiland.ui.worktop.simple.panel.ProfileWebinarPanel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Base profile panel for simple user.
 *
 * @author Aleksey Plotnikov
 */
public class ProfilePanel extends BaseEltilandPanel<User> {
    @SpringBean
    private UserManager userManager;
    @SpringBean
    private FileManager fileManager;

    private static final String PROFILE_CLASS = "profile";
    private static final String COURSE_CLASS = "course";
    private static final String WEBINAR_CLASS = "webinar";
    private static final String TRAINING_CLASS = "training";
    private static final String FILES_CLASS = "files";

    private WebMarkupContainer currentPage;
    private AvatarPreviewPanel avatarPanel = new AvatarPreviewPanel("avatarPanel",
            UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT) {
        @Override
        protected boolean isLabelVisible() {
            return false;
        }

        @Override
        protected boolean isEditLinkVisible() {
            return currentPage.getMarkupId().equals(editLink.getMarkupId());
        }
    };

    private final SelectorLink editLink = new SelectorLink("editLink") {
        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!(editLink.getMarkupId().equals(currentPage.getMarkupId()))) {
                informationContainer.replace(new ProfileEditPanel("informationPanel", ProfilePanel.this.getModel()) {
                    @Override
                    protected void saveAvatar(User user, AjaxRequestTarget target) {
                        if (avatarPanel.isDataWasLost()) {
                            ELTAlerts.renderErrorPopup(getString("AvatarDataWasLost"), target);
                            return;
                        }
                        user.setAvatar(avatarPanel.getAvatarFile());
                    }
                });
                setCurrentSelection(target, editLink);
            }
        }

        @Override
        public String getLabelText() {
            return getString("profile.general");
        }

        @Override
        public IModel<String> getIconModelClass() {
            return new Model<>(PROFILE_CLASS);
        }
    };

    private final SelectorLink trainingLink = new SelectorLink("trainingLink") {
        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!(trainingLink.getMarkupId().equals(currentPage.getMarkupId()))) {
                User user = userManager.initializeSimpleUserInfo(ProfilePanel.this.getModelObject());
                informationContainer.replace(new ProfileCoursePanel("informationPanel",
                        new GenericDBModel<>(User.class, user)) {
                    @Override
                    protected boolean isTraining() {
                        return true;
                    }
                });
                setCurrentSelection(target, trainingLink);
            }
        }

        @Override
        public String getLabelText() {
            return getString("profile.training");
        }

        @Override
        public IModel<String> getIconModelClass() {
            return new Model<>(TRAINING_CLASS);
        }
    };


    private final SelectorLink coursesLink = new SelectorLink("coursesLink") {
        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!(coursesLink.getMarkupId().equals(currentPage.getMarkupId()))) {
                User user = userManager.initializeSimpleUserInfo(ProfilePanel.this.getModelObject());
                informationContainer.replace(new ProfileCoursePanel("informationPanel",
                        new GenericDBModel<>(User.class, user)) {
                    @Override
                    protected boolean isTraining() {
                        return false;
                    }
                });
                setCurrentSelection(target, coursesLink);
            }
        }

        @Override
        public String getLabelText() {
            return getString("profile.courses");
        }

        @Override
        public IModel<String> getIconModelClass() {
            return new Model<>(COURSE_CLASS);
        }
    };

    private final SelectorLink webinarsLink = new SelectorLink("webinarsLink") {
        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!(webinarsLink.getMarkupId().equals(currentPage.getMarkupId()))) {
                User user = userManager.initializeSimpleUserInfo(ProfilePanel.this.getModelObject());
                informationContainer.replace(new ProfileWebinarPanel("informationPanel",
                        new GenericDBModel<>(User.class, user)));
                setCurrentSelection(target, webinarsLink);
            }
        }

        @Override
        public String getLabelText() {
            return getString("profile.webinars");
        }

        @Override
        public IModel<String> getIconModelClass() {
            return new Model<>(WEBINAR_CLASS);
        }
    };

    private final SelectorLink filesLink = new SelectorLink("filesLink") {
        @Override
        public void changeSelection(AjaxRequestTarget target) {
            if (!(filesLink.getMarkupId().equals(currentPage.getMarkupId()))) {
                User user = userManager.initializeSimpleUserInfo(ProfilePanel.this.getModelObject());
                informationContainer.replace(new ProfileFilesPanel(
                        "informationPanel", new GenericDBModel<>(User.class, user)));
                setCurrentSelection(target, filesLink);
            }
        }

        @Override
        public String getLabelText() {
            return getString("profile.files");
        }

        @Override
        public IModel<String> getIconModelClass() {
            return new Model<>(FILES_CLASS);
        }
    };

    private final WebMarkupContainer informationContainer = new WebMarkupContainer("informationContainer");

    /**
     * Base profile panel for simple user.
     *
     * @param id        markup id.
     * @param userModel simple user model.
     */
    public ProfilePanel(String id, IModel<User> userModel) {
        this(id, userModel, 1);
    }

    public ProfilePanel(String id, IModel<User> userModel, int currentTab) {
        super(id, userModel);

        setCurrentSelection(null, editLink);
        currentPage = editLink;
        add(informationContainer.setOutputMarkupId(true));
        informationContainer.add(new ProfileEditPanel("informationPanel", userModel) {
            @Override
            protected void saveAvatar(User user, AjaxRequestTarget target) {
                if (avatarPanel.isDataWasLost()) {
                    ELTAlerts.renderErrorPopup(getString("AvatarDataWasLost"), target);
                    return;
                }
                user.setAvatar(avatarPanel.getAvatarFile());
            }
        });

        if (currentTab == 1) {
            editLink.changeSelection(null);
        } else if (currentTab == 2) {
            coursesLink.changeSelection(null);
        } else if (currentTab == 3) {
            trainingLink.changeSelection(null);
        } else if (currentTab == 4) {
            webinarsLink.changeSelection(null);
        } else if (currentTab == 5) {
            filesLink.changeSelection(null);
        }

        User user = userModel.getObject();

        add(avatarPanel.setOutputMarkupId(true));

        if (user.getAvatar() != null) {
            File file = fileManager.getFileById(user.getAvatar().getId());
            avatarPanel.initEditMode(new GenericDBModel<>(File.class, file));
        }
        add(editLink);
        add(coursesLink);
        add(trainingLink);
        add(webinarsLink);
        add(filesLink);
    }

    private void setCurrentSelection(AjaxRequestTarget target, WebMarkupContainer selection) {
        selection.add(new AttributeModifier("class", "menuItemBase menuItemPushed"));
        if (currentPage != null) {
            currentPage.add(new AttributeModifier("class", "menuItemBase menuItemFree"));
        }

        if (target != null) {
            target.add(selection);
            target.add(currentPage);
        }

        currentPage = selection;

        if (target != null) {
            target.add(avatarPanel);
            target.add(informationContainer);
        }
    }
}
