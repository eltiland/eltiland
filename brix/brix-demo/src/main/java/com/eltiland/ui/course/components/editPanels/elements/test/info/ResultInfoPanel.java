package com.eltiland.ui.course.components.editPanels.elements.test.info;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.bl.test.TestResultManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestJumpOrder;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.ui.common.model.GenericDBListModel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Test Result info panel.
 *
 * @author ALeksey PLotnikov.
 */
public abstract class ResultInfoPanel extends EntityInfoPanel<TestResult> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestJumpOrderManager testJumpOrderManager;
    @SpringBean
    private TestResultManager testResultManager;
    @SpringBean
    private TestJumpManager testJumpManager;

    /**
     * Panel constructor.
     *
     * @param id               markup id.
     * @param testResultIModel entity model.
     */
    public ResultInfoPanel(String id, final IModel<TestResult> testResultIModel) {
        super(id, testResultIModel);
    }

    @Override
    protected Component getAdditionInfoComponent() {
        TestResult result = getModelObject();

        String label;
        if (result.getMaxValue() == result.getMinValue()) {
            label = String.format(getString("value"), result.getMaxValue());
        } else {
            label = String.format(getString("interval"), result.getMinValue(), result.getMaxValue());
        }

        if (result.isJumpFinish()) {
            label += getString("finish");
        }

        return new Label("number", label);
    }

    @Override
    protected Component getAdditionInfoComponent2() {
        TestResult result = getModelObject();
        genericManager.initialize(result, result.getJumps());

        ListView<TestJump> jumpListView = new ListView<TestJump>(
                "listView", new GenericDBListModel<>(TestJump.class, testJumpManager.getSortedJumps(result))) {
            @Override
            protected void populateItem(ListItem<TestJump> item) {
                TestJump jump = item.getModelObject();

                genericManager.initialize(jump, jump.getPrevs());
                genericManager.initialize(jump, jump.getDest());

                String prev = "";
                if (!(jump.getPrevs().isEmpty())) {
                    prev += getString("prev");
                    boolean isFirst = true;
                    for (TestJumpOrder order : testJumpOrderManager.getSortedJumpOrders(item.getModelObject())) {
                        if (!isFirst) {
                            prev += ", ";
                        } else {
                            isFirst = false;
                        }
                        genericManager.initialize(order, order.getQuestion());
                        prev += order.getQuestion().getTextValue();
                    }
                    prev += ".";
                }

                item.add(new Label("jump", String.format(getString("jump"),
                        item.getModelObject().getDest().getTextValue() + ". " + prev)));
            }
        };

        return jumpListView;
    }

    @Override
    protected Component getAdditionInfoComponent3() {
        return new WebMarkupContainer("rightFlag");
    }

    @Override
    protected boolean isRightResult() {
        return getModelObject().isRightResult();
    }

    @Override
    protected void onDelete(TestResult entity) throws EltilandManagerException {
        testResultManager.deleteTestResult(entity);
    }
}
