package com.eltiland.ui.course.control.users.panel;

import com.eltiland.bl.GenericManager;
import com.eltiland.model.course.CourseListener;
import com.eltiland.ui.common.BaseEltilandPanel;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Panel for organization information for listener.
 *
 * @author Aleksey Plotnikov.
 */
public class OrganizationPanel extends BaseEltilandPanel<CourseListener> {
    @SpringBean
    private GenericManager genericManager;

    private boolean allEmpty;

    public OrganizationPanel(String id, IModel<CourseListener> courseListenerIModel) {
        super(id, courseListenerIModel);
        genericManager.initialize(getModelObject(), getModelObject().getListener());

        allEmpty = true;
        String company = getModelObject().getListener().getOrganization();
        String job = getModelObject().getListener().getAppointment();
        Integer exp = getModelObject().getListener().getExperience();
        final boolean hasCompany = company != null && !company.isEmpty();
        final boolean hasJob = job != null && !job.isEmpty();
        final boolean hasExp = exp != null && (exp != 0);
        if (hasCompany || hasExp || hasJob) {
            allEmpty = false;
        }

        add(new Label("organization", String.format(getString("company"), company)) {
            @Override
            public boolean isVisible() {
                return hasCompany;
            }
        });

        add(new Label("position", String.format(getString("job"), job)) {
            @Override
            public boolean isVisible() {
                return hasJob;
            }
        });

        add(new Label("experience", exp != null ? (String.format(getString("exp"), exp,
                getString((exp == 1) ? ("one") : ((exp > 4) ? ("many") : ("several"))))) : (StringUtils.EMPTY)) {
            @Override
            public boolean isVisible() {
                return hasExp;
            }
        });

        add(new WebMarkupContainer("allEmpty") {
            @Override
            public boolean isVisible() {
                return allEmpty;
            }
        });
    }
}
