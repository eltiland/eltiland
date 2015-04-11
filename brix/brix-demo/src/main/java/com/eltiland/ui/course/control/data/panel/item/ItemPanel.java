package com.eltiland.ui.course.control.data.panel.item;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.course.CourseEditPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for output name of the item and it's actions.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class ItemPanel extends BaseEltilandPanel<ELTCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<String> nameModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            return ItemPanel.this.getModelObject().getName();
        }
    };

    private Dialog<ItemTypeSelector> typeSelectorDialog = new Dialog<ItemTypeSelector>("typeSelector", 465) {
        @Override
        public ItemTypeSelector createDialogPanel(String id) {
            return new ItemTypeSelector(id) {
                @Override
                protected boolean isGroupVisible() {
                    return false;
                }
            };
        }

        @Override
        public void registerCallback(ItemTypeSelector panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                    model.getObject().setBlock(null);
                    model.getObject().setParent((ELTGroupCourseItem) ItemPanel.this.getModelObject());

                    genericManager.initialize(ItemPanel.this.getModelObject(),
                            ((ELTGroupCourseItem) ItemPanel.this.getModelObject()).getItems());
                    model.getObject().setIndex((long)
                            ((ELTGroupCourseItem) ItemPanel.this.getModelObject()).getItems().size());

                    propertyDialog.getDialogPanel().initData(model);
                    propertyDialog.show(target);
                }
            });
        }
    };

    private Dialog<ItemPropertyPanel> propertyDialog = new Dialog<ItemPropertyPanel>("itemPropertyDialog", 440) {
        @Override
        public ItemPropertyPanel createDialogPanel(String id) {
            return new ItemPropertyPanel(id);
        }

        @Override
        public void registerCallback(ItemPropertyPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        courseItemManager.create(model.getObject());
                        onAdd(target);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseItem>() {
                @Override
                public void process(IModel<ELTCourseItem> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        courseItemManager.update(model.getObject());
                        nameModel.detach();
                        target.add(item);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    private WebMarkupContainer subItemsContainer = new WebMarkupContainer("subItemContainer");
    private WebMarkupContainer item = new WebMarkupContainer("item");

    private IModel<List<ELTCourseItem>> subItemModel = new LoadableDetachableModel<List<ELTCourseItem>>() {
        @Override
        protected List<ELTCourseItem> load() {
            if (!(ItemPanel.this.getModelObject() instanceof ELTGroupCourseItem)) {
                return new ArrayList<>();
            } else {
                return courseItemManager.getItems((ELTGroupCourseItem) ItemPanel.this.getModelObject());
            }
        }
    };

    /**
     * Panel ctor.
     *
     * @param id                  markup id.
     * @param eltCourseItemIModel course item model.
     */
    public ItemPanel(String id, IModel<ELTCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        item.add(new Label("name", nameModel));
        item.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                onClick(target, getModel());
            }
        });
        add(item.setOutputMarkupId(true));

        WebMarkupContainer image = new WebMarkupContainer("image");
        item.add(image);
        image.add(new AttributeModifier("class", new Model<>(
                "image " + getString(getModelObject().getClass().getSimpleName() + ".class"))));

        EltiAjaxLink addButton = new EltiAjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                typeSelectorDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                return ItemPanel.this.getModelObject() instanceof ELTGroupCourseItem;
            }
        };


        EltiAjaxLink upButton = new EltiAjaxLink("up") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onUp(target);
            }

            @Override
            public boolean isVisible() {
                return canBeMovedUp();
            }
        };

        EltiAjaxLink downButton = new EltiAjaxLink("down") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onDown(target);
            }

            @Override
            public boolean isVisible() {
                return canBeMovedDown();
            }
        };

        EltiAjaxLink cancelButton = new EltiAjaxLink("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
            }
        };

        item.add(addButton);
        item.add(upButton);
        item.add(downButton);
        item.add(cancelButton);
        add(typeSelectorDialog);
        add(propertyDialog);

        upButton.add(new AttributeModifier("title", ItemPanel.this.getString("up.tooltip")));
        upButton.add(new TooltipBehavior());

        downButton.add(new AttributeModifier("title", ItemPanel.this.getString("down.tooltip")));
        downButton.add(new TooltipBehavior());

        cancelButton.add(new AttributeModifier("title", ItemPanel.this.getString("delete.tooltip")));
        cancelButton.add(new TooltipBehavior());

        addButton.add(new AttributeModifier("title", ItemPanel.this.getString("add.tooltip")));
        addButton.add(new TooltipBehavior());

        add(subItemsContainer.setOutputMarkupId(true));
        subItemsContainer.add(new ListView<ELTCourseItem>("subItems", subItemModel) {
            @Override
            protected void populateItem(final ListItem<ELTCourseItem> item) {
                item.add(new ItemPanel("subItem", item.getModel()) {
                    @Override
                    protected boolean canBeMovedUp() {
                        return item.getModelObject().getIndex() > 0;
                    }

                    @Override
                    protected boolean canBeMovedDown() {
                        Long index = item.getModelObject().getIndex();
                        genericManager.initialize(item.getModelObject(), item.getModelObject().getParent());
                        genericManager.initialize(item.getModelObject().getParent(),
                                item.getModelObject().getParent().getItems());
                        int count = item.getModelObject().getParent().getItems().size();
                        return (index + 1) < count;
                    }

                    @Override
                    protected void onUp(AjaxRequestTarget target) {
                        try {
                            courseItemManager.moveUp(item.getModelObject());
                            subItemModel.detach();
                            target.add(subItemsContainer);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }

                    @Override
                    protected void onDown(AjaxRequestTarget target) {
                        try {
                            courseItemManager.moveDown(item.getModelObject());
                            subItemModel.detach();
                            target.add(subItemsContainer);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }
                });
            }
        });
    }

    protected abstract boolean canBeMovedUp();

    protected abstract boolean canBeMovedDown();

    protected abstract void onUp(AjaxRequestTarget target);

    protected abstract void onDown(AjaxRequestTarget target);

    private void onClick(AjaxRequestTarget target, IModel<ELTCourseItem> itemModel) {
        if (itemModel.getObject() instanceof ELTGroupCourseItem) {
            propertyDialog.getDialogPanel().initData(itemModel);
            propertyDialog.show(target);
        } else {
            throw new RestartResponseException(CourseEditPage.class,
                    new PageParameters().add(CourseEditPage.PARAM_ID, itemModel.getObject().getId()));
        }
    }

    protected void onAdd(AjaxRequestTarget target) {
    }
}
