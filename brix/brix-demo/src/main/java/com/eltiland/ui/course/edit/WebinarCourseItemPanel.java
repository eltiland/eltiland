package com.eltiland.ui.course.edit;

import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import org.apache.wicket.model.IModel;

/**
 * Panel for editing webinar course item.
 *
 * @author Aleksey Plotnikov.
 */
public class WebinarCourseItemPanel extends AbstractCourseItemPanel<ELTWebinarCourseItem> {
    public WebinarCourseItemPanel(String id, IModel<ELTWebinarCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);
    }
}
