package com.eltiland.ui.course.components.editPanels.elements.test.list;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestVariantManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestVariant;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.AbstractTestPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.edit.VariantPropertyPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.EntityInfoPanel;
import com.eltiland.ui.course.components.editPanels.elements.test.info.VariantInfoPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel for Variants list for tests.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class VariantPanel extends EntityPanel<TestVariant> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestVariantManager testVariantManager;

    /**
     * Panel constructor.
     *
     * @param id                   markup id.
     * @param testCourseItemIModel test course item model.
     */
    public VariantPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);
    }

    @Override
    protected AbstractTestPropertyPanel getPropertyPanel(String id) {
        return new VariantPropertyPanel(id, VariantPanel.this.getModel()) {
            @Override
            protected void OnCreate(AjaxRequestTarget target) {
                TestVariant variant = new TestVariant();

                createEntity(fillEntity(variant));

                propertyDialog.close(target);
                updateList(target);
            }

            @Override
            protected void OnSave(AjaxRequestTarget target) {
                TestVariant variant = (TestVariant) propertyDialog.getDialogPanel().getModelObject();

                updateEntity(fillEntity(variant));
                propertyDialog.close(target);
                updateList(target);
            }
        };
    }

    @Override
    protected IModel<List<TestVariant>> getEntityListModel() {
        return new GenericDBListModel<>(TestVariant.class, testVariantManager.getVariantsForItem(getModelObject()));
    }

    @Override
    protected EntityInfoPanel<TestVariant> getEntityInfoPanel(String id, final IModel<TestVariant> model) {
        return new VariantInfoPanel(id, model) {
            @Override
            protected void updateList(AjaxRequestTarget target) {
                VariantPanel.this.updateList(target);
            }

            @Override
            protected void onEdit(AjaxRequestTarget target, IModel<TestVariant> model) {
                propertyDialog.getDialogPanel().initEditMode(model.getObject());
                propertyDialog.show(target);
            }

            @Override
            protected boolean canBeMovedUp() {
                TestVariant variant = genericManager.getObject(TestVariant.class, model.getObject().getId());
                int number = variant.getOrderNumber();
                return number > 0;
            }

            @Override
            protected boolean canBeMovedDown() {
                TestVariant variant = genericManager.getObject(TestVariant.class, model.getObject().getId());
                TestCourseItem item = genericManager.getObject(TestCourseItem.class,
                        VariantPanel.this.getModelObject().getId());

                int number = variant.getOrderNumber();
                genericManager.initialize(item, item.getVariants());

                int totalCount = item.getVariants().size();

                return number < (totalCount - 1);
            }

            @Override
            protected void onMoveUp(TestVariant entity) {
                try {
                    testVariantManager.moveVariantOfItem(entity, VariantPanel.this.getModelObject(), true);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Error while moving entity");
                    throw new WicketRuntimeException("Error while moving entity", e);
                }
            }

            @Override
            protected void onMoveDown(TestVariant entity) {
                try {
                    testVariantManager.moveVariantOfItem(entity, VariantPanel.this.getModelObject(), false);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Error while moving entity");
                    throw new WicketRuntimeException("Error while moving entity", e);
                }
            }
        };
    }
}
