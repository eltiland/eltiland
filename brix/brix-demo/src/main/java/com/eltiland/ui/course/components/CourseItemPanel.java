package com.eltiland.ui.course.components;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.components.ReadonlyObjects;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.item.AbstractItemPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.common.resource.StaticImage;
import com.eltiland.ui.course.CourseControlPage;
import com.eltiland.ui.course.CourseNewContentPage;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

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

    private IModel<User> userModel = new GenericDBModel<>(User.class);

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
        return new ArrayList<>(Arrays.asList(ButtonAction.ENTER, ButtonAction.SETTINGS));
    }

    @Override
    protected IModel<String> getActionName(ButtonAction action) {
        if (action.equals(ButtonAction.ENTER)) {
            return new ResourceModel("enter");
        } else if (action.equals(ButtonAction.SETTINGS)) {
            return new ResourceModel("settings");
        } else {
            return ReadonlyObjects.EMPTY_DISPLAY_MODEL;
        }
    }

    @Override
    protected boolean isVisible(ButtonAction action) {
        if (action.equals(ButtonAction.SETTINGS)) {
            if (userModel.getObject().isSuperUser()) {
                return true;
            } else {
                List<ELTCourse> courses = eltCourseManager.getAdminCourses(userModel.getObject(), null);
                return courses.contains(getModelObject());
            }
        } else if (action.equals(ButtonAction.ENTER)) {
            return courseListenerManager.getItem(userModel.getObject(), getModelObject()).
                    getStatus().equals(PaidStatus.CONFIRMED);
        } else {
            return true;
        }
    }

    @Override
    protected void onClick(ButtonAction action, AjaxRequestTarget target) {
        if (action.equals(ButtonAction.ENTER)) {
            throw new RestartResponseException(CourseNewContentPage.class,
                    new PageParameters()
                            .add(CourseNewContentPage.PARAM_ID, getModelObject().getId())
                            .add(CourseNewContentPage.PARAM_VERSION, CourseNewContentPage.FULL_VERSION));
        } else if (action.equals(ButtonAction.SETTINGS)) {
            throw new RestartResponseException(CourseControlPage.class,
                    new PageParameters()
                            .add(CourseControlPage.PARAM_ID, getModelObject().getId()));
        }
    }
}
