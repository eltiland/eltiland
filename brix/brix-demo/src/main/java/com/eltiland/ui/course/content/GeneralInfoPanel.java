package com.eltiland.ui.course.content;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.Course;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel, for output general course information.
 *
 * @author Aleksey Plotnikov.
 */
public class GeneralInfoPanel extends BaseEltilandPanel<Course> {

    @SpringBean
    private GenericManager genericManager;

    public GeneralInfoPanel(String id, IModel<Course> courseModel) {
        super(id, courseModel);

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.add(new Label("contentField", courseModel.getObject().getName()));
        add(content);
    }
}
