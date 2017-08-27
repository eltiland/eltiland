/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.demo.web.admin;

import com.eltiland.bl.HtmlCleaner;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import org.apache.wicket.markup.html.GenericWebPage;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.web.admin.AdminPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

/**
 * This page hosts Brix's {@link AdminPanel}
 *
 * @author igor.vaynberg
 */
public class AdminPage extends GenericWebPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminPage.class);

    @SpringBean
    private HtmlCleaner htmlCleaner;


    /**
     * Constructor
     */
    public AdminPage() {
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null || !(currentUser.isSuperUser())) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        LOGGER.info("User {} is entered to admin page", currentUser.getName());
        add(new AdminPanel("admin", null));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_ELT_STYLE);
        response.renderCSSReference(ResourcesUtils.CSS_JQUERY);
        response.renderCSSReference(ResourcesUtils.CSS_COMPONENTS);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE);
        response.renderCSSReference(ResourcesUtils.CSS_STATIC_CONTENT);
        response.renderCSSReference(ResourcesUtils.CSS_VIDEO);
        response.renderCSSReference(ResourcesUtils.CSS_FORUM);
        response.renderCSSReference(ResourcesUtils.CSS_MAGAZINE);
        response.renderCSSReference(ResourcesUtils.CSS_WEBINAR);
        response.renderCSSReference(ResourcesUtils.CSS_ICONPANEL);
        response.renderCSSReference(ResourcesUtils.CSS_SUBSCRIBE);

        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);

        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_UI);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_COMPONENTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_FUNCTION);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_NUMBERFORMATTER);
        //response.renderJavaScriptReference(ResourcesUtils.JS_YASHARE);
        response.renderJavaScriptReference(ResourcesUtils.JS_TIMEPICKER);

        response.renderOnDomReadyJavaScript(String.format("tryRegisterWicketAjaxOnFailure('%s')",
                getString("unreachableServerMessage").replaceAll("\\n", "")));
        response.renderOnDomReadyJavaScript("createShare();");
        response.renderOnDomReadyJavaScript("createShare();");
        response.renderOnDomReadyJavaScript("document.body.style.backgroundImage=\"none\"");
        response.renderOnDomReadyJavaScript("document.body.style.backgroundColor=\"whitesmoke\"");

        EltiStaticAlerts.renderOKPopups(response);
        EltiStaticAlerts.renderErrorPopups(response);
    }
}

