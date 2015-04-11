package com.eltiland.ui.course.components.editPanels.elements.test.edit.result;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course.test.TestJump;
import com.eltiland.model.course.test.TestResult;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing jumps of the test result.
 *
 * @author Aleksey Plotnikov.
 */
class JumpsPanel extends FormComponentPanel<List<TestJump>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpsPanel.class);

    @SpringBean
    private TestJumpOrderManager testJumpOrderManager;
    @SpringBean
    private TestJumpManager testJumpManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<TestCourseItem> itemModel = new GenericDBModel<>(TestCourseItem.class);
    private IModel<TestResult> resultModel = new GenericDBModel<>(TestResult.class);

    private IModel<List<TestJump>> jumpListModel = new LoadableDetachableModel<List<TestJump>>() {
        @Override
        protected List<TestJump> load() {
            return JumpsPanel.this.getModelObject();
        }
    };

    private Dialog<EditJumpPanel> editDialog = new Dialog<EditJumpPanel>("editDialog", 350) {
        @Override
        public EditJumpPanel createDialogPanel(String id) {
            return new EditJumpPanel(id);
        }

        @Override
        public void registerCallback(EditJumpPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<TestJump>() {
                @Override
                public void process(IModel<TestJump> model, AjaxRequestTarget target) {
                    close(target);

                    model.getObject().setResult(resultModel.getObject());

                    genericManager.initialize(resultModel.getObject(), resultModel.getObject().getJumps());
                    int index = resultModel.getObject().getJumps().size();
                    model.getObject().setJumpOrder(index);

                    try {
                        genericManager.update(model.getObject());
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save jump", e);
                        throw new WicketRuntimeException("Cannot save jump", e);
                    }

                    JumpsPanel.this.getModelObject().add(model.getObject());
                    jumpListModel.detach();
                    target.add(list);
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<TestJump>() {
                @Override
                public void process(IModel<TestJump> model, AjaxRequestTarget target) {
                    close(target);
                    TestResult result = resultModel.getObject();
                    JumpsPanel.this.setModelObject(new ArrayList<>(result.getJumps()));
                    jumpListModel.detach();
                    target.add(list);
                }
            });
        }

        @Override
        protected void onClose(AjaxRequestTarget target) {
            super.onClose(target);
            try {
                testJumpOrderManager.deleteAllOrphans();
            } catch (EltilandManagerException e) {
                LOGGER.error("Cannot delete jump order", e);
                throw new WicketRuntimeException("Cannot delete jump order", e);
            }
        }
    };

    private WebMarkupContainer list = new WebMarkupContainer("list");

    private ListView<TestJump> listView = new ListView<TestJump>("jumpList", jumpListModel) {
        @Override
        protected void populateItem(ListItem<TestJump> components) {
            components.add(new JumpInfoPanel("jumpInfoPanel", components.getModel()) {
                @Override
                protected void onDelete(TestJump jump, AjaxRequestTarget target) {
                    JumpsPanel.this.getModelObject().remove(jump);

                    try {
                        testJumpManager.deleteJump(jump);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot delete jump order", e);
                        throw new WicketRuntimeException("Cannot delete jump order", e);
                    }

                    JumpsPanel.this.setModelObject(testJumpManager.getSortedJumps(resultModel.getObject()));
                    jumpListModel.detach();
                    target.add(list);
                }

                @Override
                protected void onEdit(TestJump jump, AjaxRequestTarget target) {
                    editDialog.getDialogPanel().initEditMode(itemModel, new GenericDBModel<>(TestJump.class, jump));
                    editDialog.show(target);
                }

                @Override
                protected void updateList(AjaxRequestTarget target) {
                    JumpsPanel.this.setModelObject(testJumpManager.getSortedJumps(resultModel.getObject()));
                    jumpListModel.detach();
                    target.add(list);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id        markup id.
     * @param model     test jump list model.
     * @param itemModel course item model.
     */
    public JumpsPanel(String id, IModel<List<TestJump>> model, IModel<TestCourseItem> itemModel) {
        super(id, model);
        this.itemModel = itemModel;
        addComponents();
    }

    /**
     * Create mode initializer.
     */
    public void initCreateMode() {
        setModelObject(new ArrayList<TestJump>());
        jumpListModel.detach();
    }

    /**
     * Edit mode initializer.
     */
    public void initEditMode(List<TestJump> jumpList, IModel<TestResult> resultModel) {
        this.resultModel = resultModel;
        setModelObject(testJumpManager.getSortedJumps(resultModel.getObject()));
        jumpListModel.detach();
    }

    private void addComponents() {
        add(list);
        list.setOutputMarkupPlaceholderTag(true);
        list.add(listView);

        add(new EltiAjaxLink("addJump") {
            @Override
            public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                editDialog.getDialogPanel().initCreateMode(itemModel);
                editDialog.show(ajaxRequestTarget);
            }
        });

        add(editDialog);
    }

    @Override
    protected void convertInput() {
        jumpListModel.detach();
        setConvertedInput(jumpListModel.getObject());
    }
}
