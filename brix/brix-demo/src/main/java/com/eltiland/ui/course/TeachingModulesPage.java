package com.eltiland.ui.course;

import com.eltiland.BrixPanel;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.components.CourseIconPanel;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * Page with list of all teaching modules.
 *
 * @author Aleksey Plotnikov.
 */
public class TeachingModulesPage extends BaseEltilandPage {

    @SpringBean
    private ELTCourseManager courseManager;

    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/modules";

    private final String CSS = "static/css/panels/course_list.css";

    private IModel<List<AuthorCourse>> courseListModel = new LoadableDetachableModel<List<AuthorCourse>>() {
        @Override
        protected List<AuthorCourse> load() {
            return courseManager.getSortedAuthorCourses(0, 20, true);
        }
    };

    public TeachingModulesPage() {
        add(new BrixPanel("brix.panel", UrlUtils.createBrixPathForPanel("COURSE/modulesInfo.html")));

        add(new ListView<AuthorCourse>("courseListView", courseListModel) {
            @Override
            protected void populateItem(ListItem<AuthorCourse> item) {
                item.add(new CourseIconPanel("courseInnerPanel",
                        new GenericDBModel<>(ELTCourse.class, item.getModelObject())));
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
