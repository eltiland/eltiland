package com.eltiland.ui.worktop;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.IAuthorizedPage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.ProfilePanel;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.servlet.http.HttpServletResponse;

/**
 * Main page for user profile.
 *
 * @author Aleksey Plotnikov.
 */
public class BaseWorktopPage extends BaseEltilandPage implements IAuthorizedPage {

    public static final String MOUNT_PATH = "/worktop";

    /**
     * Tab id for simple user panel.
     */
    public static final String PARAM_ID = "tab";
    /**
     * User id. Acceptable only for admin.
     */
    public static final String PARAM_USER = "user";

    @SpringBean
    private UserManager userManager;
    @SpringBean
    private GenericManager genericManager;

    public BaseWorktopPage() {
        User user = userManager.initializeSimpleUserInfo(EltilandSession.get().getCurrentUser());

        if (user == null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new ProfilePanel("panel", new GenericDBModel<>(User.class, user)));
    }

    public BaseWorktopPage(PageParameters parameters) {
        String userId = parameters.get(PARAM_USER).toString();
        String tabId = parameters.get(PARAM_ID).toString();

        User user;
        User currentUser = EltilandSession.get().getCurrentUser();
        if (userId != null && currentUser.isSuperUser()) {
            user = userManager.initializeSimpleUserInfo(genericManager.getObject(User.class, Long.parseLong(userId)));
        } else {
            user = userManager.initializeSimpleUserInfo(currentUser);
        }

        if (tabId != null) {
            add(new ProfilePanel("panel", new GenericDBModel<>(User.class, user), Integer.parseInt(tabId)));
        } else {
            add(new ProfilePanel("panel", new GenericDBModel<>(User.class, user)));
        }
    }
}
