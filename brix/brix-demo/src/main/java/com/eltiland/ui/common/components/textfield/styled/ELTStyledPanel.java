package com.eltiland.ui.common.components.textfield.styled;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Styled form with one field and submit button.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTStyledPanel<T> extends BaseEltilandPanel {

    private FormComponent<T> editor;

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public ELTStyledPanel(String id) {
        super(id);

        Form form = new Form("form") {
            @Override
            protected void onSubmit() {
                super.onSubmit();    //To change body of overridden methods use File | Settings | File Templates.
            }
        };

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedbackPanel");
        feedbackPanel.setFilter(new IFeedbackMessageFilter() {
            @Override
            public boolean accept(FeedbackMessage message) {
                return message.isError();
            }
        });

        form.add(new EltiAjaxSubmitLink("submit") {

            @Override
            protected void onError(AjaxRequestTarget target, Form form) {
                target.add(feedbackPanel);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                target.add(feedbackPanel);
                ELTStyledPanel.this.onSubmit(target, editor.getModelObject());
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });

        editor = getEditor("textField");
        form.add(editor);
        editor.setRequired(true);

        add(form);
        add(feedbackPanel.setOutputMarkupId(true));
    }

    public void setRequired(boolean value) {
        editor.setRequired(value);
    }

    /**
     * Submit handler.
     */
    public abstract void onSubmit(AjaxRequestTarget target, T value);

    /**
     * Override it for specify editor
     *
     * @param markupId markup id.
     * @return form component.
     */
    protected abstract FormComponent<T> getEditor(String markupId);
}
