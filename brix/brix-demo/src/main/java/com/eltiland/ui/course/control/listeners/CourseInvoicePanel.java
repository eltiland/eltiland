package com.eltiland.ui.course.control.listeners;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.user.UserFileAccessManager;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.FileException;
import com.eltiland.exceptions.UserException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.course2.listeners.ListenerType;
import com.eltiland.model.file.File;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.file.UserFileAccess;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.EltiAjaxLink;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleUpdateCallback;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.file.ELTFilePanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.components.textfield.ELTTextField;
import com.eltiland.ui.common.model.GenericDBListModel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.control.listeners.panel.GeneralDataPanel;
import com.eltiland.ui.course.control.listeners.panel.NamePanel;
import com.eltiland.ui.course.control.listeners.panel.OrganizationPanel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Course invoice management panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseInvoicePanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private UserFileManager userFileManager;
    @SpringBean
    private UserFileAccessManager userFileAccessManager;

    private ELTTable<ELTCourseListener> grid;

    private Dialog<OfferPanel> offerPanelDialog = new Dialog<OfferPanel>("offerDialog", 340) {
        @Override
        public OfferPanel createDialogPanel(String id) {
            return new OfferPanel(id);
        }

        @Override
        public void registerCallback(OfferPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<ELTCourseListener>() {
                @Override
                public void process(IModel<ELTCourseListener> model, AjaxRequestTarget target) {
                    close(target);
                    try {
                        courseListenerManager.update(model.getObject());
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            });
        }
    };

    private Dialog<FilePanel> fileDialog = new Dialog<FilePanel>("fileDialog", 455) {
        @Override
        public FilePanel createDialogPanel(String id) {
            return new FilePanel(id);
        }

        @Override
        public void registerCallback(FilePanel panel) {
            super.registerCallback(panel);
            panel.setSimpleUpdateCallback(new IDialogSimpleUpdateCallback.IDialogActionProcessor<List<File>>() {
                @Override
                public void process(IModel<List<File>> model, AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    /**
     * Panel ctor.
     *
     * @param id              markup id.
     * @param eltCourseIModel course model.
     */
    public CourseInvoicePanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        add(offerPanelDialog);
        add(fileDialog);

        grid = new ELTTable<ELTCourseListener>("grid", 30) {
            @Override
            protected List<IColumn<ELTCourseListener>> getColumns() {
                List<IColumn<ELTCourseListener>> columns = new ArrayList<>();
                columns.add(new AbstractColumn<ELTCourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL, "listener.name") {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> components,
                                             String s, IModel<ELTCourseListener> listenerIModel) {
                        genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getListener());
                        components.add(new NamePanel(s, new Model<>(listenerIModel.getObject().getListener())));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("contactColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new GeneralDataPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("organizationColumn")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new OrganizationPanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("typeColumn"), "kind") {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new TypePanel(componentId, rowModel));
                    }
                });
                columns.add(new AbstractColumn<ELTCourseListener>(new ResourceModel("statusColumn"), "status") {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTCourseListener>> cellItem,
                                             String componentId, IModel<ELTCourseListener> rowModel) {
                        cellItem.add(new Label(componentId, getString(rowModel.getObject().getStatus().toString())));
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseListenerManager.getList(getModelObject(), getSearchString(), first, count,
                        getSort().getProperty(), getSort().isAscending(), false, true).iterator();
            }

            @Override
            protected int getSize() {
                return courseListenerManager.getCount(getModelObject(), getSearchString(), false, true);
            }

            @Override
            protected boolean isControlling() {
                return true;
            }

            @Override
            protected List<GridAction> getControlActions() {
                return new ArrayList<>(Arrays.asList(
                        GridAction.ON, GridAction.OFF, GridAction.LOCK, GridAction.UNLOCK));
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTCourseListener> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.APPLY, GridAction.EDIT,
                        GridAction.REMOVE, GridAction.DOWNLOAD, GridAction.UPLOAD,
                        GridAction.PAY, GridAction.FULL_APPLY));
            }

            @Override
            protected boolean isControlActionVisible(GridAction action) {
                boolean isTraining = getModelObject() instanceof TrainingCourse;
                boolean isOpen = isTraining && ((TrainingCourse) getModelObject()).isOpen();
                boolean isConfirm = getModelObject().isNeedConfirm();

                switch (action) {
                    case ON:
                        return !isTraining && isConfirm;
                    case OFF:
                        return !isTraining && !isConfirm;
                    case LOCK:
                        return isTraining && isOpen;
                    case UNLOCK:
                        return isTraining && !isOpen;
                    default:
                        return false;

                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                switch (action) {
                    case ON:
                        return getString("on.tooltip");
                    case OFF:
                        return getString("off.tooltip");
                    case LOCK:
                        return getString("lock.tooltip");
                    case UNLOCK:
                        return getString("unlock.tooltip");
                    case REMOVE:
                        return getString("remove.tooltip");
                    case EDIT:
                        return getString("edit.tooltip");
                    case APPLY:
                        return getString("apply.tooltip");
                    case DOWNLOAD:
                        return getString("download.tooltip");
                    case UPLOAD:
                        return getString("upload.tooltip");
                    case PAY:
                        return getString("pay.tooltip");
                    case FULL_APPLY:
                        return getString("listener.tooltip");
                    default:
                        return StringUtils.EMPTY;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.REMOVE);
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTCourseListener> rowModel) {
                PaidStatus status = rowModel.getObject().getStatus();
                ListenerType type = rowModel.getObject().getType();

                boolean train = getModelObject() instanceof TrainingCourse;
                switch (action) {
                    case REMOVE:
                        return !(status.equals(PaidStatus.CONFIRMED)) && !(status.equals(PaidStatus.PAYS));
                    case EDIT:
                        return getModelObject() instanceof TrainingCourse && type.equals(ListenerType.MOSCOW);
                    case APPLY:
                        return status.equals(PaidStatus.NEW);
                    case DOWNLOAD:
                        return train && !(status.equals(PaidStatus.NEW));
                    case UPLOAD:
                        return train && !(status.equals(PaidStatus.NEW));
                    case PAY:
                        return train && status.equals(PaidStatus.APPROVED);
                    case FULL_APPLY:
                        return train && !(status.equals(PaidStatus.NEW));
                    default:
                        return false;
                }
            }

            @Override
            protected boolean isDownload(GridAction action) {
                return false;
            }

            @Override
            protected void onClick(IModel<ELTCourseListener> rowModel, GridAction action, AjaxRequestTarget target) {
                ELTCourse course = getModelObject();
                switch (action) {
                    case ON:
                        course.setNeedConfirm(false);
                        break;
                    case OFF:
                        course.setNeedConfirm(true);
                        break;
                    case LOCK:
                        ((TrainingCourse) course).setOpen(false);
                        break;
                    case UNLOCK:
                        ((TrainingCourse) course).setOpen(true);
                        break;
                    case REMOVE:
                        try {
                            if (course instanceof TrainingCourse) {
                                //remove rights to access to the physical or legal docs.
                                TrainingCourse tCourse = courseManager.fetchDocuments(getModelObject().getId());
                                File file = rowModel.getObject().getType().equals(ListenerType.PHYSICAL) ?
                                        tCourse.getPhysicalDoc() : tCourse.getLegalDoc();
                                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getListener());
                                UserFile userFile = userFileManager.getByAuthorAndFile(tCourse.getAuthor(), file);
                                if (userFile != null) {
                                    UserFileAccess access = userFileAccessManager.getAccessInformation(
                                            rowModel.getObject().getListener(), userFile);
                                    if (access != null) {
                                        try {
                                            userFileAccessManager.delete(access);
                                        } catch (UserException e) {
                                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                        }
                                    }
                                    List<UserFileAccess> accessList =
                                            userFileAccessManager.getAccessInformation(userFile);
                                    if (accessList.isEmpty()) {
                                        try {
                                            userFileManager.delete(userFile);
                                        } catch (UserException e) {
                                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                        }
                                    }
                                }
                            }

                            emailMessageManager.sendTCUserDeclined(rowModel.getObject());
                            courseListenerManager.delete(rowModel.getObject());
                            target.add(grid);
                        } catch (EmailException | CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case EDIT:
                        offerPanelDialog.getDialogPanel().initData(rowModel.getObject());
                        offerPanelDialog.show(target);
                        break;
                    case APPLY:
                        if (course instanceof TrainingCourse) {
                            rowModel.getObject().setStatus(PaidStatus.APPROVED);

                            // Adding rights to the physical or legal documents.
                            TrainingCourse tCourse = courseManager.fetchDocuments(getModelObject().getId());
                            if ((tCourse.getPhysicalDoc() == null) || (tCourse.getLegalDoc() == null)) {
                                ELTAlerts.renderErrorPopup(getString("error.no.documents"), target);
                                break;
                            }

                            genericManager.initialize(tCourse.getAuthor(), tCourse.getAuthor().getUserFiles());

                            File file = rowModel.getObject().getType().equals(ListenerType.PHYSICAL) ?
                                    tCourse.getPhysicalDoc() : tCourse.getLegalDoc();
                            UserFile userFile = null;
                            for (UserFile tFile : tCourse.getAuthor().getUserFiles()) {
                                genericManager.initialize(tFile, tFile.getFile());
                                if (tFile.getFile().equals(file)) {
                                    userFile = tFile;
                                    break;
                                }
                            }
                            if (userFile == null) {
                                userFile = new UserFile();
                                userFile.setOwner(tCourse.getAuthor());
                                userFile.getCourses().add(course);
                                userFile.setUploadDate(DateUtils.getCurrentDate());
                                userFile.setFile(file);

                                try {
                                    userFileManager.create(userFile);
                                } catch (UserException e) {
                                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                                }
                            }
                            UserFileAccess fileAccess = new UserFileAccess();
                            fileAccess.setFile(userFile);
                            genericManager.initialize(rowModel.getObject(), rowModel.getObject().getListener());
                            fileAccess.setClient(rowModel.getObject().getListener());
                            try {
                                userFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        } else {
                            rowModel.getObject().setStatus(PaidStatus.PAYS);
                        }
                        try {
                            emailMessageManager.sendTCUserAccepted(rowModel.getObject());
                            courseListenerManager.update(rowModel.getObject());
                            target.add(grid);
                        } catch (EmailException | CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        break;
                    case DOWNLOAD:
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getListener());
                        List<UserFile> files = userFileManager.getListenerFiles(
                                rowModel.getObject().getListener(), course);
                        if (files.isEmpty()) {
                            ELTAlerts.renderErrorPopup(getString("error.no.files"), target);
                        } else {
                            List<File> tFiles = new ArrayList<>();
                            for (UserFile file : files) {
                                genericManager.initialize(file, file.getFile());
                                tFiles.add(file.getFile());
                            }
                            fileDialog.getDialogPanel().initMode(false);
                            fileDialog.getDialogPanel().initData(tFiles, rowModel);
                            fileDialog.show(target);
                        }
                        break;
                    case UPLOAD:
                        genericManager.initialize(course, course.getAuthor());
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getListener());

                        List<UserFile> authorFiles = userFileManager.getFilesForListener(
                                course.getAuthor(), rowModel.getObject().getListener());
                        List<File> tFiles = new ArrayList<>();
                        for (UserFile file : authorFiles) {
                            genericManager.initialize(file, file.getFile());
                            tFiles.add(file.getFile());
                        }
                        fileDialog.getDialogPanel().initMode(true);
                        fileDialog.getDialogPanel().initData(tFiles, rowModel);
                        fileDialog.show(target);
                        break;
                    case FULL_APPLY:
                        try {
                            emailMessageManager.sendTCUserPaid(rowModel.getObject());
                        } catch (EmailException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        genericManager.initialize(rowModel.getObject(), rowModel.getObject().getListeners());
                        for (ELTCourseListener listener : rowModel.getObject().getListeners()) {
                            listener.setStatus(PaidStatus.CONFIRMED);
                            try {
                                courseListenerManager.update(listener);
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                        rowModel.getObject().setStatus(PaidStatus.CONFIRMED);
                        try {
                            courseListenerManager.update(rowModel.getObject());
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        ELTAlerts.renderOKPopup(getString("message.listener"), target);
                        target.add(grid);
                        break;
                    case PAY:
                        String requisites = ((TrainingCourse) course).getRequisites();
                        if (requisites == null || requisites.isEmpty()) {
                            ELTAlerts.renderErrorPopup(getString("error.no.requisites"), target);
                            break;
                        }
                        rowModel.getObject().setStatus(PaidStatus.PAYS);
                        rowModel.getObject().setRequisites(requisites);
                        try {
                            courseListenerManager.update(rowModel.getObject());
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        try {
                            emailMessageManager.sendTCUserPayAccepted(rowModel.getObject());
                        } catch (EmailException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                        target.add(grid);
                        break;
                }
                if (action.equals(GridAction.ON) || action.equals(GridAction.OFF) ||
                        action.equals(GridAction.LOCK) || action.equals(GridAction.UNLOCK)) {
                    try {
                        courseManager.update(course);
                        target.add(grid);
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }
            }
        };

        add(grid);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }

    private class TypePanel extends BaseEltilandPanel<ELTCourseListener> {
        private Dialog<ListenersListPanel> listenersListPanelDialog =
                new Dialog<ListenersListPanel>("listenersListDialog", 500) {
                    @Override
                    public ListenersListPanel createDialogPanel(String id) {
                        return new ListenersListPanel(id);
                    }
                };

        public TypePanel(String id, IModel<ELTCourseListener> courseListenerIModel) {
            super(id, courseListenerIModel);
            add(new Label("type", CourseInvoicePanel.this.getString(getModelObject().getType().toString())));

            genericManager.initialize(getModelObject(), getModelObject().getListeners());
            final boolean hasListeners = !getModelObject().getListeners().isEmpty();
            final boolean isLegal = !(getModelObject().getType().equals(ListenerType.PHYSICAL));
            add(new WebMarkupContainer("noListeners") {
                @Override
                public boolean isVisible() {
                    return !hasListeners && isLegal;
                }
            });
            add(new EltiAjaxLink("showUsersLink") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    listenersListPanelDialog.getDialogPanel().initData(TypePanel.this.getModel());
                    listenersListPanelDialog.show(target);
                }

                @Override
                public boolean isVisible() {
                    return hasListeners && isLegal;
                }
            });
            add(listenersListPanelDialog);
        }
    }

    private class ListenersListPanel extends ELTDialogPanel {

        private IModel<ELTCourseListener> courseListenerModel = new GenericDBModel<>(ELTCourseListener.class);

        public ListenersListPanel(String id) {
            super(id);
            form.add(new ELTTable<ELTCourseListener>("grid", 15) {
                @Override
                protected List<IColumn<ELTCourseListener>> getColumns() {
                    List<IColumn<ELTCourseListener>> columns = new ArrayList<>();
                    columns.add(new PropertyColumn<ELTCourseListener>(
                            new Model<>(CourseInvoicePanel.this.getString("nameColumn")),
                            "listener.name", "listener.name"));
                    columns.add(new AbstractColumn<ELTCourseListener>(ReadonlyObjects.EMPTY_DISPLAY_MODEL) {
                        @Override
                        public void populateItem(Item<ICellPopulator<ELTCourseListener>> components,
                                                 String s, IModel<ELTCourseListener> userIModel) {
                            genericManager.initialize(userIModel.getObject(), userIModel.getObject().getListener());
                            Label label = new Label(s, new Model<String>());
                            if (userIModel.getObject().getListener().getConfirmationDate() == null) {
                                label.setDefaultModelObject(CourseInvoicePanel.this.getString("no_activated"));
                                label.add(new AttributeModifier("style", "color:red"));
                            } else {
                                label.setDefaultModelObject(CourseInvoicePanel.this.getString("activated"));
                                label.add(new AttributeModifier("style", "color:green"));
                            }
                            components.add(label);
                        }
                    });
                    return columns;
                }

                @Override
                protected Iterator getIterator(int first, int count) {
                    genericManager.initialize(courseListenerModel.getObject(),
                            courseListenerModel.getObject().getListeners());
                    return courseListenerModel.getObject().getListeners().iterator();
                }

                @Override
                protected int getSize() {
                    genericManager.initialize(courseListenerModel.getObject(),
                            courseListenerModel.getObject().getListeners());
                    return courseListenerModel.getObject().getListeners().size();
                }

                @Override
                protected void onClick(IModel<ELTCourseListener> rowModel,
                                       GridAction action, AjaxRequestTarget target) {
                }
            });
        }

        public void initData(IModel<ELTCourseListener> listenerIModel) {
            courseListenerModel = listenerIModel;
        }

        @Override
        protected String getHeader() {
            return CourseInvoicePanel.this.getString("select.user.header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>();
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        }

        @Override
        public String getVariation() {
            return "styled";
        }
    }

    private class OfferPanel extends ELTDialogPanel implements IDialogUpdateCallback<ELTCourseListener> {

        private IDialogActionProcessor<ELTCourseListener> callback;

        private IModel<ELTCourseListener> listenerIModel = new GenericDBModel<>(ELTCourseListener.class);

        private ELTTextField<String> offerField = new ELTTextField<>(
                "offer", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>(), String.class);

        public OfferPanel(String id) {
            super(id);
            form.add(offerField);
        }

        public void initData(ELTCourseListener data) {
            listenerIModel.setObject(data);
            offerField.setModelObject(data.getOffer());
        }

        @Override
        protected String getHeader() {
            return CourseInvoicePanel.this.getString("offer.header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                listenerIModel.getObject().setOffer(offerField.getModelObject());
                callback.process(listenerIModel, target);
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<ELTCourseListener> callback) {
            this.callback = callback;
        }

        @Override
        public String getVariation() {
            return "styled";
        }
    }

    private class FilePanel extends ELTDialogPanel implements IDialogSimpleUpdateCallback<List<File>> {

        private boolean isUpload;

        private IModel<ELTCourseListener> listenerModel = new GenericDBModel<>(ELTCourseListener.class);

        private IDialogActionProcessor<List<File>> callback;

        private ELTFilePanel filePanel = new ELTFilePanel("files") {
            @Override
            protected boolean canBeDeleted() {
                return isUpload;
            }

            @Override
            protected boolean canBeUploaded() {
                return isUpload;
            }

            @Override
            protected int getMaxFiles() {
                return 5;
            }

            @Override
            protected void onDeleteActions(AjaxRequestTarget target, File file) {
                super.onDeleteActions(target, file);
                genericManager.initialize(CourseInvoicePanel.this.getModelObject(),
                        CourseInvoicePanel.this.getModelObject().getAuthor());
                UserFile userFile = userFileManager.getByAuthorAndFile(
                        CourseInvoicePanel.this.getModelObject().getAuthor(), file);
                UserFileAccess fileAccess = userFileAccessManager.getAccessInformation(
                        listenerModel.getObject().getListener(), userFile);
                try {
                    userFileAccessManager.delete(fileAccess);
                    userFileManager.delete(userFile);
                } catch (UserException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        };

        public FilePanel(String id) {
            super(id);
            form.add(filePanel);
            form.setMultiPart(true);
        }

        public void initMode(boolean mode) {
            isUpload = mode;
        }

        public void initData(List<File> files, IModel<ELTCourseListener> listenerModel) {
            filePanel.setFiles(files);
            this.listenerModel = listenerModel;
        }

        @Override
        protected String getHeader() {
            return CourseInvoicePanel.this.getString(isUpload ? "files.author.header" : "files.user.header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected boolean actionSelector(EVENT event) {
            return event.equals(EVENT.Save) && isUpload;
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            if (event.equals(EVENT.Save)) {
                try {
                    genericManager.initialize(listenerModel.getObject(), listenerModel.getObject().getListener());
                    genericManager.initialize(CourseInvoicePanel.this.getModelObject(),
                            CourseInvoicePanel.this.getModelObject().getAuthor());
                    List<UserFile> userFiles = userFileManager.getFilesForListener(
                            CourseInvoicePanel.this.getModelObject().getAuthor(),
                            listenerModel.getObject().getListener());
                    List<File> files = filePanel.getFiles(true);
                    for (UserFile userFile : userFiles) {
                        genericManager.initialize(userFile, userFile.getFile());
                        if (!(files.contains(userFile.getFile()))) {
                            try {
                                userFileManager.delete(userFile);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    }
                    for (File file : files) {
                        boolean save = true;
                        for (UserFile userFile : userFiles) {
                            genericManager.initialize(userFile, userFile.getFile());
                            if (file.getId().equals(userFile.getFile().getId())) {
                                save = false;
                                break;
                            }
                        }
                        if (save) {
                            genericManager.initialize(CourseInvoicePanel.this.getModelObject(),
                                    CourseInvoicePanel.this.getModelObject().getAuthor());

                            UserFile userFile = new UserFile();
                            userFile.setFile(file);
                            userFile.setOwner(CourseInvoicePanel.this.getModelObject().getAuthor());
                            userFile.setUploadDate(DateUtils.getCurrentDate());

                            try {
                                userFileManager.create(userFile);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }

                            UserFileAccess fileAccess = new UserFileAccess();
                            fileAccess.setClient(listenerModel.getObject().getListener());
                            fileAccess.setFile(userFile);

                            try {
                                userFileAccessManager.create(fileAccess);
                            } catch (UserException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }
                        }
                    }

                    callback.process(new GenericDBListModel<>(File.class, filePanel.getFiles(true)), target);
                } catch (FileException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                }
            }
        }

        @Override
        public String getVariation() {
            return "styled";
        }

        @Override
        public void setSimpleUpdateCallback(IDialogActionProcessor<List<File>> callback) {
            this.callback = callback;
        }
    }
}
