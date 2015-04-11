package com.eltiland.ui.worktop.simple.panel.files.panels;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.file.UserFile;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel for output user access information.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseAccessPanel extends BaseEltilandPanel<UserFile> {

    @SpringBean
    private GenericManager genericManager;

    private IModel<List<ELTCourse>> courseModel = new LoadableDetachableModel<List<ELTCourse>>() {
        @Override
        protected List<ELTCourse> load() {
            return new ArrayList<>(getModelObject().getCourses());
        }
    };

    public CourseAccessPanel(String id, IModel<UserFile> userFileIModel) {
        super(id, userFileIModel);

        genericManager.initialize(getModelObject(), getModelObject().getCourses());

        add(new ListView<ELTCourse>("userList", courseModel) {
            @Override
            protected void populateItem(ListItem<ELTCourse> item) {
                item.add(new Label("name", item.getModelObject().getName()));
            }
        });
    }
}
