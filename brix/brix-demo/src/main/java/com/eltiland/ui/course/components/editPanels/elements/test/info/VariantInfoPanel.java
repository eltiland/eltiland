package com.eltiland.ui.course.components.editPanels.elements.test.info;

import com.eltiland.bl.test.TestVariantManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestVariant;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Test Variant info panel.
 *
 * @author ALeksey PLotnikov.
 */
public abstract class VariantInfoPanel extends EntityInfoPanel<TestVariant> {

    @SpringBean
    private TestVariantManager testVariantManager;

    /**
     * Panel constructor.
     *
     * @param id                markup id.
     * @param testVariantIModel entity model.
     */
    public VariantInfoPanel(String id, final IModel<TestVariant> testVariantIModel) {
        super(id, testVariantIModel);
    }

    @Override
    protected Component getAdditionInfoComponent() {
        return new Label("number", String.format(getString("score"), getModelObject().getNumber()));
    }

    @Override
    protected void onDelete(TestVariant entity) throws EltilandManagerException {
        testVariantManager.updateNumbers(entity);
        testVariantManager.deleteEntity(entity);
    }
}
