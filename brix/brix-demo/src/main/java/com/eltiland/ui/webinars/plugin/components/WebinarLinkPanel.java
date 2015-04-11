package com.eltiland.ui.webinars.plugin.components;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Panel with check link for webinar user.
 *
 * @author Aleksey Plotnikov.
 */
abstract class WebinarLinkPanel extends BaseEltilandPanel<WebinarUserPayment> {

    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private Dialog<ShowLinkPanel> showLinkPanelDialog = new Dialog<ShowLinkPanel>("showLinkPanel", 500) {
        @Override
        public ShowLinkPanel createDialogPanel(String id) {
            return new ShowLinkPanel(id);
        }
    };

    protected WebinarLinkPanel(String id, IModel<WebinarUserPayment> webinarUserPaymentIModel) {
        super(id, webinarUserPaymentIModel);

        add(new EltiAjaxLink("checkLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                String link = null;
                try {
                    link = webinarUserPaymentManager.getLink(WebinarLinkPanel.this.getModelObject());
                } catch (EltilandManagerException e) {
                    e.printStackTrace();
                }
                if (link != null) {
                    showLinkPanelDialog.getDialogPanel().initPanel(link);
                    showLinkPanelDialog.show(target);
                }
            }

            @Override
            public boolean isVisible() {
                return showLink();
            }
        });

        add(showLinkPanelDialog);
    }

    abstract protected boolean showLink();


    private class ShowLinkPanel extends ELTDialogPanel {

        private ELTTextField linkField = new ELTTextField<String>("linkField",
                ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class) {
            @Override
            protected int getInitialWidth() {
                return 450;
            }
        };

        public ShowLinkPanel(String id) {
            super(id);
            form.add(linkField);
        }

        public void initPanel(String link) {
            linkField.setModelObject(link);
        }

        @Override
        protected String getHeader() {
            return getString("headerLink");
        }

        @Override
        protected List<EVENT> getActionList() {
            return null;
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        }
    }
}
