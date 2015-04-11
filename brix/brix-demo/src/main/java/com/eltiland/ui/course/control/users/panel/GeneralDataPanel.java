package com.eltiland.ui.course.control.users.panel;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.CourseListener;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for organization information for listener.
 *
 * @author Aleksey Plotnikov.
 */
public class GeneralDataPanel extends BaseEltilandPanel<CourseListener> {
    @SpringBean
    private GenericManager genericManager;

    public GeneralDataPanel(String id, IModel<CourseListener> courseListenerIModel) {
        super(id, courseListenerIModel);
        genericManager.initialize(getModelObject(), getModelObject().getListener());

        String phone = getModelObject().getListener().getPhone();
        String skype = getModelObject().getListener().getSkype();
        final boolean hasPhone = phone != null && !phone.isEmpty();
        final boolean hasSkype = skype != null && !skype.isEmpty();

        add(new Label("email", String.format(getString("email"), getModelObject().getListener().getEmail())));
        add(new Label("phone", String.format(getString("phone"), phone)) {
            @Override
            public boolean isVisible() {
                return hasPhone;
            }
        });

        add(new Label("skype", String.format(getString("skype"), skype)) {
            @Override
            public boolean isVisible() {
                return hasSkype;
            }
        });
    }
}
