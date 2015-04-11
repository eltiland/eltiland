package com.eltiland.ui.course.components;

import com.eltiland.bl.*;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.FileException;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseDocument;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.file.File;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogUpdateCallback;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.upload.ELTUploadComponent;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.course.CourseContentPage;
import com.eltiland.ui.course.CourseControlPage;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.UrlUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
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
 * Course item view panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseItemPanel_old extends AbstractItemPanel<Course> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseItemPanel_old.class);

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private CourseListenerManager courseListenerManager;
    @SpringBean
    private CourseSessionManager courseSessionManager;
    @SpringBean
    private CourseDocumentManager courseDocumentManager;
    @SpringBean
    private FileUtility fileUtility;
    @SpringBean
    private FileManager fileManager;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<CourseListener> listenerIModel = new LoadableDetachableModel<CourseListener>() {
        @Override
        protected CourseListener load() {
            return courseListenerManager.getListener(getModelObject(), currentUserModel.getObject());
        }
    };

    private IModel<File> fileModel = new LoadableDetachableModel<File>() {
        @Override
        protected File load() {
            CourseSession session = courseSessionManager.getActiveSession(getModelObject());
            CourseDocument document = courseDocumentManager.getDocumentForSession(session);
            CourseListener listener = listenerIModel.getObject();
            if (listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                return document.getPhysicalDoc();
            } else {
                return document.getLegalDoc();
            }
        }
    };

    private IModel<File> authorFileModel = new LoadableDetachableModel<File>() {
        @Override
        protected File load() {
            listenerIModel.detach();
            if (listenerIModel.getObject() == null) {
                return null;
            } else {
                genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getAuthorDocument());
                return listenerIModel.getObject().getAuthorDocument();
            }
        }
    };

    private Dialog<PayPanel> payPanelDialog = new Dialog<PayPanel>("payDialog", 400) {
        @Override
        public PayPanel createDialogPanel(String id) {
            return new PayPanel(id);
        }
    };

    private Dialog<UploadPanel> uploadPanelDialog = new Dialog<UploadPanel>("uploadDialog", 275) {
        @Override
        public UploadPanel createDialogPanel(String id) {
            return new UploadPanel(id);
        }

        @Override
        public void registerCallback(UploadPanel panel) {
            super.registerCallback(panel);
            panel.setUpdateCallback(new IDialogUpdateCallback.IDialogActionProcessor<File>() {
                @Override
                public void process(IModel<File> model, AjaxRequestTarget target) {
                    CourseListener listener = listenerIModel.getObject();
                    listener.setDocument(model.getObject());
                    try {
                        genericManager.update(listener);
                    } catch (ConstraintException e) {
                        LOGGER.error("Cannot save listener", e);
                        throw new WicketRuntimeException("Cannot save listener", e);
                    }
                    close(target);
                    ELTAlerts.renderOKPopup(getString("uploadedMessage"), target);
                }
            });
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course panel model.
     */
    public CourseItemPanel_old(String id, final IModel<Course> courseIModel) {
        super(id, courseIModel);
        genericManager.initialize(getModelObject(), getModelObject().getAuthor());

        add(new Label("courseAuthor", String.format(getString("authorLabel"), getModelObject().getAuthor().getName())));

        Label status = new Label("status", new Model<String>());
        if (getModelObject().isTraining()) {
            CourseListener listener = listenerIModel.getObject();
            if (listener != null) {
                if (listener.getOffer() != null && !(listener.getOffer().equals(StringUtils.EMPTY))) {
                    status.setDefaultModelObject(String.format(getString("PAYS_OFFER"), listener.getOffer()));
                } else {
                    status.setDefaultModelObject(getString(listener.getStatus().toString()));
                }
            }
        }

        add(uploadPanelDialog);
        add(payPanelDialog);
        add(status);
    }

    @Override
    protected WebComponent getIcon(String markupId) {
        return new StaticImage(markupId, UrlUtils.StandardIcons.ICON_ITEM_COURSE.getPath());
    }

    @Override
    protected String getIconLabel() {
        return getString("course");
    }

    @Override
    protected String getEntityName(IModel<Course> itemModel) {
        return itemModel.getObject().getName();
    }

    @Override
    protected String getEntityDescription(IModel<Course> itemModel) {
        return "";
    }

    @Override
    protected List<ButtonAction> getActionList() {
        return new ArrayList<>(Arrays.asList(
                ButtonAction.ENTER, ButtonAction.SETTINGS, ButtonAction.DOWNLOAD,
                ButtonAction.UPLOAD, ButtonAction.PAY, ButtonAction.PAYMENT));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        if (action.equals(ButtonAction.ENTER)) {
            return new ResourceModel("enter");
        } else if (action.equals(ButtonAction.SETTINGS)) {
            return new ResourceModel("settings");
        } else if (action.equals(ButtonAction.DOWNLOAD)) {
            return new ResourceModel("download");
        } else if (action.equals(ButtonAction.UPLOAD)) {
            return new ResourceModel("upload");
        } else if (action.equals(ButtonAction.PAY)) {
            return new ResourceModel("pay");
        } else if (action.equals(ButtonAction.PAYMENT)) {
            return new ResourceModel("payment");
        } else {
            return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        if (action.equals(ButtonAction.SETTINGS)) {
            genericManager.initialize(getModelObject(), getModelObject().getAuthor());
            return currentUserModel.getObject() != null &&
                    currentUserModel.getObject().isSuperUser() ||
                    currentUserModel.getObject().getId().equals(getModelObject().getAuthor().getId());
        } else if (action.equals(ButtonAction.ENTER)) {
            if (getModelObject().isTraining()) {
                CourseListener listener = listenerIModel.getObject();
                return listener != null && listener.getStatus().equals(CourseListener.Status.CONFIRMED);
            } else {
                return true;
            }
        } else if (action.equals(ButtonAction.DOWNLOAD)) {
            if (getModelObject().isTraining()) {
                CourseSession session = courseSessionManager.getActiveSession(getModelObject());
                if (session != null) {
                    CourseDocument document = courseDocumentManager.getDocumentForSession(session);
                    CourseListener listener = listenerIModel.getObject();
                    if (listener != null) {
                        if (listener.getKind().equals(CourseListener.Kind.MOSCOW)) {
                            return false;
                        }
                        if (document != null && listener != null) {
                            if (listener.getKind().equals(CourseListener.Kind.PHYSICAL)) {
                                return document.getPhysicalDoc() != null;
                            } else {
                                return document.getLegalDoc() != null;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (action.equals(ButtonAction.UPLOAD)) {
            if (getModelObject().isTraining()) {
                CourseListener listener = listenerIModel.getObject();
                return listener != null && (listener.getStatus().equals(CourseListener.Status.APPROVED)
                        || listener.getStatus().equals(CourseListener.Status.PAYS));
            } else {
                return false;
            }
        } else if (action.equals(ButtonAction.PAY)) {
            return listenerIModel.getObject() != null &&
                    listenerIModel.getObject().getStatus().equals(CourseListener.Status.PAYS);
        } else if (action.equals(ButtonAction.PAYMENT)) {
            return authorFileModel.getObject() != null;
        } else {
            return true;
        }
    }

    @Override
    protected void onClick(ButtonAction action, AjaxRequestTarget target) {
        if (action.equals(ButtonAction.ENTER)) {
            if (getModelObject().isTraining()) {
                CourseSession session = courseSessionManager.getActiveSession(getModelObject());
                if (session.getStartDate().after(DateUtils.getCurrentDate())) {
                    ELTAlerts.renderErrorPopup(String.format(
                            getString("accessLater"), DateUtils.formatRussianDate(session.getStartDate())), target);
                    return;
                }
            }
            throw new RestartResponseException(CourseContentPage.class,
                    new PageParameters()
                            .add(CourseContentPage.PARAM_ID, getModelObject().getId())
                            .add(CourseContentPage.PARAM_KIND, CourseContentPage.FULL_KIND));
        } else if (action.equals(ButtonAction.SETTINGS)) {
            throw new RestartResponseException(CourseControlPage.class,
                    new PageParameters()
                            .add(CourseContentPage.PARAM_ID, getModelObject().getId()));
        } else if (action.equals(ButtonAction.DOWNLOAD)) {
            ajaxDownload.initiate(target);
        } else if (action.equals(ButtonAction.UPLOAD)) {
            uploadPanelDialog.show(target);
        } else if (action.equals(ButtonAction.PAY)) {
            String req = listenerIModel.getObject().getRequisites();
            if (req == null || req.isEmpty()) {
                CourseDocument document = courseDocumentManager.getDocumentForSession(
                        courseSessionManager.getActiveSession(getModelObject()));
                req = document.getRequisites();
            }
            payPanelDialog.getDialogPanel().initData(req);
            payPanelDialog.show(target);
        } else if (action.equals(ButtonAction.PAYMENT)) {
            ajaxAuthorDownload.initiate(target);
        }
    }

    final AjaxDownload ajaxDownload = new AjaxDownload() {
        @Override
        protected String getFileName() {
            return fileModel.getObject().getName();
        }

        @Override
        protected IResourceStream getResourceStream() {
            return new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    genericManager.initialize(fileModel.getObject(), fileModel.getObject().getBody());

                    IResourceStream resourceStream = fileUtility.getFileResource(
                            fileModel.getObject().getBody().getHash());
                    return resourceStream.getInputStream();
                }

                @Override
                public void close() throws IOException {
                }
            };
        }
    };

    final AjaxDownload ajaxAuthorDownload = new AjaxDownload() {
        @Override
        protected String getFileName() {
            return authorFileModel.getObject().getName();
        }

        @Override
        protected IResourceStream getResourceStream() {
            return new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    genericManager.initialize(authorFileModel.getObject(), authorFileModel.getObject().getBody());

                    IResourceStream resourceStream = fileUtility.getFileResource(
                            authorFileModel.getObject().getBody().getHash());
                    return resourceStream.getInputStream();
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
        } else if (action.equals(ButtonAction.PAYMENT)) {
            return ajaxAuthorDownload;
        } else {
            return super.getAdditionalBehavior(action);
        }
    }

    private class UploadPanel extends ELTDialogPanel implements IDialogUpdateCallback<File> {
        private ELTUploadComponent uploadPanel = new ELTUploadComponent("uploadPanel", 1);

        private IDialogActionProcessor<File> callback;

        public UploadPanel(String id) {
            super(id);

            form.add(uploadPanel);
            form.setMultiPart(true);
            genericManager.initialize(listenerIModel.getObject(), listenerIModel.getObject().getDocument());
            if (listenerIModel.getObject().getDocument() != null) {
                uploadPanel.setUploadedFiles(new ArrayList<>(Arrays.asList(listenerIModel.getObject().getDocument())));
            }
        }

        @Override
        protected String getHeader() {
            return getString("header");
        }

        @Override
        protected List<EVENT> getActionList() {
            return new ArrayList<>(Arrays.asList(EVENT.Save));
        }

        @Override
        protected void eventHandler(EVENT event, AjaxRequestTarget target) {
            List<File> files = uploadPanel.getUploadedFiles();
            if (files.isEmpty()) {
                ELTAlerts.renderErrorPopup(getString("noFile"), target);
            } else {
                try {
                    File file = fileManager.saveFile(files.get(0));
                    callback.process(new GenericDBModel<>(File.class, file), target);
                } catch (FileException e) {
                    LOGGER.error("Cannot save file", e);
                    throw new WicketRuntimeException("Cannot save file", e);
                }
            }
        }

        @Override
        public void setUpdateCallback(IDialogActionProcessor<File> callback) {
            this.callback = callback;
        }
    }

    private class PayPanel extends ELTDialogPanel {
        private ELTTextArea requisites =
                new ELTTextArea("requisites", ReadonlyObjects.EMPTY_DISPLAY_MODEL, new Model<String>()) {
                    @Override
                    protected boolean isFillToWidth() {
                        return true;
                    }

                    @Override
                    protected int getInitialHeight() {
                        return 130;
                    }
                };

        public PayPanel(String id) {
            super(id);
            requisites.setReadonly(true);
            form.add(requisites);
        }

        public void initData(String data) {
            requisites.setModelObject(data);
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
