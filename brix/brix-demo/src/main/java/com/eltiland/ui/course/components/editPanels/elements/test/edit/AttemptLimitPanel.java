package com.eltiland.ui.course.components.editPanels.elements.test.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for specifying attempt limit for test.
 *
 * @author Aleksey Plotnikov.
 */
public class AttemptLimitPanel extends BaseEltilandPanel<TestCourseItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttemptLimitPanel.class);

    private int attemptCount;

    @SpringBean
    private GenericManager genericManager;

    private Dialog<AttemptPropertyPanel> attemptPropertyPanelDialog =
            new Dialog<AttemptPropertyPanel>("attemptPropertyDialog", 320) {
                @Override
                public AttemptPropertyPanel createDialogPanel(String id) {
                    return new AttemptPropertyPanel(id);
                }

                @Override
                public void registerCallback(AttemptPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<Integer>() {
                        @Override
                        public void process(IModel<Integer> model, AjaxRequestTarget target) {
                            close(target);

                            TestCourseItem item = AttemptLimitPanel.this.getModelObject();
                            item.setAttemptLimit(model.getObject());

                            try {
                                genericManager.update(item);
                            } catch (ConstraintException e) {
                                LOGGER.error("Cannot update course test item", e);
                                throw new WicketRuntimeException("Cannot update course test item", e);
                            }

                            attemptCount = model.getObject();
                            target.add(attemptButton);
                            updateLabel();
                            target.add(attemptLabel);
                            target.add(limitLabel);
                        }
                    });
                }
            };

    private EltiAjaxLink attemptButton = new EltiAjaxLink("attemptButton") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            attemptPropertyPanelDialog.getDialogPanel().initData(attemptCount);
            attemptPropertyPanelDialog.show(target);
        }
    };

    private Label attemptLabel = new Label("buttonLabel", new Model<String>());
    private Label limitLabel = new Label("limitValue", new Model<String>()) {
        @Override
        public boolean isVisible() {
            return attemptCount > 0;
        }
    };

    public AttemptLimitPanel(String id, IModel<TestCourseItem> testCourseItemIModel) {
        super(id, testCourseItemIModel);

        attemptCount = genericManager.getObject(TestCourseItem.class, getModelObject().getId()).getAttemptLimit();

        add(attemptButton.setOutputMarkupPlaceholderTag(true));
        add(limitLabel.setOutputMarkupPlaceholderTag(true));
        add(attemptPropertyPanelDialog);
        attemptButton.add(attemptLabel.setOutputMarkupId(true));
        updateLabel();
    }

    private void updateLabel() {
        attemptLabel.setDefaultModelObject(
                getString((attemptCount == 0) ? "setAttemptLimitMessage" : "updateAttemptLimitMessage"));
        limitLabel.setDefaultModelObject(String.format(getString("limitMessage"), attemptCount));
    }
}
