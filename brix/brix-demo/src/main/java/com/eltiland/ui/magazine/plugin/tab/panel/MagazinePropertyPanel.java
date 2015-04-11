package com.eltiland.ui.magazine.plugin.tab.panel;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.magazine.MagazineManager;
import com.eltiland.exceptions.CountableException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.avatar.AvatarPreviewPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.form.FormRequired;
import com.eltiland.ui.common.components.pricefield.PriceField;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Panel for creating and editing magazine entity.
 *
 * @author Aleksey Plotnikov.
 */
public class MagazinePropertyPanel extends BaseEltilandPanel<Magazine>
        implements IDialogNewCallback<Magazine>, IDialogUpdateCallback<Magazine> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MagazinePropertyPanel.class);

    @SpringBean
    private MagazineManager magazineManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    private IDialogNewCallback.IDialogActionProcessor<Magazine> newCallback;
    private IDialogUpdateCallback.IDialogActionProcessor<Magazine> updateCallback;

    private boolean isEditMode = false;

    private Label headerLabel = new Label("header", new Model<String>());

    private ELTTextArea nameField = new ELTTextArea("name", new ResourceModel("name"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }

        @Override
        protected int getInitialHeight() {
            return 35;
        }
    };

    private ELTTextArea topicField = new ELTTextArea("topic", new ResourceModel("topic"), new Model<String>(), true) {
        @Override
        protected boolean isFillToWidth() {
            return true;
        }

        @Override
        protected int getInitialHeight() {
            return 35;
        }
    };

    private PriceField priceField = new PriceField("price", new ResourceModel("price"), new Model<BigDecimal>());

    private ELTUploadComponent contentFileField = new ELTUploadComponent("contentFile", 1) {
        @Override
        protected boolean showMaximumWarning() {
            return false;
        }
    };

    private AvatarPreviewPanel coverField = new AvatarPreviewPanel("cover", UrlUtils.StandardIcons.ICONS_IMAGE) {
        @Override
        protected String getImageHeader() {
            return getString("cover");
        }

        @Override
        public String getAspectRatio() {
            return eltilandProps.getProperty("avatar.a4.aspect.ratio");
        }

        @Override
        public String getWidth() {
            return eltilandProps.getProperty("avatar.a4.width");
        }

        @Override
        public String getHeight() {
            return eltilandProps.getProperty("avatar.a4.height");
        }
    };

    private EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            if (contentFileField.getUploadedFiles().isEmpty()) {
                ELTAlerts.renderErrorPopup(getString("errorNotLoadedContent"), target);
                return;
            }

            Magazine magazine = fillMagazineData(new Magazine());

            try {
                magazineManager.createMagazine(magazine);
            } catch (EltilandManagerException | CountableException | FileException e) {
                LOGGER.error("Cannot create magazine entity", e);
                throw new WicketRuntimeException("Cannot create magazine entity", e);
            }

            newCallback.process(new GenericDBModel<>(Magazine.class, magazine), target);
        }
    };

    private EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
        @Override
        protected void onSubmit(AjaxRequestTarget target, Form form) {
            if (contentFileField.getUploadedFiles().isEmpty()) {
                ELTAlerts.renderErrorPopup(getString("errorNotLoadedContent"), target);
                return;
            }

            Magazine magazine = fillMagazineData(MagazinePropertyPanel.this.getModelObject());

            try {
                magazineManager.updateMagazine(magazine);
            } catch (EltilandManagerException | FileException e) {
                LOGGER.error("Cannot update magazine entity", e);
                throw new WicketRuntimeException("Cannot update magazine entity", e);
            }

            updateCallback.process(new GenericDBModel<>(Magazine.class, magazine), target);
        }
    };

    public MagazinePropertyPanel(String id) {
        super(id, new GenericDBModel<>(Magazine.class));
        add(headerLabel);

        Form form = new Form("form");
        form.setMultiPart(true);
        add(form);
        form.add(nameField);
        form.add(topicField);
        form.add(priceField);
        form.add(coverField);
        coverField.setAvatarLabelText(getString("cover"));
        form.add(contentFileField);
        form.add(new FormRequired("required"));
        form.add(createButton);
        form.add(saveButton);
        priceField.setRequired(true);

        nameField.addMaxLengthValidator(256);
        topicField.addMaxLengthValidator(256);

        initCreateMode();
    }

    public void initCreateMode() {
        headerLabel.setDefaultModelObject(getString("createHeader"));
        createButton.setVisible(true);
        saveButton.setVisible(false);
        isEditMode = false;
    }

    public void initEditMode(Magazine magazine) {
        setModelObject(magazine);
        headerLabel.setDefaultModelObject(getString("editHeader"));
        createButton.setVisible(false);
        saveButton.setVisible(true);
        nameField.setModelObject(magazine.getName());
        topicField.setModelObject(magazine.getTopic());
        priceField.setValue(magazine.getPrice());

        if (magazine.getCover() != null) {
            File file = fileManager.getFileById(magazine.getCover().getId());
            coverField.initEditMode(new GenericDBModel<>(File.class, file));
        }

        File contentFile = fileManager.getFileById(magazine.getContent().getId());
        contentFileField.setUploadedFiles(new ArrayList<>(Arrays.asList(contentFile)));

        isEditMode = true;
    }

    @Override
    protected void onBeforeRender() {
        if (!isEditMode) {
            nameField.setModelObject(null);
            topicField.setModelObject(null);
            priceField.setValue(BigDecimal.valueOf(0));
            coverField.initCreateMode(UrlUtils.StandardIcons.ICONS_IMAGE);
            contentFileField.setUploadedFiles(new ArrayList<File>());
        }
        super.onBeforeRender();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTIP);
        response.renderCSSReference(ResourcesUtils.CSS_TOOLTUP_BOX);
    }

    @Override
    public void setNewCallback(IDialogNewCallback.IDialogActionProcessor<Magazine> callback) {
        newCallback = callback;
    }

    @Override
    public void setUpdateCallback(IDialogUpdateCallback.IDialogActionProcessor<Magazine> callback) {
        updateCallback = callback;
    }

    private Magazine fillMagazineData(Magazine magazine) {
        magazine.setName(nameField.getModelObject());
        magazine.setCover(coverField.getAvatarFile());
        magazine.setContent(contentFileField.getUploadedFiles().get(0));
        magazine.setPrice(priceField.getPriceValue());
        magazine.setTopic(topicField.getModelObject());
        return magazine;
    }
}
