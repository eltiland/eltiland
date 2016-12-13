package com.eltiland.ui.webinars.components.datatable;

import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.datatable.EltiDataProviderBase;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.webinars.components.WebinarActionPanel;
import com.eltiland.ui.webinars.components.WebinarLoginPanel;
import com.eltiland.ui.webinars.components.WebinarNewUserPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * Data table for available webinars.
 *
 * @author Aleksey Plotnikov
 */
public abstract class AvailableWebinarDataTablePanel extends WebinarDataTablePanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarDataTablePanel.class);

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;


    private Dialog<WebinarNewUserPanel> webinarNewUserPanelDialog
            = new Dialog<WebinarNewUserPanel>("addUserDialog", 330) {
        @Override
        public WebinarNewUserPanel createDialogPanel(String id) {
            return new WebinarNewUserPanel(id) {
                @Override
                public void onLogin(AjaxRequestTarget target, IModel<Webinar> webinarIModel) {
                    close(target);
                    loginPanelDialog.getDialogPanel().initWebinarData(webinarIModel);
                    loginPanelDialog.show(target);
                }
            };
        }

        @Override
        public void registerCallback(WebinarNewUserPanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<WebinarUserPayment>() {
                @Override
                public void process(IModel<WebinarUserPayment> model, AjaxRequestTarget target) {
                    try {
                        webinarUserPaymentManager.createUser(model.getObject());
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create webinar user", e);
                        throw new WicketRuntimeException("Cannot create webinar user", e);
                    } catch (EmailException e) {
                        LOGGER.error("Cannot send mail to user", e);
                        throw new WicketRuntimeException("Cannot send mail to user", e);
                    } catch (WebinarException e) {
                        e.printStackTrace();
                    }

                    close(target);

                    boolean isFree = model.getObject().getPrice() == null
                            || model.getObject().getPrice().equals(BigDecimal.valueOf(0));

                    ELTAlerts.renderOKPopup(getString(isFree ? "signupFreeMessage" : "signupPaidMessage"), target);
                    onChange(target);
                }
            });
        }
    };

    private Dialog<WebinarLoginPanel> loginPanelDialog = new Dialog<WebinarLoginPanel>("loginDialog", 340) {
        @Override
        public WebinarLoginPanel createDialogPanel(String id) {
            return new WebinarLoginPanel(id);
        }
    };

    /**
     * Table constructor.
     *
     * @param id      panel's id.
     * @param maxRows rows limit
     */
    public AvailableWebinarDataTablePanel(String id, int maxRows) {
        super(id, new EltiDataProviderBase<Webinar>() {
            @SpringBean
            private WebinarManager webinarManager;

            @Override
            public Iterator iterator(int first, int count) {
                return webinarManager.getWebinarAvailableList(
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            public int size() {
                return webinarManager.getWebinarAvailableCount();
            }
        }, maxRows);

        add(webinarNewUserPanelDialog);
        add(loginPanelDialog);
    }

    @Override
    protected AbstractColumn<Webinar> getActionColumn() {
        return new AbstractColumn<Webinar>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
            @Override
            public void populateItem(Item<ICellPopulator<Webinar>> cellItem, String componentId,
                                     final IModel<Webinar> rowModel) {
                cellItem.add(new WebinarActionPanel(componentId, WebinarActionPanel.ACTION.REG) {
                    @Override
                    public void onAction(AjaxRequestTarget target) {
                        webinarNewUserPanelDialog.getDialogPanel().initWebinarData(rowModel.getObject());
                        webinarNewUserPanelDialog.show(target);
                    }

                    @Override
                    public void onActionMany(AjaxRequestTarget target) {
                        onAddManyUsers(rowModel, target);
                    }
                });
            }
        };
    }

    @Override
    protected AbstractColumn<Webinar> getStatusColumn() {
        return null;
    }

    public abstract void onChange(AjaxRequestTarget target);
}
