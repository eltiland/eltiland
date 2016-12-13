package com.eltiland.ui.course;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.test.TestCourseItem;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.audio.ELTAudioCourseItem;
import com.eltiland.model.course2.content.google.ELTContentCourseItem;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTGoogleCourseItem;
import com.eltiland.model.course2.content.google.ELTPresentationCourseItem;
import com.eltiland.model.course2.content.test.ELTTestCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import com.eltiland.ui.common.BaseEltilandPage;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.content.TestContentPanel;
import com.eltiland.ui.course.content2.AudioContentPanel;
import com.eltiland.ui.course.content2.ContentPanel;
import com.eltiland.ui.course.content2.GoogleContentPanel;
import com.eltiland.ui.course.content2.VideoContentPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page for edititng course element.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseItemPage extends BaseEltilandPage {

    @SpringBean
    private GenericManager genericManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseItemPage.class);
    /**
     * Page mount path.
     */
    public static final String MOUNT_PATH = "/item";

    /**
     * Profile id page parameter.
     */
    public static final String PARAM_ID = "id";

    /**
     * Page constructor.
     *
     * @param parameters page parameters.
     */
    public CourseItemPage(final PageParameters parameters) {
        super(parameters);

        if (!parameters.getNamedKeys().contains(PARAM_ID)) {
            String errMsg = String.format("Mandatory parameter: %s doesn't passed", PARAM_ID);
            LOGGER.error(errMsg);
            throw new WicketRuntimeException(errMsg);
        }

        final IModel<ELTCourseItem> courseItemIModel = new LoadableDetachableModel<ELTCourseItem>() {
            @Override
            protected ELTCourseItem load() {
                return genericManager.getObject(ELTCourseItem.class, parameters.get(PARAM_ID).toLong());
            }
        };

        Class clazz = courseItemIModel.getObject().getClass();
        if (clazz.equals(ELTContentCourseItem.class)) {
            add(new ContentPanel("panel", new GenericDBModel<>(ELTContentCourseItem.class,
                    (ELTContentCourseItem) courseItemIModel.getObject())));
        } else if (clazz.equals(ELTDocumentCourseItem.class)) {
            add(new GoogleContentPanel("panel", new GenericDBModel<>(ELTGoogleCourseItem.class,
                    (ELTGoogleCourseItem) courseItemIModel.getObject())));
        } else if (clazz.equals(ELTPresentationCourseItem.class)) {
            add(new GoogleContentPanel("panel", new GenericDBModel<>(ELTGoogleCourseItem.class,
                    (ELTGoogleCourseItem) courseItemIModel.getObject())));
        } else if (clazz.equals(ELTTestCourseItem.class)) {
            add(new TestContentPanel("panel", new GenericDBModel<>(TestCourseItem.class, (long) 76120)));
        } else if (clazz.equals(ELTVideoCourseItem.class)) {
            add(new VideoContentPanel("panel", new GenericDBModel<>(ELTVideoCourseItem.class,
                    (ELTVideoCourseItem) courseItemIModel.getObject())));
        } else if (clazz.equals(ELTWebinarCourseItem.class)) {
//            add(new WebinarCourseItemPanel("panel", new GenericDBModel<>(ELTWebinarCourseItem.class,
//                    (ELTWebinarCourseItem) courseItemIModel.getObject())));
        } else if (clazz.equals(ELTAudioCourseItem.class)) {
            add(new AudioContentPanel("panel", new GenericDBModel<>(
                    ELTAudioCourseItem.class, (ELTAudioCourseItem) courseItemIModel.getObject())));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_COURSE_ITEM_EDIT);
    }
}
