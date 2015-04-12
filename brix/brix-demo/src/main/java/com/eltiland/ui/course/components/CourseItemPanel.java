package com.eltiland.ui.course.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.course2.listeners.ListenerType;
import com.eltiland.model.file.File;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.behavior.AjaxDownload;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.course.CourseControlPage;
import com.eltiland.ui.course.CourseNewContentPage;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

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
public class CourseItemPanel extends AbstractItemPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseManager eltCourseManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private FileUtility fileUtility;

    private IModel<User> userModel = new GenericDBModel<>(User.class);

    private IModel<ELTCourseListener> listenerModel = new LoadableDetachableModel<ELTCourseListener>() {
        @Override
        protected ELTCourseListener load() {
            return courseListenerManager.getItem(userModel.getObject(), getModelObject());
        }
    };

    private IModel<File> documentModel = new LoadableDetachableModel<File>() {
        @Override
        protected File load() {
            if (listenerModel.getObject() != null && getModelObject() instanceof TrainingCourse) {
                if (listenerModel.getObject().getType().equals(ListenerType.PHYSICAL)) {
                    genericManager.initialize(getModelObject(), ((TrainingCourse) getModelObject()).getPhysicalDoc());
                    return ((TrainingCourse) getModelObject()).getPhysicalDoc();
                } else {
                    genericManager.initialize(getModelObject(), ((TrainingCourse) getModelObject()).getLegalDoc());
                    return ((TrainingCourse) getModelObject()).getLegalDoc();
                }
            } else {
                return null;
            }
        }
    };

    /**
     * Panel constructor.
     *
     * @param id           markup id.
     * @param courseIModel course panel model.
     */
    public CourseItemPanel(String id, final IModel<ELTCourse> courseIModel, IModel<User> userModel) {
        super(id, courseIModel);
        genericManager.initialize(getModelObject(), getModelObject().getAuthor());

        this.userModel = userModel;
        add(new Label("courseAuthor", String.format(getString("authorLabel"), getModelObject().getAuthor().getName())));
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
    protected String getEntityName(IModel<ELTCourse> itemModel) {
        return itemModel.getObject().getName();
    }

    @Override
    protected String getEntityDescription(IModel<ELTCourse> itemModel) {
        return "";
    }

    @Override
    protected List<ButtonAction> getActionList() {
        return new ArrayList<>(Arrays.asList(ButtonAction.ENTER, ButtonAction.SETTINGS, ButtonAction.DOWNLOAD));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        switch (action) {
            case ENTER:
                return new ResourceModel("enter");
            case SETTINGS:
                return new ResourceModel("settings");
            case DOWNLOAD:
                return new ResourceModel("download");
            default:
                return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        switch (action) {
            case SETTINGS:
                if (userModel.getObject().isSuperUser()) {
                    return true;
                } else {
                    List<ELTCourse> courses = eltCourseManager.getAdminCourses(userModel.getObject(), null);
                    return courses.contains(getModelObject());
                }
            case ENTER:
                return listenerModel.getObject() != null &&
                        listenerModel.getObject().getStatus().equals(PaidStatus.CONFIRMED);
            case DOWNLOAD:
                return getModelObject() instanceof TrainingCourse && listenerModel.getObject() != null &&
                        !(listenerModel.getObject().getStatus().equals(PaidStatus.NEW))
                        && documentModel.getObject() != null;
            default:
                return true;
        }
    }

    @Override
    protected void onClick(ButtonAction action, AjaxRequestTarget target) {
        switch (action) {
            case ENTER:
                setResponsePage(CourseNewContentPage.class,
                        new PageParameters()
                                .add(CourseNewContentPage.PARAM_ID, getModelObject().getId())
                                .add(CourseNewContentPage.PARAM_VERSION, CourseNewContentPage.FULL_VERSION));
            case SETTINGS:
                setResponsePage(CourseControlPage.class,
                        new PageParameters().add(CourseControlPage.PARAM_ID, getModelObject().getId()));
            case DOWNLOAD:
                ajaxDownload.initiate(target);
                break;
            default:
                break;
        }
    }

    final AjaxDownload ajaxDownload = new AjaxDownload() {
        @Override
        protected String getFileName() {
            return documentModel.getObject().getName();
        }

        @Override
        protected IResourceStream getResourceStream() {
            return new AbstractResourceStream() {
                @Override
                public InputStream getInputStream() throws ResourceStreamNotFoundException {
                    genericManager.initialize(documentModel.getObject(), documentModel.getObject().getBody());

                    IResourceStream resourceStream = fileUtility.getFileResource(
                            documentModel.getObject().getBody().getHash());
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
        } else {
            return null;
        }
    }
}
