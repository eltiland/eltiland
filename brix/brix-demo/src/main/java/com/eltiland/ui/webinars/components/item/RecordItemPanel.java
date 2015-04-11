package com.eltiland.ui.webinars.components.item;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarRecordPaymentManager;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.payment.RecordPaymentPage;
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
public class RecordItemPanel extends AbstractItemPanel<WebinarRecord> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordItemPanel.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private WebinarRecordPaymentManager webinarRecordPaymentManager;
    @SpringBean
    private WebinarCertificateGenerator webinarCertificateGenerator;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<WebinarRecordPayment> paymentIModel = new LoadableDetachableModel<WebinarRecordPayment>() {
        @Override
        protected WebinarRecordPayment load() {
            return webinarRecordPaymentManager.getPaymentForUser(getModelObject(), currentUserModel.getObject());
        }
    };

    /**
     * Panel ctor.
     *
     * @param id                  markup id.
     * @param webinarRecordIModel record item
     */
    public RecordItemPanel(String id, IModel<WebinarRecord> webinarRecordIModel) {
        super(id, webinarRecordIModel);

        Label status = new Label("status", new Model<String>());
        boolean paid = paymentIModel.getObject().getStatus();
        status.setDefaultModelObject(paid ? getString("open") :
                String.format(getString("closed"), paymentIModel.getObject().getPrice().toString()));
        status.add(new AttributeAppender("class", new Model<>(paid ? "open" : "closed"), " "));

        add(status);
    }

    @Override
    protected WebComponent getIcon(String markupId) {
        return new StaticImage(markupId, UrlUtils.StandardIcons.ICON_ITEM_RECORD.getPath());
    }

    @Override
    protected String getIconLabel() {
        return getString("record");
    }

    @Override
    protected String getEntityName(IModel<WebinarRecord> itemModel) {
        return itemModel.getObject().getName();
    }

    @Override
    protected String getEntityDescription(IModel<WebinarRecord> itemModel) {
        genericManager.initialize(itemModel.getObject(), itemModel.getObject().getWebinar());
        return itemModel.getObject().getWebinar().getDescription();
    }

    @Override
    protected List<ButtonAction> getActionList() {
        return new ArrayList<>(Arrays.asList(ButtonAction.DOWNLOAD, ButtonAction.PAY));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        switch (action) {
            case DOWNLOAD:
                return new ResourceModel("download.action");
            case PAY:
                return new ResourceModel("pay.action");
            default:
                return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        switch (action) {
            case DOWNLOAD:
                return paymentIModel.getObject().getStatus();
            case PAY:
                return !(paymentIModel.getObject().getStatus());
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
                setResponsePage(RecordPaymentPage.class, new PageParameters().add(
                        UrlUtils.PAYMENT_CODE_PARAMETER_NAME, paymentIModel.getObject().getPayLink()));
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
                            return webinarCertificateGenerator.generateRecordCertificate(paymentIModel.getObject());
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
}
