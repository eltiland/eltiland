package com.eltiland.ui.course.edit;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.behavior.TooltipBehavior;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for actions with editable content and google content of course items.
 *
 * @author Alex Plotnikov
 */
abstract class AbstractDocActionPanel extends BaseEltilandPanel<ELTGoogleCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;


    EltiAjaxLink enablePrintButton, disablePrintButton,
            printControlButton, enableAuthorWarning, disableAuthorWarning,
            enableSelectButton, disableSelectButton;

    private Dialog<GooglePrintStatisticsPanel> printStatisticsPanelDialog =
            new Dialog<GooglePrintStatisticsPanel>("printStatisticsDialog", 510) {
                @Override
                public GooglePrintStatisticsPanel createDialogPanel(String id) {
                    return new GooglePrintStatisticsPanel(id);
                }

                @Override
                public void registerCallback(GooglePrintStatisticsPanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTDocumentCourseItem>() {
                        @Override
                        public void process(IModel<ELTDocumentCourseItem> model, AjaxRequestTarget target) {
                            close(target);
                        }
                    });
                }
            };

    protected AbstractDocActionPanel(String id, IModel<ELTGoogleCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);

        EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                AbstractDocActionPanel.this.onClick(target);
            }

            @Override
            public boolean isVisible() {
                return isForm();
            }
        };

        EltiAjaxLink saveLinkButton = new EltiAjaxLink("saveLinkButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AbstractDocActionPanel.this.onClick(target);
            }

            @Override
            public boolean isVisible() {
                return !isForm();
            }
        };

        enablePrintButton = new EltiAjaxLink("enablePrintButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ((ELTDocumentCourseItem) AbstractDocActionPanel.this.getModelObject()).setPrintable(true);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                enablePrintButton.setVisible(false);
                disablePrintButton.setVisible(true);
                target.add(enablePrintButton);
                target.add(disablePrintButton);
            }
        };

        disablePrintButton = new EltiAjaxLink("disablePrintButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ((ELTDocumentCourseItem) AbstractDocActionPanel.this.getModelObject()).setPrintable(false);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                enablePrintButton.setVisible(true);
                disablePrintButton.setVisible(false);
                target.add(enablePrintButton);
                target.add(disablePrintButton);
            }
        };

        enableAuthorWarning = new EltiAjaxLink("enableAuthorWarning") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AbstractDocActionPanel.this.getModelObject().setHasWarning(true);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                    ELTAlerts.renderOKPopup(getString("authorMessageON"), target);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                enableAuthorWarning.setVisible(false);
                disableAuthorWarning.setVisible(true);
                target.add(enableAuthorWarning);
                target.add(disableAuthorWarning);
            }
        };

        disableAuthorWarning = new EltiAjaxLink("disableAuthorWarning") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                AbstractDocActionPanel.this.getModelObject().setHasWarning(false);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                    ELTAlerts.renderOKPopup(getString("authorMessageOFF"), target);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                enableAuthorWarning.setVisible(true);
                disableAuthorWarning.setVisible(false);
                target.add(enableAuthorWarning);
                target.add(disableAuthorWarning);
            }
        };

        enableSelectButton = new EltiAjaxLink("enableSelectionButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ((ELTDocumentCourseItem)AbstractDocActionPanel.this.getModelObject()).setProhibitSelect(false);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                    ELTAlerts.renderOKPopup(getString("selectionON"), target);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                disableSelectButton.setVisible(true);
                enableSelectButton.setVisible(false);
                target.add(disableSelectButton);
                target.add(enableSelectButton);
            }
        };

        disableSelectButton = new EltiAjaxLink("disableSelectionButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ((ELTDocumentCourseItem)AbstractDocActionPanel.this.getModelObject()).setProhibitSelect(true);
                try {
                    courseItemManager.update(AbstractDocActionPanel.this.getModelObject());
                    ELTAlerts.renderOKPopup(getString("selectionOFF"), target);
                } catch (CourseException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
                disableSelectButton.setVisible(false);
                enableSelectButton.setVisible(true);
                target.add(disableSelectButton);
                target.add(enableSelectButton);
            }
        };

        printControlButton = new EltiAjaxLink("printControlButton") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                printStatisticsPanelDialog.getDialogPanel().initData(new GenericDBModel<>(ELTDocumentCourseItem.class,
                        ((ELTDocumentCourseItem) AbstractDocActionPanel.this.getModelObject())));
                printStatisticsPanelDialog.show(target);
            }
        };

        // check for demo or full
        genericManager.initialize(getModelObject(), getModelObject().getBlock());
        boolean isFull = false;

        if (getModelObject().getBlock() != null) {
            ELTCourseBlock block = getModelObject().getBlock();
            genericManager.initialize(block, block.getCourse());
            isFull = block.getCourse() != null;
        } else {
            genericManager.initialize(getModelObject(), getModelObject().getParent());
            ELTGroupCourseItem group = getModelObject().getParent();
            genericManager.initialize(group, group.getBlock());
            genericManager.initialize(group.getBlock(), group.getBlock().getCourse());
            isFull = group.getBlock().getCourse() != null;
        }

        boolean isDoc = getModelObject() instanceof ELTDocumentCourseItem;
        boolean isPrint = isDoc && ((ELTDocumentCourseItem) getModelObject()).isPrintable();
        boolean isWarning = getModelObject().isHasWarning();
        boolean isSelect = isDoc && !(((ELTDocumentCourseItem) getModelObject()).isProhibitSelect());
        enablePrintButton.setVisible(isDoc && !isPrint && !isFull);
        disablePrintButton.setVisible(isDoc && isPrint && !isFull);
        enableAuthorWarning.setVisible(!isWarning);
        disableAuthorWarning.setVisible(isWarning);
        enableSelectButton.setVisible(isDoc && !isSelect);
        disableSelectButton.setVisible(isDoc && isSelect);

        // Temporary
        printControlButton.setVisible(isDoc && isFull);

        add(saveButton);
        add(saveLinkButton);
        add(enablePrintButton.setOutputMarkupPlaceholderTag(true));
        add(disablePrintButton.setOutputMarkupPlaceholderTag(true));
        add(printControlButton.setOutputMarkupPlaceholderTag(true));
        add(enableAuthorWarning.setOutputMarkupPlaceholderTag(true));
        add(disableAuthorWarning.setOutputMarkupPlaceholderTag(true));
        add(enableSelectButton.setOutputMarkupPlaceholderTag(true));
        add(disableSelectButton.setOutputMarkupPlaceholderTag(true));
        saveButton.add(new AttributeModifier("title", AbstractDocActionPanel.this.getString("save.tooltip")));
        saveButton.add(new TooltipBehavior());
        enablePrintButton.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("enable.print.tooltip")));
        enablePrintButton.add(new TooltipBehavior());
        disablePrintButton.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("disable.print.tooltip")));
        disablePrintButton.add(new TooltipBehavior());
        printControlButton.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("control.print.tooltip")));
        printControlButton.add(new TooltipBehavior());
        enableAuthorWarning.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("enable.author.warning")));
        enableAuthorWarning.add(new TooltipBehavior());
        disableAuthorWarning.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("disable.author.warning")));
        disableAuthorWarning.add(new TooltipBehavior());

        enableSelectButton.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("enable.selection")));
        enableSelectButton.add(new TooltipBehavior());
        disableSelectButton.add(new AttributeModifier("title",
                AbstractDocActionPanel.this.getString("disable.selection")));
        disableSelectButton.add(new TooltipBehavior());

        add(printStatisticsPanelDialog);
    }

    abstract protected void onClick(AjaxRequestTarget target);

    protected boolean isForm() {
        return true;
    }
}
