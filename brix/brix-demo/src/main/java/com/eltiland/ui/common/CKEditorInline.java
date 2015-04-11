package com.eltiland.ui.common;

import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.BorderBehavior;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plugin to change <div> to CKEditor in inline mode
 *
 * @author Pavel Androchuk
 */
public abstract class CKEditorInline extends Behavior {
    private static final Logger LOGGER = LoggerFactory.getLogger(CKEditorInline.class);

    private AbstractDefaultAjaxBehavior ajaxCallback = new AbstractDefaultAjaxBehavior() {
        @Override
        protected void respond(AjaxRequestTarget target) {
            String data = RequestCycle.get().getRequest().getRequestParameters().getParameterValue("data").toString();
            onChanged(data, target);
        }
    };

    public abstract void onChanged(String data, AjaxRequestTarget target);

    public CKEditorInline() {
    }

    @Override
    public void afterRender(Component component) {
        component.getResponse().write(enableScript(component));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        tag.put("contenteditable", "true");
    }

    private String enableScript(Component component) {
        return String.format("<script>CKEDITOR.disableAutoInline = true; " +
                "var editor = CKEDITOR.inline('%s'); " +
                "editor.on('blur', function( evt ) { " +
                "var data = evt.editor.getData(); " +
                "renderCKEditorInline(data); " +
                "});</script>", component.getMarkupId());
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.renderJavaScriptReference(ResourcesUtils.CKEDITOR_JS);
        response.renderJavaScript("function renderCKEditorInline(data) {var wcall = wicketAjaxPost('" +
                ajaxCallback.getCallbackUrl() + "', 'data=' + encodeURIComponent(data))}", "renderCKEditorInline");
    }

    @Override
    public void bind(Component component) {
        if (component.getEscapeModelStrings()) {
            throw new WicketRuntimeException("Component need to use setEscapeModelStrings(false)");
        }

        component.setOutputMarkupId(true);
        component.add(ajaxCallback);
    }
}
