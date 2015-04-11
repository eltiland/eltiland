package com.eltiland.ui.course.components.editPanels.elements.test.edit.result;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.test.TestJumpManager;
import com.eltiland.bl.test.TestJumpOrderManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.test.*;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.select.ELTSelectField;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for adding-editing single jump.
 *
 * @author Aleksey Plotnikov
 */
class EditJumpPanel extends ELTDialogPanel implements IDialogNewCallback<TestJump>, IDialogUpdateCallback<TestJump> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditJumpPanel.class);

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private TestJumpOrderManager testJumpOrderManager;
    @SpringBean
    private TestJumpManager testJumpManager;
    @SpringBean
    private GenericManager genericManager;

    private IDialogNewCallback.IDialogActionProcessor<TestJump> callback;
    private IDialogUpdateCallback.IDialogActionProcessor<TestJump> updateCallback;

    private IModel<TestCourseItem> itemModel = new GenericDBModel<>(TestCourseItem.class);
    private IModel<TestJump> jumpModel = new GenericDBModel<>(TestJump.class);
    private boolean isEditMode;

    private IModel<List<TestQuestion>> questionsListModel = new LoadableDetachableModel<List<TestQuestion>>() {
        @Override
        protected List<TestQuestion> load() {
            return courseItemManager.getTopLevelQuestions(itemModel.getObject(), null, true);
        }
    };

    private IModel<List<TestJumpOrder>> jumpOrderListModel = new GenericDBListModel<>(TestJumpOrder.class);

    private IModel<List<TestJumpOrder>> ordersListModel = new LoadableDetachableModel<List<TestJumpOrder>>() {
        @Override
        protected List<TestJumpOrder> load() {
            return jumpOrderListModel.getObject();
        }
    };

    private Dialog<PrevPanel> prevDialog = new Dialog<PrevPanel>("prevDialog", 335) {
        @Override
        public PrevPanel createDialogPanel(String id) {
            return new PrevPanel(id);
        }

        @Override
        public void registerCallback(PrevPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<TestJumpOrder>() {
                @Override
                public void process(IModel<TestJumpOrder> model, AjaxRequestTarget target) {
                    TestJumpOrder order = model.getObject();
                    if (isEditMode) {
                        order.setJump(jumpModel.getObject());
                    }
                    if (jumpOrderListModel.getObject() != null) {
                        order.setOrder(jumpOrderListModel.getObject().size());
                    }
                    try {
                        testJumpOrderManager.createJumpOrder(order);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create jump order", e);
                        throw new WicketRuntimeException("Cannot create jump order", e);
                    }
                    jumpOrderListModel.getObject().add(order);
                    close(target);
                    ordersListModel.detach();
                    target.add(list);
                }
            });
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<TestJumpOrder>() {
                @Override
                public void process(IModel<TestJumpOrder> model, AjaxRequestTarget target) {
                    TestJumpOrder order = model.getObject();
                    try {
                        testJumpOrderManager.updateJumpOrder(order);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot update jump order", e);
                        throw new WicketRuntimeException("Cannot update jump order", e);
                    }
                    close(target);
                    ordersListModel.detach();
                    target.add(list);
                }
            });
        }
    };

    private ELTSelectField<TestQuestion> selector =
            new ELTSelectField<TestQuestion>("selector",
                    new ResourceModel("jumpSelector"), new GenericDBModel<>(TestQuestion.class), true) {
                @Override
                protected IModel<List<TestQuestion>> getChoiceListModel() {
                    return questionsListModel;
                }

                @Override
                protected IChoiceRenderer<TestQuestion> getChoiceRenderer() {
                    return new QuestionRenderer();
                }
            };

    private WebMarkupContainer list = new WebMarkupContainer("list");

    private ListView<TestJumpOrder> listOrders = new ListView<TestJumpOrder>("prevList", ordersListModel) {
        @Override
        protected void populateItem(final ListItem<TestJumpOrder> item) {
            genericManager.initialize(item.getModelObject(), item.getModelObject().getQuestion());
            item.add(new ConditionPanel("prev", item.getModel()) {
                @Override
                protected boolean canBeDeleted() {
                    int order = item.getModelObject().getOrder();
                    return ((order + 1) >= jumpOrderListModel.getObject().size());
                }

                @Override
                protected void onDelete(AjaxRequestTarget target) {
                    TestJumpOrder order = item.getModelObject();
                    jumpOrderListModel.getObject().remove(order);
                    ordersListModel.detach();

                    try {
                        testJumpOrderManager.deleteJumpOrder(order);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot delete jump order", e);
                        throw new WicketRuntimeException("Cannot delete jump order", e);
                    }

                    TestJump jump = jumpModel.getObject();
                    jumpModel.setObject(genericManager.getObject(TestJump.class, jump.getId()));

                    target.add(list);
                }
            });
        }
    };


    public EditJumpPanel(String id) {
        super(id);
        form.add(selector);
        form.add(new EltiAjaxLink("addPrev") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                prevDialog.getDialogPanel().initCreateMode();
                prevDialog.show(target);
            }
        });
        form.add(prevDialog);
        list.setOutputMarkupId(true);
        form.add(list);
        list.add(listOrders);
    }

    public void initCreateMode(IModel<TestCourseItem> itemModel) {
        this.itemModel = itemModel;
        questionsListModel.detach();
        jumpOrderListModel.setObject(new ArrayList<TestJumpOrder>());
        ordersListModel.detach();
        jumpModel.setObject(null);
        isEditMode = false;
    }

    public void initEditMode(IModel<TestCourseItem> itemModel,
                             IModel<TestJump> jumpModel) {
        this.itemModel = itemModel;
        questionsListModel.detach();

        TestJump jump = jumpModel.getObject();
        genericManager.initialize(jump, jump.getDest());

        selector.setModelObject(jump.getDest());
        jumpOrderListModel.setObject(testJumpOrderManager.getSortedJumpOrders(jump));
        ordersListModel.detach();
        this.jumpModel = jumpModel;
        isEditMode = true;
    }

    @Override
    protected String getHeader() {
        return getString("jumpHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Save)) {
            if (isEditMode) {
                TestJump jump = jumpModel.getObject();
                jump.setDest(selector.getModelObject());

                try {
                    testJumpManager.updateJump(jump);
                    for (TestJumpOrder order : jumpOrderListModel.getObject()) {
                        order.setJump(jump);
                        testJumpOrderManager.updateJumpOrder(order);
                    }
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot update jump", e);
                    throw new WicketRuntimeException("Cannot update jump", e);
                }
                updateCallback.process(new GenericDBModel<>(TestJump.class, jump), target);
            } else {
                TestJump jump = new TestJump();
                jump.setDest(selector.getModelObject());
                try {
                    testJumpManager.createJump(jump);
                    for (TestJumpOrder order : jumpOrderListModel.getObject()) {
                        order.setJump(jump);
                        testJumpOrderManager.updateJumpOrder(order);
                    }
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create jump", e);
                    throw new WicketRuntimeException("Cannot create jump", e);
                }
                callback.process(new GenericDBModel<>(TestJump.class, jump), target);
            }
        }
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<TestJump> callback) {
        this.callback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<TestJump> callback) {
        this.updateCallback = callback;
    }

    /**
     * Panel for creating/editing Jump Order.
     */
    private class PrevPanel extends ELTDialogPanel implements
            IDialogNewCallback<TestJumpOrder>, IDialogUpdateCallback<TestJumpOrder> {

        private IModel<TestJumpOrder> orderModel = new GenericDBModel(TestJumpOrder.class);
        private IDialogNewCallback.IDialogActionProcessor<TestJumpOrder> callback;
        private IDialogUpdateCallback.IDialogActionProcessor<TestJumpOrder> updateCallback;
        private boolean editMode = false;

        private ELTSelectField<TestQuestion> prevSelector =
                new ELTSelectField<TestQuestion>("selector",
                        new ResourceModel("prevQuestion"), new GenericDBModel<>(TestQuestion.class), true) {
                    @Override
                    protected IModel<List<TestQuestion>> getChoiceListModel() {
                        return questionsListModel;
                    }

                    @Override
                    protected IChoiceRenderer<TestQuestion> getChoiceRenderer() {
                        return new QuestionRenderer();
                    }
                };

        /**
         * Panel ctor.
         *
         * @param id markup id.
         */
        public PrevPanel(String id) {
            super(id, new GenericDBModel<>(TestJumpOrder.class));
            form.add(prevSelector);
        }

        /**
         * Edit mode initializer.
         *
         * @param orderModel current jump order model.
         */
        public void initEditMode(IModel<TestJumpOrder> orderModel) {
            this.orderModel = orderModel;
            prevSelector.setModelObject(orderModel.getObject().getQuestion());
            editMode = true;
        }

        /**
         * Create mode initializer.
         */
        public void initCreateMode() {
            this.orderModel.setObject(null);
            editMode = false;
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                if (editMode) {
                    TestJumpOrder order = orderModel.getObject();
                    order.setQuestion(prevSelector.getModelObject());
                    updateCallback.process(new GenericDBModel<>(TestJumpOrder.class, order), target);
                } else {
                    TestJumpOrder order = new TestJumpOrder();
                    order.setOrder(0);
                    order.setQuestion(prevSelector.getModelObject());
                    callback.process(new GenericDBModel<>(TestJumpOrder.class, order), target);
                }
            }
        }

        @Override
        public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<TestJumpOrder> callback) {
            this.callback = callback;
        }

        @Override
        public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<TestJumpOrder> callback) {
            this.updateCallback = callback;
        }
    }

    /**
     * Renderer for question selector.
     */
    private class QuestionRenderer implements IChoiceRenderer<TestQuestion> {

        @Override
        public Object getDisplayValue(TestQuestion object) {
            return String.format(getString("selectLabel"), object.getNumber() + 1, object.getTextValue());
        }

        @Override
        public String getIdValue(TestQuestion object, int index) {
            return object.getId().toString();
        }
    }

    /**
     * Panel for output condition
     */
    private abstract class ConditionPanel extends BaseEltilandPanel<TestJumpOrder> {

        protected ConditionPanel(String id, IModel<TestJumpOrder> testJumpOrderIModel) {
            super(id, testJumpOrderIModel);
            add(new Label("label", getModelObject().getQuestion().getTextValue()));
            add(new EltiAjaxLink("deleteLink") {
                @Override
                public boolean isVisible() {
                    return canBeDeleted();
                }

                @Override
                public void onClick(AjaxRequestTarget target) {
                    onDelete(target);
                }
            });
        }

        protected abstract boolean canBeDeleted();

        protected abstract void onDelete(AjaxRequestTarget target);
    }
}
