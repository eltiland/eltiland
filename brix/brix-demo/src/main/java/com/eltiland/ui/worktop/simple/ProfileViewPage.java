package com.eltiland.ui.worktop.simple;

import com.eltiland.bl.user.UserManager;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.worktop.simple.panel.ProfileViewPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profile view page.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileViewPage extends BaseEltilandPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileViewPage.class);

    @SpringBean
    private UserManager userManager;

    /**
     * Page mount path(url).
     */
    public static final String MOUNT_PATH = "/user";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    public ProfileViewPage(PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        User user = userManager.getUserById(parameters.get(PARAM_ID).toLong());

        ProfileViewPanel panel = new ProfileViewPanel("profilePanel",
                new GenericDBModel<>(User.class, user)) {
            @Override
            public boolean isViewMode() {
                return true;
            }
        };
        add(panel);
    }
}
