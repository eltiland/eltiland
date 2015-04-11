package com.eltiland.ui.webinars.plugin.tab.record;

import com.eltiland.bl.GenericManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarRecord;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Webinar record property panel.
 *
 * @author Aleksey Plotnikov.
 */
public class WRPropertyPanel extends ELTDialogPanel
        implements IDialogNewCallback<WebinarRecord>, IDialogUpdateCallback<WebinarRecord> {

    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(WRPropertyPanel.class);

    private boolean mode;
    private IModel<WebinarRecord> webinarRecordIModel = new GenericDBModel<>(WebinarRecord.class);
    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    private IDialogUpdateCallback.IDialogActionProcessor<WebinarRecord> updateCallback;
    private IDialogNewCallback.IDialogActionProcessor<WebinarRecord> newCallback;

    private ELTTextArea nameField = new ELTTextArea(
            "nameField", new ResourceModel("nameField"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    private ELTTextArea linkField = new ELTTextArea(
            "linkField", new ResourceModel("linkField"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };
    private ELTTextField passField = new ELTTextField(
            "passwordField", new ResourceModel("passwordField"), new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return 310;
        }
    };
    private PriceField priceField = new PriceField("price",
            new ResourceModel("recordPriceLabel"), new Model<BigDecimal>());
    private ELTAjaxCheckBox freeRecordCheck = new ELTAjaxCheckBox(
            "freeRecordCheck", new ResourceModel("freeRecordLabel"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            boolean value = getModelObject();
            priceField.setEnabled(!value);
            if (value) {
                priceField.setValue(BigDecimal.ZERO);
            }
            target.add(priceField);
        }
    };

    public WRPropertyPanel(String id) {
        super(id);
        form.add(nameField);
        form.add(linkField);
        form.add(passField);
        form.add(priceField.setOutputMarkupId(true));
        form.add(freeRecordCheck);
    }

    public void initCreateMode(Webinar webinar) {
        this.webinarIModel.setObject(webinar);
        mode = false;
        nameField.setModelObject(webinar.getName());
        linkField.setModelObject(null);
        passField.setModelObject(null);
        priceField.setValue(BigDecimal.ZERO);
        freeRecordCheck.setModelObject(true);
        priceField.setEnabled(false);
    }

    public void initEditMode(WebinarRecord webinarRecord) {
        this.webinarRecordIModel.setObject(webinarRecord);
        genericManager.initialize(webinarRecord, webinarRecord.getWebinar());
        this.webinarIModel.setObject(webinarRecord.getWebinar());

        nameField.setModelObject(webinarRecord.getName());
        linkField.setModelObject(webinarRecord.getLink());
        passField.setModelObject(webinarRecord.getPassword());
        priceField.setValue(webinarRecord.getPrice());

        boolean isFree = webinarRecord.getPrice().longValue() == 0;
        freeRecordCheck.setModelObject(isFree);
        priceField.setEnabled(!isFree);

        mode = true;
    }

    @Override
    protected String getHeader() {
        return getString(mode ? "editHeader" : "createHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        WebinarRecord record = (event.equals(EVENT.Create) ? new WebinarRecord() : webinarRecordIModel.getObject());
        record.setName(nameField.getModelObject());
        record.setLink(linkField.getModelObject());
        record.setPassword((String) passField.getModelObject());
        record.setPrice(priceField.getPriceValue());
        record.setWebinar(webinarIModel.getObject());

        if (event.equals(EVENT.Create)) {
            try {
                genericManager.saveNew(record);
            } catch (ConstraintException e) {
                LOGGER.error("Cannot create webinar record", e);
                throw new WicketRuntimeException("Cannot create webinar record", e);
            }

            newCallback.process(new GenericDBModel<>(WebinarRecord.class, record), target);
        } else if (event.equals(EVENT.Save)) {
            try {
                genericManager.update(record);
            } catch (ConstraintException e) {
                LOGGER.error("Cannot update webinar record", e);
                throw new WicketRuntimeException("Cannot update webinar record", e);
            }
            updateCallback.process(new GenericDBModel<>(WebinarRecord.class, record), target);
        }
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        if (event.equals(EVENT.Create)) {
            return !mode;
        } else {
            return mode;
        }
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<WebinarRecord> callback) {
        this.newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<WebinarRecord> callback) {
        this.updateCallback = callback;
    }
}
