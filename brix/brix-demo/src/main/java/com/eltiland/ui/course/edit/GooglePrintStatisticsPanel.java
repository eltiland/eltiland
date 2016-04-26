package com.eltiland.ui.course.edit;

import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUploadCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.googlecode.wicket.jquery.ui.form.spinner.Spinner;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for controlling print statistics
 */
public class GooglePrintStatisticsPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTDocumentCourseItem> {

    @SpringBean
    private ELTCourseItemManager courseItemManager;

    private IModel<ELTDocumentCourseItem> itemModel = new GenericDBModel<>(ELTDocumentCourseItem.class);

    private IDialogActionProcessor<ELTDocumentCourseItem> callback;

    private IModel<String> status = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            {
                return getString(itemModel.getObject().isPrintable() ? "print.enabled" : "print.disabled");
            }
        }
    };

    private IModel<String> countModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            boolean printable = itemModel.getObject().isPrintable();
            if (!printable) {
                return getString("limit.null");
            } else {
                Long limit = itemModel.getObject().getLimit();
                if (limit == null || limit == 0) {
                    return getString("limit.null");
                } else if (limit > 0 && limit < 5) {
                    return String.format(getString("limit.small"), limit);
                } else {
                    return String.format(getString("limit.big"), limit);
                }
            }
        }
    };

    private Label statusLabel = new Label("label.status", status);
    private Label limitInfoLabel = new Label("limit.label", new ResourceModel("limit.label"));
    private Label limitCountLabel = new Label("limit.count", countModel);

    private Dialog<LimitPanel> limitPanelDialog = new Dialog<LimitPanel>("limitDialog", 240) {
        @Override
        public LimitPanel createDialogPanel(String id) {
            return new LimitPanel(id);
        }

        @Override
        public void registerCallback(LimitPanel panel) {
            super.registerCallback(panel);
            panel.setUploadCallback(new IDialogUploadCallback.IDialogActionProcessor<Long>() {
                @Override
                public void process(IModel<Long> uploadedFileModel, AjaxRequestTarget target) {
                    close(target);

                    itemModel.getObject().setLimit(uploadedFileModel.getObject());
                    try {
                        courseItemManager.update(itemModel.getObject());
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                    countModel.detach();
                    target.add(limitContainer);
                }
            });
        }
    };

    private WebMarkupContainer limitContainer = new WebMarkupContainer("limit.container") {
        @Override
        public boolean isVisible() {
            return itemModel.getObject().isPrintable();
        }
    };

    private void redrawInfo(AjaxRequestTarget target, boolean isPrintable) {
        itemModel.getObject().setPrintable(isPrintable);
        status.detach();
        countModel.detach();
        target.add(statusLabel);
        target.add(applyButton);
        target.add(denyButton);
        target.add(limitContainer);
    }

    private IconButton applyButton = new IconButton(
            "apply.print.action", new ResourceModel("apply.action"), ButtonAction.APPLY) {
        @Override
        protected void onClick(AjaxRequestTarget target) {
            itemModel.getObject().setPrintable(true);
            itemModel.getObject().setLimit((long) 1);
            try {
                courseItemManager.update(itemModel.getObject());
            } catch (CourseException e) {
                ELTAlerts.renderErrorPopup(e.getMessage(), target);
            }
            redrawInfo(target, true);
        }

        @Override
        public boolean isVisible() {
            return !(itemModel.getObject().isPrintable());
        }
    };

    private IconButton denyButton = new IconButton(
            "deny.print.action", new ResourceModel("deny.action"), ButtonAction.REMOVE) {
        @Override
        protected void onClick(AjaxRequestTarget target) {
            itemModel.getObject().setPrintable(false);
            itemModel.getObject().setLimit((long) 1);
            try {
                courseItemManager.update(itemModel.getObject());
            } catch (CourseException e) {
                ELTAlerts.renderErrorPopup(e.getMessage(), target);
            }
            redrawInfo(target, false);
        }

        @Override
        public boolean isVisible() {
            return itemModel.getObject().isPrintable();
        }
    };

    private IconButton changeButton = new IconButton(
            "limit.change.action", new ResourceModel("change.action"), ButtonAction.SETTINGS) {
        @Override
        protected void onClick(AjaxRequestTarget target) {
            limitPanelDialog.getDialogPanel().initData(itemModel.getObject().getLimit());
            limitPanelDialog.show(target);
        }
    };

    public GooglePrintStatisticsPanel(String id) {
        super(id);
        form.add(statusLabel.setOutputMarkupId(true));
        form.add(applyButton.setOutputMarkupPlaceholderTag(true));
        form.add(denyButton.setOutputMarkupPlaceholderTag(true));
        form.add(limitContainer.setOutputMarkupPlaceholderTag(true));
        form.add(limitPanelDialog);
        limitContainer.add(limitInfoLabel);
        limitContainer.add(limitCountLabel);
        limitContainer.add(changeButton);
    }

    public void initData(IModel<ELTDocumentCourseItem> itemModel) {
        this.itemModel.setObject(itemModel.getObject());
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>();
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    public void setUpdateCallback(IDialogActionProcessor<ELTDocumentCourseItem> callback) {
        this.callback = callback;
    }


    private class LimitPanel extends ELTDialogPanel implements IDialogUploadCallback<Long> {

        private IDialogActionProcessor<Long> callback;

        private Spinner<Long> spinner = new Spinner<>("spinner", new Model<Long>());

        public LimitPanel(String id) {
            super(id);
            form.add(spinner);
            spinner.setMin(1);
        }

        public void initData(Long data) {
            spinner.setModelObject(data);
        }

        @Override
        protected String getHeader() {
            return GooglePrintStatisticsPanel.this.getString("limit.label");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                String spinnerValue = spinner.getDefaultModelObjectAsString();
                callback.process(new Model<>(Long.parseLong(spinnerValue)), target);
            }
        }

        @Override
        public void setUploadCallback(IDialogActionProcessor<Long> callback) {
            this.callback = callback;
        }
    }
}
