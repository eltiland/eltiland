package com.eltiland.ui.library.view.kind.list;

import com.eltiland.bl.FileManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.drive.GoogleDriveManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.library.LibraryCollectionManager;
import com.eltiland.bl.library.LibraryRecordManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.GoogleDriveException;
import com.eltiland.model.file.File;
import com.eltiland.model.library.*;
import com.eltiland.model.tags.ITagable;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.components.selector.ELTSelectDialog;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.library.LibraryEditRecordPage;
import com.eltiland.ui.library.panels.view.RecordViewPage;
import com.eltiland.ui.tags.components.list.TagListPanel;
import com.eltiland.ui.tags.components.selector.TagSelectPanel;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.MimeType;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Record panel for list view.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class RecordListPanel extends AbstractItemPanel<LibraryRecord> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RecordListPanel.class);

    public static final String CSS = "static/css/library/record.css";

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileManager fileManager;
    @SpringBean
    private LibraryCollectionManager libraryCollectionManager;
    @SpringBean
    private LibraryRecordManager libraryRecordManager;
    @SpringBean
    private GoogleDriveManager googleDriveManager;
    @SpringBean
    private FileUtility fileUtility;

    private TagListPanel tagListPanel = new TagListPanel("tagPanel", RecordListPanel.this.getModel());

    private Long id;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };


    private IModel<LibraryRecord> recordModel = new LoadableDetachableModel<LibraryRecord>() {
        @Override
        protected LibraryRecord load() {
            return genericManager.getObject(LibraryRecord.class, id);
        }
    };

    private IModel<List<LibraryCollection>> collectionsModel = new LoadableDetachableModel<List<LibraryCollection>>() {
        @Override
        protected List<LibraryCollection> load() {
            genericManager.initialize(recordModel.getObject(), recordModel.getObject().getCollections());
            return new ArrayList<>(recordModel.getObject().getCollections());
        }
    };

    private WebMarkupContainer collectionContainer = new WebMarkupContainer("collectionContainer") {
        @Override
        public boolean isVisible() {
            return !(collectionsModel.getObject().isEmpty());
        }
    };


    private Dialog<TagSelectPanel> tagSelectPanelDialog = new Dialog<TagSelectPanel>("tagSelectDialog", 300) {
        @Override
        public TagSelectPanel createDialogPanel(String id) {
            return new TagSelectPanel(id) {
                @Override
                protected Class<? extends ITagable> getEntityClass(ITagable entity) {
                    return LibraryRecord.class;
                }
            };
        }

        @Override
        public void registerCallback(TagSelectPanel panel) {
            super.registerCallback(panel);
            panel.setConfirmCallback(new IDialogConfirmCallback.IDialogActionProcessor() {
                @Override
                public void process(AjaxRequestTarget target) {
                    close(target);
                    ELTAlerts.renderOKPopup(getString("tagMessageSaved"), target);
                    target.add(tagListPanel);
                    onChange(target);
                }
            });
        }
    };

    private boolean isDocument, isImageOrArchive, isPublished;

    private ELTSelectDialog<LibraryCollection> collectionSelectDialog = new ELTSelectDialog<LibraryCollection>(
            "collectionSelector", 900) {
        @Override
        protected int getMaxRows() {
            return 10;
        }

        @Override
        protected String getHeader() {
            return getString("collectionSelect");
        }

        @Override
        protected void onSelect(AjaxRequestTarget target, List<Long> selectedIds) {
            for (LibraryCollection collection : collectionsModel.getObject()) {
                if (!(selectedIds.contains(collection.getId()))) {
                    genericManager.initialize(collection, collection.getRecords());
                    collection.getRecords().remove(getModelObject());
                    try {
                        genericManager.update(collection);
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot remove collection", e);
                        throw new WicketRuntimeException(e);
                    }
                }
            }

            recordModel.detach();
            collectionsModel.detach();
            for (Long id : selectedIds) {
                LibraryCollection collection = genericManager.getObject(LibraryCollection.class, id);
                genericManager.initialize(collection, collection.getRecords());
                if (!(collectionsModel.getObject().contains(collection))) {
                    try {
                        libraryCollectionManager.addRecordToCollection(getModelObject(), collection);
                    } catch (EltilandManagerException e) {
                        LOGGER.error("Cannot add record to collection", e);
                        throw new WicketRuntimeException(e);
                    }
                }
            }

            close(target);
            ELTAlerts.renderOKPopup(getString("collectionMessageSaved"), target);
            recordModel.detach();
            collectionsModel.detach();
            target.add(collectionContainer);
            onChange(target);
        }

        @Override
        protected List<IColumn<LibraryCollection>> getColumns() {
            List<IColumn<LibraryCollection>> columns = new ArrayList<>();
            columns.add(new PropertyColumn<LibraryCollection>(new ResourceModel("nameColumn"), "name", "name"));
            return columns;
        }

        @Override
        protected Iterator getIterator(int first, int count) {
            return libraryCollectionManager.getLibraryCollectionList(
                    first, count, getSort().getProperty(),
                    getSort().isAscending(), getSearchString(), null).iterator();
        }

        @Override
        protected int getSize() {
            return libraryCollectionManager.getLibraryCollectionCount(getSearchString(), null, false);
        }

        @Override
        protected String getSearchPlaceholder() {
            return getString("collectionSearch");
        }
    };

    /**
     * Panel ctor.
     *
     * @param id                  markup id.
     * @param libraryRecordIModel item model.
     */
    public RecordListPanel(String id, IModel<LibraryRecord> libraryRecordIModel) {
        super(id, libraryRecordIModel);
        this.id = getModelObject().getId();

        Class<? extends LibraryRecord> clazz = getModelObject().getClass();
        isDocument = clazz.equals(LibraryDocumentRecord.class) || clazz.equals(LibraryPresentationRecord.class);
        isImageOrArchive = clazz.equals(LibraryImageRecord.class) || clazz.equals(LibraryArchiveRecord.class);
        isPublished = getModelObject().isPublished();

        genericManager.initialize(getModelObject(), getModelObject().getPublisher());
        add(new Label("author", getModelObject().getPublisher().getName()));
        add(new Label("date", DateUtils.formatRussianDate(getModelObject().getPublishedDate())));
        add(tagListPanel.setOutputMarkupId(true));
        add(collectionContainer.setOutputMarkupPlaceholderTag(true));
        collectionContainer.add(new ListView<LibraryCollection>("collectionList", collectionsModel) {
            @Override
            protected void populateItem(ListItem<LibraryCollection> item) {
                item.add(new Label("collectionName", item.getModelObject().getName()));
            }
        });
        add(tagSelectPanelDialog);
        add(collectionSelectDialog);
    }

    @Override
    protected WebComponent getIcon(String markupId) {

        final Class<? extends LibraryRecord> clazz = getModelObject().getClass();
        if (clazz.equals(LibraryImageRecord.class)) {
            final IModel<File> fileIModel = new LoadableDetachableModel<File>() {
                @Override
                protected File load() {
                    genericManager.initialize(RecordListPanel.this.getModelObject(),
                            RecordListPanel.this.getModelObject().getFileContent());
                    return fileManager.getFileById(RecordListPanel.this.getModelObject().getFileContent().getId());
                }
            };

            IResourceStream resourceStream = new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    return new ByteArrayInputStream(fileIModel.getObject().getPreviewBody().getBody());
                }

                @Override
                public void close() throws IOException {
                }
            };
            try {
                IResource resource = new ByteArrayResource(fileIModel.getObject().getType(),
                        IOUtils.toByteArray(resourceStream.getInputStream()), fileIModel.getObject().getName());
                return new Image("icon", resource);
            } catch (ResourceStreamNotFoundException | IOException e) {
                LOGGER.error("Cannot show record", e);
                throw new WicketRuntimeException(e);
            }
        } else {
            String url = null;
            if (clazz.equals(LibraryDocumentRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_DOCUMENT.getPath();
            } else if (clazz.equals(LibraryPresentationRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_PRESENTATION.getPath();
            } else if (clazz.equals(LibraryArchiveRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_ARCHIVE.getPath();
            } else if (clazz.equals(LibraryVideoRecord.class)) {
                url = UrlUtils.StandardIcons.ICON_ITEM_VIDEO.getPath();
            }

            return new StaticImage("icon", url);
        }
    }

    @Override
    protected String getIconLabel() {
        return getString(getModelObject().getClass().getSimpleName() + ".type");
    }

    @Override
    protected String getEntityName(IModel<LibraryRecord> itemModel) {
        return itemModel.getObject().getName();
    }

    @Override
    protected String getEntityDescription(IModel<LibraryRecord> itemModel) {
        return itemModel.getObject().getDescription();
    }

    @Override
    protected List<ButtonAction> getActionList() {
        return new ArrayList<>(Arrays.asList(
                ButtonAction.EDIT,
                ButtonAction.PREVIEW,
                ButtonAction.TAG,
                ButtonAction.COLLECTION,
                ButtonAction.REMOVE,
                ButtonAction.DOWNLOAD));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        switch (action) {
            case EDIT:
                return new ResourceModel("change");
            case PREVIEW:
                return new ResourceModel("preview");
            case TAG:
                return new ResourceModel("tag");
            case COLLECTION:
                return new ResourceModel("collection");
            case REMOVE:
                return new ResourceModel("delete");
            case DOWNLOAD:
                return new ResourceModel("download");
            default:
                return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        final Class<? extends LibraryRecord> clazz = getModelObject().getClass();
        switch (action) {
            case EDIT:
                return currentUserModel.getObject() != null &&
                        (currentUserModel.getObject().isSuperUser() ||
                                currentUserModel.getObject().getId().equals(getModelObject().getPublisher().getId()));
            case PREVIEW:
                if (clazz.equals(LibraryDocumentRecord.class) || clazz.equals(LibraryPresentationRecord.class)) {
                    return getModelObject().isPublished();
                } else {
                    return clazz.equals(LibraryImageRecord.class) || clazz.equals(LibraryVideoRecord.class);
                }
            case TAG:
            case COLLECTION:
                return currentUserModel.getObject() != null && currentUserModel.getObject().isSuperUser();
            case REMOVE:
                return currentUserModel.getObject() != null && currentUserModel.getObject().isSuperUser() &&
                        (!clazz.equals(LibraryDocumentRecord.class) || !(getModelObject().isPublishing()));
            case DOWNLOAD:
                return !(clazz.equals(LibraryVideoRecord.class));
            default:
                return false;
        }
    }

    @Override
    protected void onClick(ButtonAction action, AjaxRequestTarget target) {
        switch (action) {
            case EDIT:
                throw new RestartResponseException(LibraryEditRecordPage.class,
                        new PageParameters().add(LibraryEditRecordPage.PARAM_ID, getModelObject().getId()));
            case PREVIEW:
                throw new RestartResponseException(RecordViewPage.class,
                        new PageParameters().add(RecordViewPage.PARAM_ID, getModelObject().getId()));
            case TAG:
                tagSelectPanelDialog.getDialogPanel().initPanel(getModel());
                tagSelectPanelDialog.show(target);
                break;
            case COLLECTION:
                List<Long> selectedIds = new ArrayList<>();
                genericManager.initialize(getModelObject(), getModelObject().getCollections());
                for (LibraryCollection collection : getModelObject().getCollections()) {
                    selectedIds.add(collection.getId());
                }
                collectionSelectDialog.getDialogPanel().setSelectedIds(selectedIds);
                collectionSelectDialog.show(target);
                break;
            case REMOVE:
                try {
                    libraryRecordManager.deleteRecord(getModelObject());
                } catch (EltilandManagerException e) {
                    LOGGER.error("Cannot remove record", e);
                    throw new WicketRuntimeException(e);
                }
                ELTAlerts.renderOKPopup(getString("recordDeleted"), target);
                onChangeList(target);
                onChange(target);
                break;
            case DOWNLOAD:
                ajaxDownload.initiate(target);
            default:
                break;
        }
    }

    final AjaxDownload ajaxDownload = new AjaxDownload() {
        @Override
        protected String getFileName() {
            if (isDocument && isPublished) {
                genericManager.initialize(getModelObject(), getModelObject().getContent());

                return getModelObject().getName() +
                        MimeType.getExtension((getModelObject()).getContent().getMimeType());
            } else if (isImageOrArchive || (isDocument && !isPublished)) {
                genericManager.initialize(getModelObject(), getModelObject().getFileContent());
                return getModelObject().getFileContent().getName();
            } else {
                return "";
            }
        }

        @Override
        protected IResourceStream getResourceStream() {
            return new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    if (isDocument && isPublished) {
                        genericManager.initialize(getModelObject(), getModelObject().getContent());
                        try {
                            return googleDriveManager.downloadFile(getModelObject().getContent());
                        } catch (GoogleDriveException e) {
                            LOGGER.error("Cannot download record", e);
                            throw new WicketRuntimeException(e);
                        }
                    } else if (isImageOrArchive || (isDocument && !isPublished)) {
                        genericManager.initialize(getModelObject(), getModelObject().getFileContent());
                        genericManager.initialize(getModelObject().getFileContent(),
                                getModelObject().getFileContent().getBody());
                        IResourceStream resourceStream = fileUtility.getFileResource(
                                getModelObject().getFileContent().getBody().getHash());
                        return resourceStream.getInputStream();
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

    @Override
    protected AbstractAjaxBehavior getAdditionalBehavior(ButtonAction action) {
        if (action.equals(ButtonAction.DOWNLOAD)) {
            return ajaxDownload;
        } else {
            return super.getAdditionalBehavior(action);
        }
    }

    @Override
    protected boolean hasConfirmation(ButtonAction action) {
        return action.equals(ButtonAction.DOWNLOAD) || super.hasConfirmation(action);
    }

    @Override
    protected IModel<String> getConfirmationText(ButtonAction action) {
        if (action.equals(ButtonAction.DOWNLOAD)) {
            return new ResourceModel("copyrightAgreementBody");
        } else {
            return super.getConfirmationText(action);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    protected abstract void onChange(AjaxRequestTarget target);

    protected abstract void onChangeList(AjaxRequestTarget target);
}
