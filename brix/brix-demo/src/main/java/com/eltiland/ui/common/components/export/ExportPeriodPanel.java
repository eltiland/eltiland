package com.eltiland.ui.common.components.export;

import com.eltiland.bl.ExportManager;
import com.eltiland.model.export.Exportable;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.checkbox.ELTAjaxCheckBox;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.textfield.ELTDateField;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.EncodingUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Panel for selecting period
 */
public class ExportPeriodPanel<T extends Exportable> extends ELTDialogPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportPeriodPanel.class);

    @SpringBean
    private ExportManager<T> exportManager;

    private ELTDateField startField =
            new ELTDateField("startField", new ResourceModel("start"), new Model<Date>());
    private ELTDateField endField =
            new ELTDateField("endField", new ResourceModel("end"), new Model<Date>());

    private Class clazz;

    private IModel<List<T>> paymentsModel;

    private final String FILENAME_1 = "/tmp/test.csv";
    private final String FILENAME_2 = "/tmp/test2.csv";
    private final String ENCODING = "Cp1251";

    private AjaxDownload ajaxDownload = new AjaxDownload() {

        @Override
        protected IResourceStream getResourceStream() {

            File file = new File(FILENAME_1);
            if (file.exists()) {
                file.delete();
            }
            File file2 = new File(FILENAME_2);
            if (file2.exists()) {
                file2.delete();
            }

            try {
                boolean created = file.createNewFile();
                if (created) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    String stringStream = getString("headerCSV") + "\n";
                    for (T client : paymentsModel.getObject()) {
                        BigDecimal price = client.getPrice();
                        Date date = client.getDate();

                        stringStream += client.getName() + ";"
                                + ((price != null) ? price.toString() : "0.00") + ";"
                                + ((date != null) ? DateUtils.formatFullDate(client.getDate()) : "") + ";"
                                + client.getDescription() + "\n";
                    }

                    writer.write(stringStream);
                    writer.close();
                    EncodingUtils.convert(FILENAME_1, FILENAME_2, System.getProperty("file.encoding"), ENCODING);
                }
            } catch (IOException e) {
                LOGGER.error("Got exception when constructing the report", e);
                throw new WicketRuntimeException("Got exception when constructing the report", e);
            }
            return new FileResourceStream(file2);
        }

        @Override
        protected String getFileName() {
            return "report.csv";
        }
    };

    private EltiAjaxSubmitLink downloadButton = new EltiAjaxSubmitLink("download") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

            boolean full = fullCheckBox.getModelObject();

            List<T> clients = full ?
                    exportManager.getAllPayments(clazz) :
                    exportManager.getPaymentsForPeriod(clazz, startField.getModelObject(), endField.getModelObject());
            if (clients.isEmpty()) {
                ELTAlerts.renderErrorPopup(getString("noPayments"), target);
            } else {
                paymentsModel.setObject(clients);
                ajaxDownload.initiate(target);
            }
        }
    };

    private ELTAjaxCheckBox fullCheckBox = new ELTAjaxCheckBox(
            "fullCheckBox", new ResourceModel("fullPeriod"), new Model<>(false)) {
        @Override
        protected void onUpdate(AjaxRequestTarget target) {
            boolean value = getModelObject();
            startField.setReadonly(value);
            endField.setReadonly(value);
            target.add(startField);
            target.add(endField);
        }
    };

    public ExportPeriodPanel(String id, Class<T> clazz) {
        super(id);

        this.clazz = clazz;
        paymentsModel = new LoadableDetachableModel<List<T>>() {
            @Override
            protected List<T> load() {
                return new ArrayList<>();
            }
        };
        form.add(startField.setOutputMarkupId(true));
        form.add(endField.setOutputMarkupId(true));
        form.add(downloadButton);
        form.add(fullCheckBox);
        downloadButton.add(ajaxDownload);

        form.add(new AbstractFormValidator() {

            @Override
            public FormComponent<?>[] getDependentFormComponents() {
                return new FormComponent<?>[]{startField, endField, fullCheckBox};
            }

            @Override
            public void validate(Form form) {
                Date start = startField.getConvertedInput();
                Date end = endField.getConvertedInput();
                boolean full = fullCheckBox.getConvertedInput();

                if (start == null || end == null) {
                    if (start == null && !full) {
                        this.error(startField, "emptyError");
                    }
                    if (end == null && !full) {
                        this.error(endField, "emptyError");
                    }
                } else {
                    if (startField.getConvertedInput().after(endField.getConvertedInput())) {
                        this.error(startField, "periodError");
                        this.error(endField, "periodError");
                    }
                }
            }
        });
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
