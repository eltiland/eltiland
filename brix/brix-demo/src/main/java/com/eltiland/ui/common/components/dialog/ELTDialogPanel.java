package com.eltiland.ui.common.components.dialog;

import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.captcha.CaptchaPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * Abstract panel for dialog.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ELTDialogPanel extends BaseEltilandPanel {

    public enum EVENT {
        Send,
        Apply,
        Save,
        Create,
        Register,
        Add,
        Select,
        AddUser,
        Yes,
        No
    }

    private Label headerLabel = new Label("header", new Model<String>());
    private CaptchaPanel captchaPanel = new CaptchaPanel("captchaPanel");
    protected Form form = new Form("form");

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public ELTDialogPanel(String id) {
        super(id);
        addComponents();
    }

    @Override
    public String getVariation() {
        return "standart";
    }

    protected ELTDialogPanel(String id, IModel iModel) {
        super(id, iModel);
        addComponents();
    }

    private void addComponents() {
        add(headerLabel);
        headerLabel.setDefaultModelObject(getHeader());

        form.add(captchaPanel);
        captchaPanel.setVisible(showCaptcha());

        form.add(new ListView<EVENT>("buttonListView", getActionList()) {
            @Override
            protected void populateItem(final ListItem<EVENT> components) {
                components.add(new Button("button", components.getModelObject()) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        eventHandler(components.getModelObject(), target);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target) {
                        ELTDialogPanel.this.onError(target);
                    }

                    @Override
                    public boolean isVisible() {
                        return actionSelector(components.getModelObject());
                    }
                });
            }

            @Override
            public boolean isVisible() {
                return !(getActionList().isEmpty());
            }
        });

        add(form);
    }

    /**
     * @return header of the dialog.
     */
    protected abstract String getHeader();

    /**
     * @return list of the actions.
     */
    protected abstract List<EVENT> getActionList();

    /**
     * Function, which representing logic of showing action buttons.
     *
     * @param event event
     * @return TRUE if action EVENT must be shown.
     */
    protected boolean actionSelector(EVENT event) {
        return true;
    }

    /**
     * Callback for clicking on the buttons.
     *
     * @param event  event
     * @param target ajax target.
     */
    protected abstract void eventHandler(EVENT event, AjaxRequestTarget target);


    /**
     * Override it to show captcha.
     *
     * @return TRUE - if captcha should be visible.
     */
    protected boolean showCaptcha() {
        return false;
    }

    /**
     * Internal class for button.
     *
     * @author Aleksey Plotnikov.
     */
    private abstract class Button extends BaseEltilandPanel {

        private EVENT event;

        public Button(String id, EVENT event) {
            super(id);
            this.event = event;

            EltiAjaxSubmitLink link = new EltiAjaxSubmitLink("button") {
                @Override
                protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form components) {
                    onClick(ajaxRequestTarget);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form form) {
                    super.onError(target, form);
                    Button.this.onError(target);
                }

                @Override
                protected IAjaxCallDecorator getAjaxCallDecorator() {
                    if (!showButtonDecorator()) {
                        return null;
                    } else {
                        return super.getAjaxCallDecorator();
                    }
                }
            };
            add(link);

            link.add(new Label("buttonLabel", getString(event.toString())));
        }

        protected abstract void onClick(AjaxRequestTarget target);

        protected abstract void onError(AjaxRequestTarget target);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        headerLabel.setDefaultModelObject(getHeader());
    }

    protected boolean showButtonDecorator() {
        return false;
    }

    protected void onError(AjaxRequestTarget target) {

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_DIALOG);
    }
}
