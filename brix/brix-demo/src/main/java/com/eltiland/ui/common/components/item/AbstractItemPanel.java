package com.eltiland.ui.common.components.item;

import com.eltiland.model.Identifiable;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Abstract panel for showing item (course, record, webinar, and so on).
 *
 * @author Aleksey Plotnikov.
 */
public abstract class AbstractItemPanel<T extends Identifiable> extends BaseEltilandPanel<T> {

    public static final String CSS = "static/css/panels/item_panel.css";

    private WebMarkupContainer iconContainer = new WebMarkupContainer("iconContainer");

    /**
     * Panel ctor.
     *
     * @param id      markup id.
     * @param tiModel item model.
     */
    public AbstractItemPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);

        add(iconContainer.setOutputMarkupId(true));
        iconContainer.add(getIcon("icon"));

        iconContainer.add(new Label("type", getIconLabel()));

        add(new Label("name", getEntityName(tiModel)));
        add(new Label("description", getEntityDescription(tiModel)));

        add(new ListView<ButtonAction>("actionContainer", getActionList()) {
            @Override
            protected void populateItem(final ListItem<ButtonAction> item) {
                item.add(new IconButton("actionButton", getActionName(item.getModelObject()), item.getModelObject()) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        AbstractItemPanel.this.onClick(item.getModelObject(), target);
                    }

                    @Override
                    public boolean isVisible() {
                        return AbstractItemPanel.this.isVisible(item.getModelObject());
                    }

                    @Override
                    protected AbstractAjaxBehavior getAdditionalBehavior() {
                        return AbstractItemPanel.this.getAdditionalBehavior(item.getModelObject());
                    }

                    @Override
                    protected boolean hasConfirmation() {
                        return AbstractItemPanel.this.hasConfirmation(item.getModelObject());
                    }

                    @Override
                    protected IModel<String> getConfirmationText() {
                        return AbstractItemPanel.this.getConfirmationText(item.getModelObject());
                    }
                });
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    protected abstract WebComponent getIcon(String markupId);

    protected abstract String getIconLabel();

    protected abstract String getEntityName(IModel<T> itemModel);

    protected abstract String getEntityDescription(IModel<T> itemModel);

    protected abstract List<ButtonAction> getActionList();

    protected abstract IModel<String> getActionName(ButtonAction action);

    protected abstract boolean isVisible(ButtonAction action);

    protected abstract void onClick(ButtonAction action, AjaxRequestTarget target);

    protected AbstractAjaxBehavior getAdditionalBehavior(ButtonAction action) {
        return null;
    }

    protected boolean hasConfirmation(ButtonAction action) {
        return false;
    }

    protected IModel<String> getConfirmationText(ButtonAction action) {
        return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
    }
}
