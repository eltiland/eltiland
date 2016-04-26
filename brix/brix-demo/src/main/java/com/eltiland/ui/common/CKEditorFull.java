package com.eltiland.ui.common;

import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * CKEditor panel
 *
 * @author Pavel Androschuk
 */
public class CKEditorFull extends Panel {
    private TextArea<String> container;

    public void setData(String value) {
        container.setModelObject(value);
    }

    public String getData() {
        return container.getConvertedInput();
    }

    public CKEditorFull(String id, Dialog dialog) {
        super(id);

        container = new TextArea<>("container", new Model<String>());
        container.setOutputMarkupId(true);
        container.setEscapeModelStrings(false);
        add(container);

        Label script = new Label("enableScript", new Model<>(enableScript()));
        script.setEscapeModelStrings(false);
        add(script);

        // Fix the bug then CKEditor become disabled in Chrome
        // After page refresh its become active
        if (dialog != null) {
            dialog.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
                @Override
                public void onClose(AjaxRequestTarget target) {
                    setResponsePage(getPage());
                }
            });
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderJavaScriptReference(ResourcesUtils.CKEDITOR_JS);
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
        response.renderJavaScriptReference(ResourcesUtils.JS_YASHARE);
        response.renderJavaScriptReference(ResourcesUtils.JS_INDICATOR_FUNCTION);
    }

    public String enableScript() {
        return String.format("var editor = CKEDITOR.replace('%s', { baseFloatZIndex: 1000000 }); " +
                "editor.on('change', function( evt ) { " +
                "var data = evt.editor.getData(); " +
                "$('#%s').text(data); " +
                "});",
                container.getMarkupId(), container.getMarkupId());
    }
}
