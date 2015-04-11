package com.eltiland.ui.webinars.plugin.tab;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.WebinarCourseItem;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.webinars.plugin.components.WebinarInvoiceActionPanel;
import com.eltiland.ui.webinars.plugin.components.WebinarPropertyPanel;
import com.eltiland.ui.webinars.plugin.tab.components.WebinarDataGridPanel;
import com.eltiland.ui.webinars.plugin.tab.components.WebinarDataSource;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.brixcms.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Webinars invoices management panel.
 *
 * @author Aleksey Plotnikov
 */
public class WInvoiceManagementPanel extends BaseEltilandPanel<Workspace> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WInvoiceManagementPanel.class);

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    private WebinarDataGridPanel grid;

    private final Dialog<WebinarPropertyPanel> webinarPropertyPanelDialog =
            new Dialog<WebinarPropertyPanel>("editWebinarDialog", 350) {
                @Override
                public WebinarPropertyPanel createDialogPanel(String id) {
                    return new WebinarPropertyPanel(id);
                }

                @Override
                public void registerCallback(WebinarPropertyPanel panel) {
                    super.registerCallback(panel);
                    panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<Webinar>() {
                        @Override
                        public void process(IModel<Webinar> model, AjaxRequestTarget target) {
                            close(target);
                            ELTAlerts.renderOKPopup(getString("messageEditSuccess"), target);
                            grid.updateGrid();
                        }
                    });
                }
            };

    /**
     * Panel constructor.
     *
     * @param id              panel's ID.
     * @param workspaceIModel workspace model.
     */
    public WInvoiceManagementPanel(String id, IModel<Workspace> workspaceIModel) {
        super(id, workspaceIModel);

        grid = new WebinarDataGridPanel("grid") {
            @Override
            protected boolean isApproved() {
                return false;
            }

            @Override
            protected AbstractColumn<WebinarDataSource, Webinar> getActionColumn() {
                return new AbstractColumn<WebinarDataSource, Webinar>(
                        "actionPanel", new ResourceModel("actionTabLabel")) {
                    @Override
                    public Component newCell(WebMarkupContainer components, String s, final IModel<Webinar> webinarIModel) {
                        return new WebinarInvoiceActionPanel(s, webinarIModel) {
                            @Override
                            public void onEdit(AjaxRequestTarget target) {
                                webinarPropertyPanelDialog.getDialogPanel().setWebinarModel(webinarIModel);
                                webinarPropertyPanelDialog.show(target);
                            }

                            @Override
                            public void onCancel(AjaxRequestTarget target) {
                                WebinarCourseItem item = webinarIModel.getObject().getCourseItem();
                                if (item != null) {
                                    item.setWebinar(null);
                                    try {
                                        courseItemManager.updateCourseItem(item);

                                        genericManager.initialize(webinarIModel.getObject(),
                                                webinarIModel.getObject().getWebinarUserPayments());
                                        for (WebinarUserPayment payment :
                                                webinarIModel.getObject().getWebinarUserPayments()) {
                                            genericManager.delete(payment);
                                        }

                                        webinarManager.remove(webinarIModel.getObject());
                                        genericManager.delete(item);
                                    } catch (EltilandManagerException e) {
                                        LOGGER.error("Cannot delete webinar", e);
                                        throw new WicketRuntimeException("Cannot delete webinar", e);
                                    }
                                    ELTAlerts.renderOKPopup(getString("messageDeleteSuccess"), target);
                                    target.add(grid);
                                }
                            }

                            @Override
                            public void onApply(AjaxRequestTarget target) {
                                WebinarUserPayment payment =
                                        webinarUserPaymentManager.getWebinarPayments(webinarIModel.getObject()).get(0);

                                webinarIModel.getObject().setApproved(true);
                                try {
                                    genericManager.update(webinarIModel.getObject());
                                    webinarManager.apply(webinarIModel.getObject(), payment);
                                } catch (ConstraintException | EltilandManagerException e) {
                                    LOGGER.error("Cannot apply webinar", e);
                                    throw new WicketRuntimeException("Cannot apply webinar", e);
                                }

                                ELTAlerts.renderOKPopup(getString("messageApplySuccess"), target);
                                target.add(grid);
                            }
                        };
                    }
                };
            }
        };

        add(grid.setOutputMarkupId(true));
        add(webinarPropertyPanelDialog);
    }
}
