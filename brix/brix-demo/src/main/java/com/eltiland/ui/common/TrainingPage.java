package com.eltiland.ui.common;

import com.eltiland.BrixPanel;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.CourseIconPanel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Page for "Повышаем квалификацию".
 *
 * @author Aleksey Plotnikov.
 */
public class TrainingPage extends TwoColumnPage {

    @SpringBean
    private ELTCourseManager courseManager;

    public static final String MOUNT_PATH = "/train";

    private final String CSS = "static/css/panels/course_list.css";

    private IModel<List<TrainingCourse>> courseListModel = new LoadableDetachableModel<List<TrainingCourse>>() {
        @Override
        protected List<TrainingCourse> load() {
            return courseManager.getActiveTrainingCourses();
        }
    };

    public TrainingPage() {
        add(new BrixPanel("panel", UrlUtils.createBrixPathForPage("training.html")));
        WebMarkupContainer activeCourseContainer = new WebMarkupContainer("activeCourses") {
            @Override
            public boolean isVisible() {
                return courseListModel.getObject().size() > 0;
            }
        };

        activeCourseContainer.add(new ListView<ELTCourse>("activeCoursesList", courseListModel) {
            @Override
            protected void populateItem(ListItem<ELTCourse> item) {
                item.add(new CourseIconPanel("courseInnerPanel",
                        new GenericDBModel<>(ELTCourse.class, item.getModelObject())));
            }
        });

        add(activeCourseContainer);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
