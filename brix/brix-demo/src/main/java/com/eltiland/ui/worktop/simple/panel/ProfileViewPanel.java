package com.eltiland.ui.worktop.simple.panel;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.blocks.AboutViewBlock;
import com.eltiland.ui.worktop.simple.blocks.AchieveViewBlock;
import com.eltiland.ui.worktop.simple.blocks.ContactInfoViewBlock;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Profile panel - view information.
 *
 * @author Aleksey PLotnikov
 */
public class ProfileViewPanel extends BaseEltilandPanel<User> {

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private FileManager fileManager;

    private AvatarPreviewPanel avatarPanel = new AvatarPreviewPanel("avatarPanel",
            UrlUtils.StandardIcons.ICONS_DEFAULT_PARENT) {
        @Override
        protected boolean isDescriptionVisible() {
            return false;
        }

        @Override
        public boolean isVisible() {
            return isViewMode();
        }
    };

    /**
     * Panel constructor.
     *
     * @param id               markup id.
     * @param userModel user model.
     */
    public ProfileViewPanel(String id, IModel<User> userModel) {
        super(id, userModel);

        add(avatarPanel.setOutputMarkupId(true));
        setOutputMarkupId(true);

        setModelObject(userManager.initializeAvatarInfo(userModel.getObject()));

        if (getModelObject().getAvatar() != null) {
            File file = fileManager.getFileById(getModelObject().getAvatar().getId());
            avatarPanel.initEditMode(new GenericDBModel<>(File.class, file));
        }

        add(new AboutViewBlock("aboutBlock", userModel) {
            @Override
            public boolean isVisible() {
                String information = ((User) getModelObject()).getInformation();
                return information != null && information.isEmpty();
            }
        });
        add(new AchieveViewBlock("achieveBlock", userModel) {
            @Override
            public boolean isVisible() {
                String achievements = ((User) getModelObject()).getAchievements();
                return achievements != null && achievements.isEmpty();
            }
        });
        add(new ContactInfoViewBlock("infoBlock", userModel));
    }

    public boolean isViewMode() {
        return false;
    }
}
