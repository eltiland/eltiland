package com.eltiland.ui.course.edit;

import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.common.components.dialog.Dialog;
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
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for controlling print statistics
 */
public class GooglePrintStatisticsPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTDocumentCourseItem> {

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
                    return getString("limit.small");
                } else {
                    return getString("limit.big");
                }
            }
        }
    };

    private Label statusLabel = new Label("label.status", status);
    private Label limitInfoLabel = new Label("limit.label", new ResourceModel("limit.label"));
    private Label limitCountLabel = new Label("limit.count", countModel);

    private Dialog<LimitPanel> limitPanelDialog = new Dialog<LimitPanel>("limitDialog", 205) {
        @Override
        public LimitPanel createDialogPanel(String id) {
            return new LimitPanel(id);
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

    public void initData(IModel<ELTGoogleCourseItem> itemModel) {
        this.itemModel.setObject((ELTDocumentCourseItem) (itemModel.getObject()));
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
            callback.process(itemModel, target);
        }
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

        private Spinner spinner = new Spinner("spinner");

        public LimitPanel(String id) {
            super(id);
            form.add(spinner);
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

        }

        @Override
        public void setUploadCallback(IDialogActionProcessor<Long> callback) {
            this.callback = callback;
        }
    }
}
