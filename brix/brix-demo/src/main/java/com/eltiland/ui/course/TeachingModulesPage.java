package com.eltiland.ui.course;

import com.eltiland.ui.common.BaseEltilandPage;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Page with list of all teaching modules.
 *
 * @author Aleksey Plotnikov.
 */
public class TeachingModulesPage extends BaseEltilandPage {

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/modules";

    private final String CSS = "static/css/panels/course_list.css";

    public TeachingModulesPage() {
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
