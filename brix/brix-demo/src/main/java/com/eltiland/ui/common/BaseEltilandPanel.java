package com.eltiland.ui.common;

import com.eltiland.ui.common.components.ResourcesUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * Base eltiland panel.
 *
 * @author Aleksey Plotnikov
 */
public abstract class BaseEltilandPanel<T> extends GenericPanel<T> {
    public BaseEltilandPanel(String id) {
        super(id);
    }

    protected BaseEltilandPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(ResourcesUtils.CSS_JQUERY);
        response.renderCSSReference(ResourcesUtils.CSS_ELT_STYLE);
        response.renderCSSReference(ResourcesUtils.CSS_COMPONENTS);

        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);

        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_UI);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_COMPONENTS);
        response.renderJavaScriptReference(ResourcesUtils.JS_JQUERY_FUNCTION);
        response.renderJavaScriptReference(ResourcesUtils.JS_VISUAL_EFECTS);
       // response.renderJavaScriptReference(ResourcesUtils.JS_YASHARE);
        response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR_FUNCTION);
    }
}
