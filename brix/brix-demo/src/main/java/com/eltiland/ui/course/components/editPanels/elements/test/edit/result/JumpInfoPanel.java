package com.eltiland.ui.course.components.editPanels.elements.test.edit.result;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestJumpOrder;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jump info panel.
 *
 * @author Aleksey Plotnikov.
 */

abstract class JumpInfoPanel extends BaseEltilandPanel<TestJump> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpInfoPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private TestJumpOrderManager testJumpOrderManager;
    @SpringBean
    private TestJumpManager testJumpManager;

    public JumpInfoPanel(String id, IModel<TestJump> testJumpIModel) {
        super(id, testJumpIModel);
        final TestJump jump = testJumpIModel.getObject();

        genericManager.initialize(jump, jump.getDest());
        genericManager.initialize(jump, jump.getPrevs());
        add(new Label("name", jump.getDest().getTextValue()));

        String condition = "";
        final boolean isCondition = !(jump.getPrevs().isEmpty());
        if (isCondition) {
            condition = getString("previous");
            int index = 0;
            for (TestJumpOrder order : testJumpOrderManager.getSortedJumpOrders(jump)) {
                genericManager.initialize(order, order.getQuestion());
                if (index != 0) {
                    condition += ", ";
                }
                condition += order.getQuestion().getTextValue();
                index++;
            }
        }
        add(new Label("condition", condition) {
            @Override
            public boolean isVisible() {
                return isCondition;
            }
        });

        add(new JumpActionPanel("actionPanel", getModel()) {
            @Override
            protected void onEdit(AjaxRequestTarget target) {
                JumpInfoPanel.this.onEdit(JumpInfoPanel.this.getModelObject(), target);
            }

            @Override
            protected void onDelete(AjaxRequestTarget target) {
                JumpInfoPanel.this.onDelete(JumpInfoPanel.this.getModelObject(), target);
            }

            @Override
            protected void onMoveUp(AjaxRequestTarget target) {
                try {
                    testJumpManager.moveUp(JumpInfoPanel.this.getModelObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot move up jump", e);
                    throw new WicketRuntimeException("Cannot move up jump", e);
                }
                updateList(target);
            }

            @Override
            protected void onMoveDown(AjaxRequestTarget target) {
                try {
                    testJumpManager.moveDown(JumpInfoPanel.this.getModelObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot move down jump", e);
                    throw new WicketRuntimeException("Cannot move down jump", e);
                }
                updateList(target);
            }

            @Override
            protected boolean canBeMovedUp() {
                return JumpInfoPanel.this.getModelObject().getJumpOrder() > 0;
            }

            @Override
            protected boolean canBeMovedDown() {
                TestJump jump = JumpInfoPanel.this.getModelObject();
                genericManager.initialize(jump, jump.getResult());
                genericManager.initialize(jump.getResult(), jump.getResult().getJumps());
                int count = jump.getResult().getJumps().size();
                return (jump.getJumpOrder() + 1) < count;
            }
        });

        setOutputMarkupId(true);
    }

    protected abstract void onDelete(TestJump jump, AjaxRequestTarget target);

    protected abstract void onEdit(TestJump jump, AjaxRequestTarget target);

    protected abstract void updateList(AjaxRequestTarget target);
}
