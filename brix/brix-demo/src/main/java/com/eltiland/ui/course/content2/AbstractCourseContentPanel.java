package com.eltiland.ui.course.content2;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.button.icon.ButtonAction;
import com.eltiland.ui.common.components.button.icon.IconButton;
import com.eltiland.ui.course.CourseNewContentPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Abstract course content panel.
 *
 * @author Aleksey Plotnikov.
 */
public class AbstractCourseContentPanel<T extends ELTCourseItem> extends BaseEltilandPanel<T> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;

    protected AbstractCourseContentPanel(String id, IModel<T> tiModel) {
        super(id, tiModel);
        add(new Label("name", getModelObject().getName()));

        add(new IconButton("back", new ResourceModel("back.label"), ButtonAction.BACK) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                ELTCourseItem item = AbstractCourseContentPanel.this.getModelObject();

                genericManager.initialize(item, item.getBlock());
                ELTCourseBlock block = item.getBlock();
                if (block == null) {
                    genericManager.initialize(item, item.getParent());
                    genericManager.initialize(item.getParent(), item.getParent().getBlock());
                    block = item.getParent().getBlock();
                }
                genericManager.initialize(block, block.getCourse());
                genericManager.initialize(block, block.getDemoCourse());

                boolean isDemo = block.getCourse() != null ? false : true;
                ELTCourse course = isDemo ? block.getDemoCourse() : block.getCourse();

                throw new RestartResponseException(CourseNewContentPage.class,
                        new PageParameters().add(CourseNewContentPage.PARAM_ID, course.getId())
                                .add(CourseNewContentPage.PARAM_VERSION, isDemo ?
                                        CourseNewContentPage.DEMO_VERSION : CourseNewContentPage.FULL_VERSION));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE_ITEM);
    }
}
