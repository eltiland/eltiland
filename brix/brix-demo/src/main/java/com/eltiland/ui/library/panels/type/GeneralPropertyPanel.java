package com.eltiland.ui.library.panels.type;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.file.File;
import com.eltiland.model.library.*;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.button.EltiAjaxSubmitLink;
import com.eltiland.ui.common.components.button.EltiSpinAjaxDecorator;
import com.eltiland.ui.common.components.button.back.BackButton;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.library.components.relevance.RelevanceField;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * General property panel for editing/creating record.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class GeneralPropertyPanel<T extends LibraryRecord> extends BaseEltilandPanel<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GeneralPropertyPanel.class);

    @SpringBean
    private LibraryRecordManager libraryRecordManager;
    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;

    private final int CONTROL_WIDTH = 488;

    private Form form = new Form("form");

    private boolean createMode;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private ELTTextField nameField = new ELTTextField<String>(
            "nameField", new ResourceModel("name"), new Model<String>(), String.class, true) {
        @Override
        protected int getInitialWidth() {
            return CONTROL_WIDTH;
        }
    };

    private ELTTextArea descriptionField = new ELTTextArea(
            "descriptionField", new ResourceModel("description"), new Model<String>()) {
        @Override
        protected int getInitialWidth() {
            return CONTROL_WIDTH;
        }

        @Override
        protected int getInitialHeight() {
            return 80;
        }
    };

    private ELTTextArea keyWordField = new ELTTextArea(
            "keyWordField", new ResourceModel("keyWord"), new Model<String>()) {
        @Override
        protected int getInitialWidth() {
            return CONTROL_WIDTH;
        }

        @Override
        protected int getInitialHeight() {
            return 80;
        }
    };

    private WebMarkupContainer adminPart = new WebMarkupContainer("adminPart") {
        @Override
        public boolean isVisible() {
            return currentUserModel.getObject().isSuperUser();
        }
    };

    private RelevanceField relevanceField = new RelevanceField("relevanceField", new Model<>(0));

    /**
     * Ctor for creating new entity.
     *
     * @param id markup id.
     */
    public GeneralPropertyPanel(String id) {
        super(id);
        createMode = true;
        addComponents();
    }

    /**
     * Ctor for editing entity.
     *
     * @param id      markup id.
     * @param tiModel record entity model.
     */
    protected GeneralPropertyPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);
        createMode = false;
        addComponents();
        nameField.setModelObject(tiModel.getObject().getName());
        descriptionField.setModelObject(tiModel.getObject().getDescription());
        keyWordField.setModelObject(tiModel.getObject().getKeyWords());
        relevanceField.setModelObject(tiModel.getObject().getRelevance());
    }

    private void addComponents() {
        setOutputMarkupId(true);
        add(form);

        form.add(nameField.setOutputMarkupId(true));
        nameField.addMaxLengthValidator(256);
        form.add(descriptionField);
        descriptionField.addMaxLengthValidator(2048);
        form.add(keyWordField);
        keyWordField.addMaxLengthValidator(2048);

        form.add(adminPart);
        adminPart.add(relevanceField.setOutputMarkupPlaceholderTag(true));

        relevanceField.setVisible(false);

        WebMarkupContainer relevanceContainer = new WebMarkupContainer("relevanceLink");
        adminPart.add(relevanceContainer);

        relevanceContainer.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                relevanceField.setVisible(!relevanceField.isVisible());
                target.add(relevanceField);
            }
        });

        EltiAjaxSubmitLink createButton = new EltiAjaxSubmitLink("createButton") {
            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new EltiSpinAjaxDecorator(GeneralPropertyPanel.this);
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                try {
                    LibraryRecord record = getItemClass().newInstance();
                    if (record instanceof LibraryDocumentRecord || record instanceof LibraryPresentationRecord ||
                            record instanceof LibraryImageRecord || record instanceof LibraryArchiveRecord) {
                        File file = ((AbstractContentPropertyPanel) GeneralPropertyPanel.this).getContentFile();
                        if (file == null) {
                            ELTAlerts.renderErrorPopup(getString("errorEmptyFile"), target);
                            return;
                        } else {
                            record.setFileContent(file);
                        }
                    }

                    if (record instanceof LibraryVideoRecord) {
                        ((LibraryVideoRecord) record).setVideoLink(
                                ((VideoPropertyPanel) GeneralPropertyPanel.this).getLink());
                    }

                    record.setName((String) nameField.getModelObject());
                    record.setDescription(descriptionField.getModelObject());
                    record.setKeyWords(keyWordField.getModelObject());
                    record.setRelevance(relevanceField.getModelObject());

                    if (currentUserModel.getObject().isSuperUser()) {
                        record.setPublishedDate(DateUtils.getCurrentDate());
                    }

                    try {
                        libraryRecordManager.createRecord(record);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot create record", e);
                        throw new WicketRuntimeException(e);
                    }
                    onCreateRecord(target);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error("Cannot create record", e);
                    throw new WicketRuntimeException(e);
                }
            }

            @Override
            public boolean isVisible() {
                return createMode;
            }
        };

        EltiAjaxSubmitLink saveButton = new EltiAjaxSubmitLink("saveButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                LibraryRecord record = GeneralPropertyPanel.this.getModelObject();

                if (record instanceof LibraryDocumentRecord || record instanceof LibraryPresentationRecord) {
                    // Check if user has changed document
                    if (((DocumentPropertyPanel) GeneralPropertyPanel.this).getContentFile() != null) {
                        genericManager.initialize(record, record.getFileContent());
                        boolean published = record.getFileContent() == null;

                        // If already published or not published and file was changed - reload file of record.
                        if (published || (!published &&
                                !(record.getFileContent().getId().equals((
                                        (DocumentPropertyPanel) GeneralPropertyPanel.this).getContentFile().getId())))) {
                            File oldFile = record.getFileContent();
                            record.setFileContent(null);
                            try {
                                genericManager.update(record);
                                if (oldFile != null) {
                                    fileManager.deleteFile(oldFile);
                                }
                            } catch (FileException | ConstraintException e) {
                                LOGGER.error("Error while removing file", e);
                                throw new WicketRuntimeException(e);
                            }

                            record.setPublished(false);
                            record.setPublishing(false);
                            record.setPublishAttempts(0);
                            (record).setFileContent(
                                    (((DocumentPropertyPanel) GeneralPropertyPanel.this).getContentFile()));
                        }
                    }
                }

                File oldFile = null;
                if (record instanceof LibraryImageRecord || record instanceof LibraryArchiveRecord) {
                    genericManager.initialize(record, record.getFileContent());
                    if (!(record.getFileContent().getId().equals(
                            ((AbstractContentPropertyPanel) GeneralPropertyPanel.this).getContentFile().getId()))) {
                        oldFile = record.getFileContent();
                        record.setFileContent(
                                ((AbstractContentPropertyPanel) GeneralPropertyPanel.this).getContentFile());
                    }
                }

                record.setName((String) nameField.getModelObject());
                record.setDescription(descriptionField.getModelObject());
                record.setRelevance(relevanceField.getModelObject());
                record.setKeyWords(keyWordField.getModelObject());

                try {
                    libraryRecordManager.saveRecord(record);
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot create record", e);
                    throw new WicketRuntimeException(e);
                }

                if (oldFile != null) {
                    try {
                        fileManager.deleteFile(oldFile);
                    } catch (FileException e) {
                        LOGGER.error("Error while removing file", e);
                        throw new WicketRuntimeException(e);
                    }
                }

                onSaveRecord(target);
            }

            @Override
            public boolean isVisible() {
                return !createMode;
            }
        };

        form.add(createButton);
        form.add(saveButton);
        form.add(new BackButton("backButton"));
    }

    protected abstract Class<? extends LibraryRecord> getItemClass();

    protected void onCreateRecord(AjaxRequestTarget target) {

    }

    protected void onSaveRecord(AjaxRequestTarget target) {

    }
}
