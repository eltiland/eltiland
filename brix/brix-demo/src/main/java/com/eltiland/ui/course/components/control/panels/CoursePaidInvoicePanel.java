package com.eltiland.ui.course.components.control.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.paidservice.CoursePaidInvoice;
import com.eltiland.model.course.paidservice.CoursePaidTerm;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for creating and modifying course paid invoice.
 *
 * @author Aleksey PLotnikov.
 */
public class CoursePaidInvoicePanel extends ELTDialogPanel implements
        IDialogNewCallback<CoursePaidInvoice>, IDialogUpdateCallback<CoursePaidInvoice> {

    @SpringBean
    private GenericManager genericManager;

    private IDialogNewCallback.IDialogActionProcessor<CoursePaidInvoice> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<CoursePaidInvoice> updateCallback;

    private final String CSS = "static/css/panels/course_paidinvoice.css";

    private boolean editMode = false;
    private IModel<CoursePaidInvoice> paidInvoiceIModel = new GenericDBModel<>(CoursePaidInvoice.class);
    private IModel<CoursePaidTerm> termModel = new GenericDBModel<>(CoursePaidTerm.class);

    private IModel<String> termLabelModel = new LoadableDetachableModel<String>() {
        @Override
        protected String load() {
            CoursePaidTerm term = termModel.getObject();
            if ((term != null) && ((term.getYears() != 0) || (term.getMonths() != 0) || (term.getDays() != 0))) {
                String label = getString("yesTerm");
                label += getDateString(term.getYears(), "year", "years", "years_many");
                label += getDateString(term.getMonths(), "month", "months", "months_many");
                label += getDateString(term.getDays(), "day", "days", "days_many");

                return label;
            } else {
                return getString("noTerm");
            }
        }
    };

    private Dialog<CourseTermPanel> termDialog = new Dialog<CourseTermPanel>("termDialog", 625) {
        @Override
        public CourseTermPanel createDialogPanel(String id) {
            return new CourseTermPanel(id);
        }

        @Override
        public void registerCallback(CourseTermPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<CoursePaidTerm>() {
                @Override
                public void process(IModel<CoursePaidTerm> model, AjaxRequestTarget target) {
                    termModel = model;
                    termLabelModel.detach();
                    target.add(termLabel);
                    close(target);
                }
            });
        }
    };

    private PriceField priceField = new PriceField("price", new ResourceModel("priceLabel"), new Model<BigDecimal>());
    private Label termLabel = new Label("termLabel", termLabelModel);
    private Label termButtonLabel = new Label("termButtonLabel", new Model<String>());
    private EltiAjaxLink termButton = new EltiAjaxLink("termButton") {
        @Override
        public void onClick(AjaxRequestTarget target) {
            CoursePaidInvoice invoice = paidInvoiceIModel.getObject();
            if (invoice != null) {
                genericManager.initialize(invoice, invoice.getTerm());
                termDialog.getDialogPanel().initEditMode(new GenericDBModel<>(CoursePaidTerm.class, invoice.getTerm()));
            } else {
                termDialog.getDialogPanel().initCreateMode();
            }
            termDialog.show(target);
        }

        @Override
        protected IAjaxCallDecorator getAjaxCallDecorator() {
            return null;
        }
    };

    public CoursePaidInvoicePanel(String id) {
        super(id);
        form.add(priceField);
        form.add(termLabel.setOutputMarkupId(true));
        form.add(termButton);
        termButton.add(termButtonLabel);
        form.add(termDialog);
    }

    public void initCreateMode() {
        editMode = false;
        termButtonLabel.setDefaultModelObject(getString("setTerm"));
        priceField.setValue(BigDecimal.ZERO);
    }

    public void initEditMode(IModel<CoursePaidInvoice> invoiceIModel) {
        editMode = true;
        paidInvoiceIModel = invoiceIModel;
        priceField.setValue(paidInvoiceIModel.getObject().getPrice());

        genericManager.initialize(paidInvoiceIModel.getObject(), paidInvoiceIModel.getObject().getTerm());
        termModel.setObject(paidInvoiceIModel.getObject().getTerm());
        termLabelModel.detach();
        termButtonLabel.setDefaultModelObject(getString("editTerm"));
    }

    @Override
    protected String getHeader() {
        return getString(editMode ? "headerLabelModify" : "headerLabelCreate");
    }

    @Override
    protected boolean actionSelector(EVENT event) {
        return event.equals(EVENT.Create) && !editMode || event.equals(EVENT.Save) && editMode;
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Create, EVENT.Save));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        CoursePaidInvoice invoice = new CoursePaidInvoice();
        invoice.setPrice(priceField.getModelObject());

        if (termModel.getObject() != null) {
            invoice.setTerm(termModel.getObject());
        }

        if (event.equals(EVENT.Create)) {
            newCallback.process(new Model<>(invoice), target);
        } else if (event.equals(EVENT.Save)) {
            updateCallback.process(new Model<>(invoice), target);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<CoursePaidInvoice> callback) {
        newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<CoursePaidInvoice> callback) {
        updateCallback = callback;
    }

    private String getDateString(int value, String one, String couple, String many) {
        if (value != 0) {
            if (value == 1) {
                return getString(one);
            } else if ((value > 1) && (value < 5)) {
                return String.format(getString(couple), value);
            } else {
                return String.format(getString(many), value);
            }
        } else {
            return "";
        }
    }
}
