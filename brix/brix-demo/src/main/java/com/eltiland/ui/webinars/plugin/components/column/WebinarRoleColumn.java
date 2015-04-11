package com.eltiland.ui.webinars.plugin.components.column;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.webinars.plugin.components.WebinarChangeRolePanel;
import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Column for output and changing webinar role for user.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class WebinarRoleColumn<T> extends AbstractColumn<T, WebinarUserPayment> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(WebinarRoleColumn.class);

    /**
     * Column constructor.
     *
     * @param columnId     column ID.
     * @param headerModel  header label model.
     * @param sortProperty sort property.
     */
    public WebinarRoleColumn(String columnId, IModel<String> headerModel, String sortProperty) {
        super(columnId, headerModel, sortProperty);
    }

    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, IModel<WebinarUserPayment> rowModel) {
        return new RolePanel(componentId, rowModel) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                WebinarRoleColumn.this.onUpdate(target);
            }
        };

    }

    protected abstract void onUpdate(AjaxRequestTarget target);

    private class RolePanel extends BaseEltilandPanel<WebinarUserPayment> {

        @SpringBean
        private WebinarUserPaymentManager webinarUserPaymentManager;
        @SpringBean
        private EmailMessageManager emailMessageManager;
        @SpringBean
        private GenericManager genericManager;

        private WebinarUserPayment.Role previousRole;


        private Dialog<WebinarChangeRolePanel> changeRolePanelDialog =
                new Dialog<WebinarChangeRolePanel>("changeRoleDialog", 365) {
                    @Override
                    public WebinarChangeRolePanel createDialogPanel(String id) {
                        return new WebinarChangeRolePanel(id, RolePanel.this.getModel());
                    }

                    @Override
                    public void registerCallback(WebinarChangeRolePanel panel) {
                        super.registerCallback(panel);
                        panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<WebinarUserPayment>() {
                            @Override
                            public void process(IModel<WebinarUserPayment> model, AjaxRequestTarget target) {
                                if (!(model.getObject().getRole().equals(previousRole))) {
                                    try {
                                        webinarUserPaymentManager.updateWebinarUser(model.getObject());
                                        genericManager.initialize(model.getObject(), model.getObject().getWebinar());
                                        emailMessageManager.sendWebinarChangeRoleToUser(model.getObject());
                                        ELTAlerts.renderOKPopup(getString("changeRoleMessage"), target);
                                    } catch (EltilandManagerException e) {
                                        LOGGER.error("Cannot update user in webinar", e);
                                        throw new WicketRuntimeException("Cannot update user in webinar", e);
                                    } catch (EmailException e) {
                                        LOGGER.error("Cannot send message to user about role changing", e);
                                        throw new WicketRuntimeException(
                                                "Cannot send message to user about role changing", e);
                                    }
                                }
                                close(target);
                                onUpdate(target);
                            }
                        });
                    }
                };

        private RolePanel(String id, IModel<WebinarUserPayment> webinarUserPaymentIModel) {
            super(id, webinarUserPaymentIModel);
            previousRole = getModelObject().getRole();

            add(new Label("roleLabel", getString(getModelObject().getRole().toString())));
            add(new EltiAjaxLink("changeLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    changeRolePanelDialog.show(target);
                }
            });

            add(changeRolePanelDialog);
        }

        protected void onUpdate(AjaxRequestTarget target) {
        }
    }
}
