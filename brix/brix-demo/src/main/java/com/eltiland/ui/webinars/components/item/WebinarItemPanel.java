package com.eltiland.ui.webinars.components.item;

import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.payment.WebinarPaymentPage;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Webinar item panel.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class WebinarItemPanel extends AbstractItemPanel<Webinar> {

    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebinarItemPanel.class);

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return getUser();
        }
    };

    private IModel<WebinarUserPayment> paymentIModel = new LoadableDetachableModel<WebinarUserPayment>() {
        @Override
        protected WebinarUserPayment load() {
            return webinarUserPaymentManager.getPaymentForUser(getModelObject(), currentUserModel.getObject());
        }
    };

    private Dialog<LinkPanel> linkPanelDialog = new Dialog<LinkPanel>("linkPanel", 500) {
        @Override
        public LinkPanel createDialogPanel(String id) {
            return new LinkPanel(id);
        }
    };

    /**
     * Panel ctor.
     *
     * @param id            markup id.
     * @param webinarIModel item model.
     */
    public WebinarItemPanel(String id, IModel<Webinar> webinarIModel) {
        super(id, webinarIModel);
        add(new Label("start_date",
                String.format(getString("date_start"), DateUtils.formatRussianDate(getModelObject().getStartDate()))));

        Label status = new Label("status", new Model<String>());
        status.setVisible(getModelObject().getStartDate().after(DateUtils.getCurrentDate()));
        boolean paid = paymentIModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
        status.setDefaultModelObject(paid ? getString("open") :
                String.format(getString("closed"), paymentIModel.getObject().getPrice().toString()));
        status.add(new AttributeAppender("class", new Model<>(paid ? "open" : "closed"), " "));

        add(status);
        add(linkPanelDialog);
    }

    @Override
    protected WebComponent getIcon(String markupId) {
        return new StaticImage(markupId, UrlUtils.StandardIcons.ICON_ITEM_WEBINAR.getPath());
    }

    @Override
    protected String getIconLabel() {
        return getString("webinar");
    }

    @Override
    protected String getEntityName(IModel<Webinar> itemModel) {
        return itemModel.getObject().getName();
    }

    @Override
    protected String getEntityDescription(IModel<Webinar> itemModel) {
        return itemModel.getObject().getDescription();
    }

    @Override
    protected List<ButtonAction> getActionList() {
        return new ArrayList<>(Arrays.asList(ButtonAction.DOWNLOAD, ButtonAction.PREVIEW, ButtonAction.PAY));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        switch (action) {
            case DOWNLOAD:
                return new ResourceModel("download.action");
            case PREVIEW:
                return new ResourceModel("preview.action");
            case PAY:
                return new ResourceModel("pay.action");
            default:
                return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        boolean logged = currentUserModel.getObject() != null;
        boolean history = getModelObject().getStartDate().before(DateUtils.getCurrentDate());
        switch (action) {
            case DOWNLOAD:
                return logged && history && !(getModelObject().isCourse());
            case PREVIEW:
                return logged && !history && paymentIModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
            case PAY:
                return logged && !history && !paymentIModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
            default:
                return false;
        }
    }

    @Override
    protected void onClick(ButtonAction action, AjaxRequestTarget target) {
        switch (action) {
            case DOWNLOAD:
                ajaxDownload.initiate(target);
                break;
            case PAY:
                setResponsePage(WebinarPaymentPage.class, new PageParameters().add(
                        UrlUtils.PAYMENT_CODE_PARAMETER_NAME, paymentIModel.getObject().getPaylink()));
                break;
            case PREVIEW:
                linkPanelDialog.show(target);
                break;
        }
    }

    @Override
    protected AbstractAjaxBehavior getAdditionalBehavior(ButtonAction action) {
        if (action.equals(ButtonAction.DOWNLOAD)) {
            return ajaxDownload;
        } else {
            return super.getAdditionalBehavior(action);
        }
    }

    final AjaxDownload ajaxDownload = new AjaxDownload() {
        @Override
        protected String getFileName() {
            return "certificate.pdf";
        }

        @Override
        protected IResourceStream getResourceStream() {
            return new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    if (paymentIModel.getObject() != null) {
                        try {
                            return webinarCertificateGenerator.generateCertificate(paymentIModel.getObject());
                        } catch (EltilandManagerException e) {
                            LOGGER.error("Can't generate certificate", e);
                            throw new WicketRuntimeException("Can't generate certificate", e);
                        }
                    } else {
                        return null;
                    }
                }

                @Override
                public void close() throws IOException {
                }
            };
        }
    };

    protected abstract User getUser();

    private class LinkPanel extends ELTDialogPanel {

        private ELTTextField<String> linkField =
                new ELTTextField<String>("link",
                        ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class) {
                    @Override
                    protected int getInitialWidth() {
                        return 460;
                    }
                };

        public LinkPanel(String id) {
            super(id);
            linkField.setReadonly(true);
            form.add(linkField);
            try {
                linkField.setModelObject(webinarUserPaymentManager.getLink(paymentIModel.getObject()));
            } catch (EltilandManagerException e) {
                LOGGER.error("Can't get link to webinar", e);
                throw new WicketRuntimeException("Can't get link to webinar", e);
            }
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
    }
}
