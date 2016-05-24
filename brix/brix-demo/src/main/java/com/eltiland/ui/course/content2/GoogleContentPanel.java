package com.eltiland.ui.course.content2;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.CoursePrintStatManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.google.CourseItemPrintStat;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.google.GoogleDriveFile;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.EltiStaticAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.google.ELTGoogleDriveEditor;
import com.eltiland.ui.google.buttons.GooglePrintButton;
import com.eltiland.utils.MimeType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for output simple document of course.
 *
 * @author Aleksey Plotnikov.
 */
public class GoogleContentPanel extends AbstractCourseContentPanel<ELTGoogleCourseItem> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private CoursePrintStatManager coursePrintStatManager;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<ELTCourse> courseModel = new LoadableDetachableModel<ELTCourse>() {
        @Override
        protected ELTCourse load() {
            return courseItemManager.getCourse(getModelObject());
        }
    };

    private IModel<ELTCourseListener> listenerModel = new LoadableDetachableModel<ELTCourseListener>() {
        @Override
        protected ELTCourseListener load() {
            return courseListenerManager.getItem(currentUserModel.getObject(), courseModel.getObject());
        }
    };

    private IModel<CourseItemPrintStat> statModel = new LoadableDetachableModel<CourseItemPrintStat>() {
        @Override
        protected CourseItemPrintStat load() {
            return coursePrintStatManager.getItem(listenerModel.getObject(), getModelObject());
        }
    };

    /**
     * Panel constructor.
     *
     * @param id                     markup id.
     * @param googleCourseItemIModel lecture model.
     */
    public GoogleContentPanel(String id, IModel<ELTGoogleCourseItem> googleCourseItemIModel) {
        super(id, googleCourseItemIModel);

        genericManager.initialize(getModelObject(), getModelObject().getItem());

        GoogleDriveFile.TYPE type = GoogleDriveFile.TYPE.DOCUMENT;
        String mimeType = getModelObject().getItem().getMimeType();
        if (MimeType.getDocumentTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.DOCUMENT;
        } else if (MimeType.getPresentationTypes().contains(mimeType)) {
            type = GoogleDriveFile.TYPE.PRESENTATION;
        }

        add(new ActionPanel("controlPanel",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem())) {
            @Override
            public boolean isVisible() {
                ELTGoogleCourseItem item = GoogleContentPanel.this.getModelObject();
                return (item instanceof ELTDocumentCourseItem) && ((ELTDocumentCourseItem) item).isPrintable();
            }
        });

        ELTGoogleDriveEditor contentField = new ELTGoogleDriveEditor("contentField",
                new GenericDBModel<>(GoogleDriveFile.class, getModelObject().getItem()),
                ELTGoogleDriveEditor.MODE.VIEW, type);
        add(contentField);

        if (getModelObject() instanceof ELTDocumentCourseItem) {
            if (((ELTDocumentCourseItem) getModelObject()).isProhibitSelect()) {
                contentField.add(new AttributeAppender("class", new Model<>("no-select"), " "));
            }
        }
    }

    private class ActionPanel extends BaseEltilandPanel<GoogleDriveFile> {

        protected ActionPanel(String id, IModel<GoogleDriveFile> googleDriveFileIModel) {
            super(id, googleDriveFileIModel);

            add(new GooglePrintButton("printButton", new GenericDBModel<>(GoogleDriveFile.class, getModelObject())) {

                @Override
                protected Long getCurrentPrint(AjaxRequestTarget target) {
                    if (statModel.getObject() == null) {
                        try {
                            CourseItemPrintStat stat = coursePrintStatManager.create(
                                    listenerModel.getObject(), GoogleContentPanel.this.getModelObject());
                            return stat.getCurrentPrint();
                        } catch (CourseException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }
                    return statModel.getObject().getCurrentPrint();
                }

                @Override
                protected void onAfterPrint(AjaxRequestTarget target) {
                    statModel.detach();
                    Long currentPrint = statModel.getObject().getCurrentPrint();
                    statModel.getObject().setCurrentPrint(currentPrint + 1);
                    try {
                        coursePrintStatManager.update(statModel.getObject());
                    } catch (CourseException e) {
                        ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    }
                }

                @Override
                protected Long getLimit() {
                    boolean isLogged = currentUserModel.getObject() != null;
                    if (!isLogged) {
                        return null;
                    } else {
                        ELTGoogleCourseItem item = GoogleContentPanel.this.getModelObject();
                        if (!(item instanceof ELTDocumentCourseItem)) {
                            return null;
                        } else {
                            if (!(((ELTDocumentCourseItem) item).isPrintable())) {
                                return null;
                            } else {
                                CourseItemPrintStat stat = coursePrintStatManager.getItem(
                                        listenerModel.getObject(), item);
                                if( stat != null ) {
                                    return stat.getPrintLimit();
                                } else {
                                    return ((ELTDocumentCourseItem) item).getLimit();
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
