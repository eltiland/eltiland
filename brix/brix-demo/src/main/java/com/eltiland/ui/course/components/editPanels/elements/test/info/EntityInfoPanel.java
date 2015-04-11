package com.eltiland.ui.course.components.editPanels.elements.test.info;

import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestEntity;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for output information about entity with button edit and remove.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class EntityInfoPanel<T extends TestEntity> extends BaseEltilandPanel<T> {

    private final int EDIT_DIALOG_WIDTH = 330;
    protected boolean isActive = false;

    protected static final Logger LOGGER = LoggerFactory.getLogger(EntityInfoPanel.class);

    @SpringBean
    private GenericManager genericManager;

    protected WebMarkupContainer infoContainer = new WebMarkupContainer("infoContainer");

    /**
     * Panel constructor.
     *
     * @param id      markup id.
     * @param tiModel entity model.
     */
    protected EntityInfoPanel(String id, final IModel<T> tiModel) {
        super(id, tiModel);

        add(infoContainer);

        String textValue = showNumbers() ? String.valueOf(getModelObject().getNumber() + 1) + ". " : "";
        String value = getModelObject().getTextValue();
        if (value != null && !value.isEmpty()) {
            textValue += value;
        }

        Label label = new Label("valueField", textValue);
        if (isLabelBold()) {
            label.add(new AttributeAppender("style", "font-weight: bold"));
        }

        infoContainer.add(label);
        Component component = getAdditionInfoComponent();
        if (component != null) {
            infoContainer.add(component);
        }
        Component component2 = getAdditionInfoComponent2();
        if (component2 != null) {
            infoContainer.add(component2);
        }
        Component component3 = getAdditionInfoComponent3();
        if (component3 != null) {
            infoContainer.add(component3);
            component3.setVisible(isRightResult());
        }

        Component childList = getChildComponent();
        if (childList != null) {
            infoContainer.add(childList);
        }

        EltiAjaxLink editButton = new EltiAjaxLink("editButton") {
            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                onEdit(target, tiModel);
            }
        };

        EltiAjaxLink upButton = new EltiAjaxLink("upButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onMoveUp(tiModel.getObject());
                updateList(target);
            }

            @Override
            public boolean isVisible() {
                return canBeMovedUp();
            }
        };

        EltiAjaxLink downButton = new EltiAjaxLink("downButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onMoveDown(tiModel.getObject());
                updateList(target);
            }

            @Override
            public boolean isVisible() {
                return canBeMovedDown();
            }
        };

        EltiAjaxLink removeButton = new EltiAjaxLink("removeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    onDelete(tiModel.getObject());
                    updateList(target);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Error while removing entity");
                    throw new WicketRuntimeException("Error while removing entity", e);
                }
            }

            @Override
            public boolean isVisible() {
                return canBeDeleted();
            }
        };

        editButton.add(new AttributeModifier("title", new ResourceModel("editTooltip")));
        editButton.add(new TooltipBehavior());
        removeButton.add(new AttributeModifier("title", new ResourceModel("removeTooltip")));
        removeButton.add(new TooltipBehavior());
        upButton.add(new AttributeModifier("title", new ResourceModel("upTooltip")));
        upButton.add(new TooltipBehavior());
        downButton.add(new AttributeModifier("title", new ResourceModel("downTooltip")));
        downButton.add(new TooltipBehavior());

        infoContainer.add(removeButton);
        infoContainer.add(editButton);
        infoContainer.add(upButton);
        infoContainer.add(downButton);

        infoContainer.setOutputMarkupId(true);
        infoContainer.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if ((EntityInfoPanel.this.getModelObject() != null) && (!isSection())) {
                    String id = infoContainer.getMarkupId();
                    if (isActive) {
                        target.appendJavaScript("deSelectItems(null);");
                    } else {
                        target.appendJavaScript("deSelectItems('" + id + "');");
                    }
                    isActive = !isActive;
                }
            }
        });
    }

    /**
     * Must return additional info panel for entity.
     */
    protected Component getAdditionInfoComponent() {
        return null;
    }

    /**
     * Must return second additional info panel for entity.
     */
    protected Component getAdditionInfoComponent2() {
        return null;
    }

    /**
     * Must return third additional info panel for entity.
     */
    protected Component getAdditionInfoComponent3() {
        return null;
    }

    protected Component getChildComponent() {
        return null;
    }

    protected boolean isLabelBold() {
        return false;
    }

    /**
     * Callback for updating entity list.
     */
    protected abstract void updateList(AjaxRequestTarget target);

    /**
     * Callback for editing entity.
     */
    protected abstract void onEdit(AjaxRequestTarget target, IModel<T> model);

    /**
     * Delete handler.
     */
    protected void onDelete(T entity) throws EltilandManagerException {
        genericManager.delete(entity);
    }

    /**
     * Move up handler.
     */
    protected void onMoveUp(T entity) {
    }

    /**
     * Move up handler.
     */
    protected void onMoveDown(T entity) {
    }

    /**
     * Callback, returning true if entity can be deleted.
     */
    protected boolean canBeDeleted() {
        return true;
    }

    protected boolean showNumbers() {
        return false;
    }

    protected boolean isSection() {
        return false;
    }

    protected boolean canBeMovedUp() {
        return false;
    }

    protected boolean canBeMovedDown() {
        return false;
    }

    protected boolean isRightResult() {
        return false;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderOnDomReadyJavaScript("hideControls()");
    }
}
