package com.eltiland.ui.course.components.editPanels.elements;

import com.eltiland.bl.CourseItemManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.exceptions.ConstraintException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.course.WebinarCourseItem;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.webinars.plugin.components.WebinarPropertyPanel;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Webinar course item editing panel.
 *
 * @author Aleksey Plotnikov
 */
public class WebinarEditPanel extends AbstractCourseItemEditPanel<WebinarCourseItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoEditPanel.class);

    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private CourseItemManager courseItemManager;
    @SpringBean
    private GenericManager genericManager;

    private IModel<Webinar> webinarIModel = new GenericDBModel<>(Webinar.class);

    /**
     * Default constructor.
     *
     * @param id                      markup id.
     * @param webinarCourseItemIModel webinar course item.
     */
    public WebinarEditPanel(String id, IModel<WebinarCourseItem> webinarCourseItemIModel) {
        super(id, webinarCourseItemIModel);

        genericManager.initialize(getModelObject(), getModelObject().getWebinar());
        webinarIModel.setObject(getModelObject().getWebinar());

        final WebMarkupContainer processContainer = new WebMarkupContainer("process") {
            @Override
            public boolean isVisible() {
                return webinarIModel.getObject() != null && !(webinarIModel.getObject().isApproved());
            }
        };

        WebinarPropertyPanel webinarPanel = new WebinarPropertyPanel("webinarPropertyPanel") {
            @Override
            protected void createWebinar(Webinar webinar, AjaxRequestTarget target) {
                webinar.setApproved(false);
                webinar.setCourse(true);
                try {
                    webinarManager.create(webinar);
                    WebinarCourseItem item = WebinarEditPanel.this.getModelObject();
                    item.setWebinar(webinar);
                    courseItemManager.updateCourseItem(WebinarEditPanel.this.getModelObject());

                    WebinarUserPayment userPayment = new WebinarUserPayment();
                    populateWebinarModerator(userPayment, webinar);
                    userPayment.setRole(WebinarUserPayment.Role.MODERATOR);
                    userPayment.setStatus(PaidStatus.CONFIRMED);
                    genericManager.saveNew(userPayment);
                } catch (EltilandManagerException | ConstraintException e) {
                    LOGGER.error("Cannot create course item", e);
                    throw new WicketRuntimeException("Cannot create course item", e);
                }
                ELTAlerts.renderOKPopup(getString("createMessage"), target);
                webinarIModel.setObject(webinar);
                target.add(this);
                target.add(processContainer);
            }

            @Override
            public boolean isVisible() {
                return webinarIModel.getObject() == null || webinarIModel.getObject().isApproved();
            }
        };

        add(webinarPanel.setOutputMarkupPlaceholderTag(true));

        if (webinarIModel.getObject() != null) {
            webinarPanel.setWebinarModel(new GenericDBModel<>(Webinar.class, webinarIModel.getObject()));
        }

        add(processContainer.setOutputMarkupId(true));
    }

    @Override
    protected boolean showActions() {
        return true;
    }
}
