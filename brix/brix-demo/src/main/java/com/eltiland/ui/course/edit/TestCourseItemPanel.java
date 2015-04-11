package com.eltiland.ui.course.edit;

import com.eltiland.model.course2.content.test.ELTTestCourseItem;
import org.apache.wicket.model.IModel;

/**
 * Test edit panel for courses.
 *
 * @author Aleksey Plotnikov.
 */
public class TestCourseItemPanel extends AbstractCourseItemPanel<ELTTestCourseItem> {
    public TestCourseItemPanel(String id, IModel<ELTTestCourseItem> eltCourseItemIModel) {
        super(id, eltCourseItemIModel);
    }
}
