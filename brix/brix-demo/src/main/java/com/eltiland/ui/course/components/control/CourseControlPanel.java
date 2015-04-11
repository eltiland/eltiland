package com.eltiland.ui.course.components.control;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.CoursePaidInvoiceManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseItem;
import com.eltiland.model.course.FolderCourseItem;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.ConfirmationDialogBehavior;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.control.panels.CourseCreateItemPanel;
import com.eltiland.ui.course.components.control.panels.CoursePaidInvoicePanel;
import com.eltiland.ui.course.components.control.panels.CourseRenameItemPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Control panel for course tree.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class CourseControlPanel extends BaseEltilandPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseControlPanel.class);

    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private CoursePaidInvoiceManager coursePaidInvoiceManager;
    @SpringBean
    private GenericManager genericManager;

    private Course.CONTENT_KIND kind;


    private Dialog<CourseCreateItemPanel> createItemPanelDialog = new Dialog<CourseCreateItemPanel>(
            "createItemDialog", 330) {
        @Override
        public CourseCreateItemPanel createDialogPanel(String id) {
            return new CourseCreateItemPanel(id);
        }

        @Override
        public void registerCallback(CourseCreateItemPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<CourseItem>() {
                @Override
                public void process(IModel<CourseItem> model, AjaxRequestTarget target) {
                    CourseItem item = model.getObject();
                    CourseItem currentItem = getCurrentItem();
                    if (currentItem instanceof FolderCourseItem) {
                        item.setParentItem(currentItem);
                    } else {
                        if (kind.equals(Course.CONTENT_KIND.DEMO)) {
                            item.setCourseDemo(CourseControlPanel.this.getModelObject());
                        } else if (kind.equals(Course.CONTENT_KIND.FULL)) {
                            item.setCourseFull(CourseControlPanel.this.getModelObject());
                        }
                    }

                    try {
                        courseItemManager.createCourseItem(item, kind);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create course item", e);
                        throw new WicketRuntimeException("Cannot create course item", e);
                    }
                    updateTree(target);
                    close(target);
                    ELTAlerts.renderOKPopup(getString("addMessage"), target);
                }
            });
        }
    };

    private Dialog<CourseRenameItemPanel> renameItemPanelDialog =
            new Dialog<CourseRenameItemPanel>("renameItemDialog", 330) {
                @Override
                public CourseRenameItemPanel createDialogPanel(String id) {
                    return new CourseRenameItemPanel(id, new Model<>(getCurrentItem().getName()));
                }

                @Override
                public void registerCallback(CourseRenameItemPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<String>() {
                        @Override
                        public void process(IModel<String> model, AjaxRequestTarget target) {
                            CourseItem currentItem = getCurrentItem();
                            currentItem.setName(model.getObject());

                            try {
                                courseItemManager.updateCourseItem(currentItem);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot update course item", e);
                                throw new WicketRuntimeException("Cannot update course item", e);
                            }
                            updateTree(target);
                            close(target);
                            ELTAlerts.renderOKPopup(getString("saveMessage"), target);
                        }
                    });
                }
            };

    private Dialog<CoursePaidInvoicePanel> paidInvoicePanelDialog =
            new Dialog<CoursePaidInvoicePanel>("paidInvoiceDialog", 430) {
                @Override
                public CoursePaidInvoicePanel createDialogPanel(String id) {
                    return new CoursePaidInvoicePanel(id);
                }

                @Override
                public void registerCallback(CoursePaidInvoicePanel panel) {
                    super.registerCallback(panel);
                    panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<CoursePaidInvoice>() {
                        @Override
                        public void process(IModel<CoursePaidInvoice> model, AjaxRequestTarget target) {
                            CoursePaidInvoice invoice = model.getObject();
                            invoice.setCourse(CourseControlPanel.this.getModelObject());
                            if (getCurrentItem() instanceof FolderCourseItem) {
                                invoice.setItem((FolderCourseItem) getCurrentItem());
                            }
                            try {
                                coursePaidInvoiceManager.createCoursePaidInvoice(invoice);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot create paid invoice for item", e);
                                throw new WicketRuntimeException("Cannot create paid invoice for item", e);
                            }

                            close(target);
                            ELTAlerts.renderOKPopup(getString("addPaidMessage"), target);
                        }
                    });
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CoursePaidInvoice>() {
                        @Override
                        public void process(IModel<CoursePaidInvoice> model, AjaxRequestTarget target) {
                            CoursePaidInvoice invoice = model.getObject();
                            invoice.setCourse(CourseControlPanel.this.getModelObject());
                            if (getCurrentItem() instanceof FolderCourseItem) {
                                invoice.setItem((FolderCourseItem) getCurrentItem());
                            }
                            try {
                                coursePaidInvoiceManager.createCoursePaidInvoice(invoice);
                            } catch (EltilandManagerException e) {
                                LOGGER.error("Cannot create paid invoice for item", e);
                                throw new WicketRuntimeException("Cannot create paid invoice for item", e);
                            }
                            close(target);
                            ELTAlerts.renderOKPopup(getString("modifyPaidMessage"), target);
                        }
                    });
                }
            };


    private EltiAjaxLink lockButton = new EltiAjaxLink("lockButton") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            updateLockButtons(target, false);
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return null;
        }

        @Override
        public boolean isVisible() {
            return kind.equals(Course.CONTENT_KIND.FULL) && CourseControlPanel.this.getModelObject().isFullAccess();
        }
    };

    private EltiAjaxLink unlockButton = new EltiAjaxLink("unlockButton") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            updateLockButtons(target, true);
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return null;
        }

        @Override
        public boolean isVisible() {
            return kind.equals(Course.CONTENT_KIND.FULL) && !(CourseControlPanel.this.getModelObject().isFullAccess());
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course model.
     * @param kind         content kind( DEMO - FULL)
     */
    public CourseControlPanel(String id, IModel<Course> courseIModel, final Course.CONTENT_KIND kind) {
        super(id, courseIModel);
        this.kind = kind;

        EltiAjaxLink addButton = new EltiAjaxLink("addButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                createItemPanelDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }
        };

        EltiAjaxLink removeButton = new EltiAjaxLink("removeButton") {
            {
                add(new ConfirmationDialogBehavior());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                CourseItem item = courseItemManager.getCourseItemById(getCurrentItem().getId());
                if (item != null) {
                    try {
                        courseItemManager.deleteCourseItem(item, kind, true);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot delete course item", e);
                        throw new WicketRuntimeException("Cannot delete course item", e);
                    }
                }
                setVisible(false);
                target.add(this);
                updateTree(target);
                onDelete(target);
                ELTAlerts.renderOKPopup(getString("deleteMessage"), target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return deleteEnabled();
            }
        };

        EltiAjaxLink renameButton = new EltiAjaxLink("renameButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                renameItemPanelDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return deleteEnabled();
            }
        };

        EltiAjaxLink upButton = new EltiAjaxLink("upButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    courseItemManager.moveCourseItemUp(getCurrentItem(), CourseControlPanel.this.kind);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot move up course element", e);
                    throw new WicketRuntimeException("Cannot move up course element", e);
                }
                updateTree(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                CourseItem item = getCurrentItem();
                return item != null && item.getIndex() > 0;
            }
        };

        EltiAjaxLink downButton = new EltiAjaxLink("downButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    courseItemManager.moveCourseItemDown(getCurrentItem(), CourseControlPanel.this.kind);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot move up course element", e);
                    throw new WicketRuntimeException("Cannot move up course element", e);
                }
                updateTree(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                CourseItem item = getCurrentItem();
                if (item == null) {
                    return false;
                } else {
                    int count = courseItemManager.getItemsCountInLevel(item);
                    return (item.getIndex()) + 1 < count;
                }
            }
        };

        EltiAjaxLink paidAddButton = new EltiAjaxLink("paidAddButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {

                paidInvoicePanelDialog.getDialogPanel().initCreateMode();
                if (getCurrentItem() == null) {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice(CourseControlPanel.this.getModelObject())) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                    } else {
                        paidInvoicePanelDialog.show(target);
                    }
                } else {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice((FolderCourseItem) getCurrentItem())) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                    } else {
                        paidInvoicePanelDialog.show(target);
                    }
                }
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeAddPaid();
            }
        };

        EltiAjaxLink paidRemoveButton = new EltiAjaxLink("paidRemoveButton") {
            {
                add(new ConfirmationDialogBehavior(new ResourceModel("paidRemoveConfirmation")));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                CourseItem item = getCurrentItem();
                Course course = CourseControlPanel.this.getModelObject();
                if (item == null) {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice(course)) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                        return;
                    }
                } else {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice((FolderCourseItem) item)) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                        return;
                    }
                }

                CoursePaidInvoice invoice = new CoursePaidInvoice();
                invoice.setCourse(course);
                if ((item != null) && (item instanceof FolderCourseItem)) {
                    invoice.setItem((FolderCourseItem) item);
                }
                invoice.setPrice(BigDecimal.valueOf(0));
                try {
                    coursePaidInvoiceManager.createCoursePaidInvoice(invoice);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create paid invoice for item", e);
                    throw new WicketRuntimeException("Cannot create paid invoice for item", e);
                }

                ELTAlerts.renderOKPopup(getString("removePaidMessage"), target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeModifyPaid();
            }
        };

        EltiAjaxLink paidModifyButton = new EltiAjaxLink("paidModifyButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CourseItem item = getCurrentItem();
                Course course = CourseControlPanel.this.getModelObject();
                if (item == null) {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice(course)) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                        return;
                    }
                } else {
                    if (coursePaidInvoiceManager.hasNotApprovedInvoice((FolderCourseItem) item)) {
                        ELTAlerts.renderErrorPopup(getString("alrearyInvoiced"), target);
                        return;
                    }
                }
                CoursePaidInvoice actualInvoice = coursePaidInvoiceManager.getActualInvoice(
                        course, (FolderCourseItem) item);
                paidInvoicePanelDialog.getDialogPanel().initEditMode(
                        new GenericDBModel<>(CoursePaidInvoice.class, actualInvoice));
                paidInvoicePanelDialog.show(target);
            }

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return null;
            }

            @Override
            public boolean isVisible() {
                return canBeModifyPaid();
            }
        };

        add(addButton);
        add(removeButton);
        add(renameButton);
        add(paidAddButton);
        add(paidRemoveButton);
        add(paidModifyButton);
        add(upButton);
        add(downButton);
        add(lockButton.setOutputMarkupPlaceholderTag(true));
        add(unlockButton.setOutputMarkupPlaceholderTag(true));

        addButton.add(new WebMarkupContainer("addImage"));
        removeButton.add(new WebMarkupContainer("removeImage"));
        renameButton.add(new WebMarkupContainer("renameImage"));
        paidAddButton.add(new WebMarkupContainer("paidAddImage"));
        paidRemoveButton.add(new WebMarkupContainer("paidRemoveImage"));
        paidModifyButton.add(new WebMarkupContainer("paidModifyImage"));
        upButton.add(new WebMarkupContainer("upImage"));
        downButton.add(new WebMarkupContainer("downImage"));
        lockButton.add(new WebMarkupContainer("lockImage"));
        unlockButton.add(new WebMarkupContainer("unlockImage"));

        addButton.add(new AttributeModifier("title", new ResourceModel("addAction")));
        removeButton.add(new AttributeModifier("title", new ResourceModel("removeAction")));
        renameButton.add(new AttributeModifier("title", new ResourceModel("renameAction")));
        upButton.add(new AttributeModifier("title", new ResourceModel("upAction")));
        downButton.add(new AttributeModifier("title", new ResourceModel("downAction")));
        paidAddButton.add(new AttributeModifier("title", new ResourceModel("paidAddAction")));
        paidRemoveButton.add(new AttributeModifier("title", new ResourceModel("paidRemoveAction")));
        paidModifyButton.add(new AttributeModifier("title", new ResourceModel("paidModifyAction")));
        lockButton.add(new AttributeModifier("title", new ResourceModel("lockAction")));
        unlockButton.add(new AttributeModifier("title", new ResourceModel("unlockAction")));

        addButton.add(new TooltipBehavior());
        removeButton.add(new TooltipBehavior());
        renameButton.add(new TooltipBehavior());
        paidAddButton.add(new TooltipBehavior());
        paidRemoveButton.add(new TooltipBehavior());
        paidModifyButton.add(new TooltipBehavior());
        upButton.add(new TooltipBehavior());
        downButton.add(new TooltipBehavior());
        lockButton.add(new TooltipBehavior());
        unlockButton.add(new TooltipBehavior());

        add(createItemPanelDialog);
        add(renameItemPanelDialog);
        add(paidInvoicePanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }

    protected abstract boolean deleteEnabled();

    protected abstract CourseItem getCurrentItem();

    protected abstract void updateTree(AjaxRequestTarget target);

    protected abstract void onDelete(AjaxRequestTarget target);

    // protected abstract void onChangeAccess(AjaxRequestTarget target, boolean newStatus);

    private boolean canBeAddPaid() {
        if (kind.equals(Course.CONTENT_KIND.FULL)) {
            CourseItem item = getCurrentItem();
            if (item == null) {
                return !coursePaidInvoiceManager.isCoursePaid(getModelObject());
            } else {
                item = courseItemManager.getCourseItemById(item.getId());
                return item instanceof FolderCourseItem &&
                        !coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) item);
            }
        } else {
            return false;
        }
    }

    private boolean canBeModifyPaid() {
        if (kind.equals(Course.CONTENT_KIND.FULL)) {
            CourseItem item = getCurrentItem();
            if (item == null) {
                return coursePaidInvoiceManager.isCoursePaid(getModelObject());
            } else {
                item = courseItemManager.getCourseItemById(item.getId());
                return item instanceof FolderCourseItem &&
                        coursePaidInvoiceManager.isBlockPaid((FolderCourseItem) item);
            }
        } else {
            return false;
        }
    }

    private void updateLockButtons(AjaxRequestTarget target, boolean value) {
        Course course = CourseControlPanel.this.getModelObject();
        course.setFullAccess(value);
        try {
            genericManager.update(course);
        } catch (ConstraintException e) {
            LOGGER.error("Cannot change access to course", e);
            throw new WicketRuntimeException("Cannot change access to course", e);
        }

        lockButton.setVisible(value);
        unlockButton.setVisible(!value);
        target.add(lockButton);
        target.add(unlockButton);
    }
}
