package com.eltiland.ui.course.components.editPanels.elements.test.list;

import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestEntity;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AbstractTestPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.EntityInfoPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Abstract panel for entity list for the test.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class EntityPanel<T extends TestEntity> extends BaseEltilandPanel<TestCourseItem> {

    protected final int DIALOG_WIDTH = 355;

    protected Dialog<AbstractTestPropertyPanel> propertyDialog =
            new Dialog<AbstractTestPropertyPanel>("propertyDialog", DIALOG_WIDTH) {
                @Override
                public AbstractTestPropertyPanel createDialogPanel(String id) {
                    return getPropertyPanel(id);
                }
            };

    protected Dialog<AbstractTestPropertyPanel> propertySecondDialog =
            new Dialog<AbstractTestPropertyPanel>("propertySecondDialog", DIALOG_WIDTH) {
                @Override
                public AbstractTestPropertyPanel createDialogPanel(String id) {
                    return getSecondPropertyPanel(id);
                }
            };


    private WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");

    private ListView<T> list = new ListView<T>("entityList", getEntityListModel()) {
        @Override
        protected void populateItem(ListItem<T> item) {
            item.add(getEntityInfoPanel("entityInfo", item.getModel()));
        }
    };

    /**
     * Panel constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test course item model.
     */
    protected EntityPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);

        add(new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                propertyDialog.getDialogPanel().initCreateMode();
                propertyDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });

        add(new EltiAjaxLink("addButtonSecond") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                propertySecondDialog.getDialogPanel().initCreateMode();
                propertySecondDialog.show(target);
            }

            @Override
            public boolean isVisible() {
                return hasSecondAction();
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        });

        add(listContainer.setOutputMarkupId(true));
        listContainer.add(list);

        add(propertyDialog);
        add(propertySecondDialog);
    }

    /**
     * Return TRUE when second add action on the panel is available.
     */
    protected boolean hasSecondAction() {
        return false;
    }

    /**
     * Must @return panel for adding entity.
     */
    protected abstract AbstractTestPropertyPanel getPropertyPanel(String id);

    /**
     * @return panel for second adding entity (f.e. section).
     */
    protected AbstractTestPropertyPanel getSecondPropertyPanel(String id) {
        return null;
    }

    /**
     * Must @return model of the entity list.
     */
    protected abstract IModel<List<T>> getEntityListModel();

    /**
     * Must @return entity info panel for list.
     */
    protected abstract EntityInfoPanel<T> getEntityInfoPanel(String id, IModel<T> model);

    /**
     * Callback for updating test validator panel.
     */
    protected abstract void updateTestValidator(AjaxRequestTarget target);

    /**
     * Updates entity list.
     *
     * @param target Ajax request target.
     */
    protected void updateList(AjaxRequestTarget target) {
        list.setModel(getEntityListModel());
        target.add(listContainer);
        updateTestValidator(target);
    }
}
