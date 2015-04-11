package com.eltiland.ui.course.control.data.panel.block;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.course.control.data.panel.item.ItemPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Block control panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class BlockControlPanel extends BaseEltilandPanel<ELTCourseBlock> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;

    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    private IModel<List<ELTCourseItem>> itemsModel = new LoadableDetachableModel<List<ELTCourseItem>>() {
        @Override
        protected List<ELTCourseItem> load() {
            return courseItemManager.getItems(BlockControlPanel.this.getModelObject());
        }
    };

    /**
     * Ctor.
     *
     * @param id                   markup id.
     * @param eltCourseBlockIModel course block model.
     */
    public BlockControlPanel(String id, IModel<ELTCourseBlock> eltCourseBlockIModel) {
        super(id, eltCourseBlockIModel);

        add(new Label("name", getModelObject().getName()));

        EltiAjaxLink button = new EltiAjaxLink("add") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onAdd(target);
            }
        };

        add(button);
        button.add(new AttributeModifier("title", new ResourceModel("add.element.tooltip")));
        button.add(new TooltipBehavior());

        add(listContainer.setOutputMarkupId(true));
        genericManager.initialize(getModelObject(), getModelObject().getItems());
        listContainer.add(new ListView<ELTCourseItem>("list", itemsModel) {
            @Override
            protected void populateItem(final ListItem<ELTCourseItem> item) {
                item.add(new ItemPanel("item", item.getModel()) {
                    @Override
                    protected boolean canBeMovedUp() {
                        return item.getModelObject().getIndex() > 0;
                    }

                    @Override
                    protected boolean canBeMovedDown() {
                        genericManager.initialize(item.getModelObject(), item.getModelObject().getBlock());
                        genericManager.initialize(item.getModelObject(), item.getModelObject().getParent());
                        int size;
                        if (item.getModelObject().getBlock() != null) {
                            genericManager.initialize(item.getModelObject().getBlock(),
                                    item.getModelObject().getBlock().getItems());
                            size = item.getModelObject().getBlock().getItems().size();
                        } else {
                            genericManager.initialize(item.getModelObject().getParent(),
                                    item.getModelObject().getParent().getItems());
                            size = item.getModelObject().getParent().getItems().size();
                        }
                        return (item.getModelObject().getIndex() + 1) < size;
                    }

                    @Override
                    protected void onUp(AjaxRequestTarget target) {
                        try {
                            courseItemManager.moveUp(item.getModelObject());
                            itemsModel.detach();
                            target.add(listContainer);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }

                    @Override
                    protected void onDown(AjaxRequestTarget target) {
                        try {
                            courseItemManager.moveDown(item.getModelObject());
                            itemsModel.detach();
                            target.add(listContainer);
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }

                    @Override
                    protected void onAdd(AjaxRequestTarget target) {
                        target.add(listContainer);
                    }
                });
            }
        });

    }

    protected abstract void onAdd(AjaxRequestTarget target);


}
