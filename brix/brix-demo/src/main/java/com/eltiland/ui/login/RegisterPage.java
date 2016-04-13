package com.eltiland.ui.login;

import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.TwoColumnPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.login.panels.RegistrationPanel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.servlet.http.HttpServletResponse;

/**
 * Page for user registration.
 *
 * @author Aleksey Plotnikov.
 */
public class RegisterPage extends TwoColumnPage {

    public static final String MOUNT_PATH = UrlUtils.REGISTER_PAGE_MOUNT_PATH;

    public static final String PARAM_COURSE = "course";

    public RegisterPage(PageParameters parameters) {

        Long courseId = null;
        if (parameters.getNamedKeys().contains(PARAM_COURSE)) {
            courseId = Long.parseLong(parameters.get(PARAM_COURSE).toString());
        }

        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser != null) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }

        add(new RegistrationPanel("panel", courseId));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_LOGIN);
    }
}
