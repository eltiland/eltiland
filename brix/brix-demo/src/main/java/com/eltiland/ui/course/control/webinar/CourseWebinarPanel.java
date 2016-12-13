package com.eltiland.ui.course.control.webinar;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.WebinarManager;
import com.eltiland.bl.WebinarUserPaymentManager;
import com.eltiland.bl.course.ELTCourseItemManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.course.ELTCourseManager;
import com.eltiland.exceptions.CourseException;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.WebinarException;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.ResourcesUtils;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.utils.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel for controlling webinars of the course.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseWebinarPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private ELTCourseManager courseManager;
    @SpringBean
    private WebinarManager webinarManager;
    @SpringBean
    private ELTCourseItemManager courseItemManager;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;
    @SpringBean
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @SpringBean
    private GenericManager genericManager;

    private ELTTable<ELTWebinarCourseItem> grid;

    private IModel<ELTWebinarCourseItem> courseItemIModel = new GenericDBModel<>(ELTWebinarCourseItem.class);

    private Dialog<WebinarItemPanel> webinarItemPanelDialog =
            new Dialog<WebinarItemPanel>("webinar_property_dialog", 400) {
                @Override
                public WebinarItemPanel createDialogPanel(String id) {
                    return new WebinarItemPanel(id);
                }

                @Override
                public void registerCallback(WebinarItemPanel panel) {
                    super.registerCallback(panel);
                    panel.setSimpleNewCallback(new IDialogSimpleNewCallback.IDialogActionProcessor<WebinarData>() {
                        @Override
                        public void process(IModel<WebinarData> model, AjaxRequestTarget target) {

                            // creating webinar item
                            Webinar webinar = new Webinar();
                            webinar.setName(model.getObject().getName());
                            webinar.setStartDate(model.getObject().getDate());
                            webinar.setDuration(model.getObject().getDuration().intValue());
                            webinar.setStatus(Webinar.Status.OPENED);
                            webinar.setCourse(true);
                            webinar.setNeedConfirm(false);
                            webinar.setApproved(true);

                            try {
                                webinarManager.create(webinar);
                            } catch (EltilandManagerException | WebinarException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }

                            courseItemIModel.getObject().setWebinar(webinar);
                            try {
                                courseItemManager.update(courseItemIModel.getObject());
                            } catch (CourseException e) {
                                ELTAlerts.renderErrorPopup(e.getMessage(), target);
                            }

                            ELTAlerts.renderOKPopup(getString("webinar.created"), target);
                            close(target);
                            target.add(grid);
                        }
                    });
                }
            };

    public CourseWebinarPanel(String id, IModel<ELTCourse> eltCourseIModel) {
        super(id, eltCourseIModel);

        grid = new ELTTable<ELTWebinarCourseItem>("grid", 20) {
            @Override
            protected List<IColumn<ELTWebinarCourseItem>> getColumns() {
                List<IColumn<ELTWebinarCourseItem>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<ELTWebinarCourseItem>(
                        new ResourceModel("name.column"), "name", "name"));
                columns.add(new AbstractColumn<ELTWebinarCourseItem>(new ResourceModel("status.column")) {
                    @Override
                    public void populateItem(Item<ICellPopulator<ELTWebinarCourseItem>> item,
                                             String s, IModel<ELTWebinarCourseItem> iModel) {
                        boolean isWebinar = !(iModel.getObject().getWebinar() == null);
                        Label label = new Label(s, new ResourceModel(isWebinar ? "yes" : "no"));
                        label.add(new AttributeModifier("class",
                                new Model<>(isWebinar ? "disactive_item " : "active_item")));
                        item.add(label);
                    }
                });
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return courseManager.getWebinars(CourseWebinarPanel.this.getModelObject(),
                        first, count, getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return courseManager.getWebinarsCount(CourseWebinarPanel.this.getModelObject());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<ELTWebinarCourseItem> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.NEW, GridAction.SYNC));
            }

            @Override
            protected boolean isActionVisible(GridAction action, IModel<ELTWebinarCourseItem> rowModel) {
                boolean hasWebinar = rowModel.getObject().getWebinar() != null;
                if (action.equals(GridAction.NEW)) {
                    return !hasWebinar;
                } else if (action.equals(GridAction.SYNC)) {
                    return hasWebinar;
                } else {
                    return false;
                }
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.NEW)) {
                    return getString("new.action");
                } else if (action.equals(GridAction.SYNC)) {
                    return getString("sync.action");
                } else {
                    return StringUtils.EMPTY_STRING;
                }
            }

            @Override
            protected boolean hasConfirmation(GridAction action) {
                return action.equals(GridAction.SYNC);
            }

            @Override
            protected void onClick(IModel<ELTWebinarCourseItem> rowModel, GridAction action, AjaxRequestTarget target) {
                if (action.equals(GridAction.NEW)) {
                    courseItemIModel.setObject(rowModel.getObject());
                    webinarItemPanelDialog.show(target);
                } else if (action.equals(GridAction.SYNC)) {
                    genericManager.initialize(rowModel.getObject(), rowModel.getObject().getWebinar());
                    Webinar webinar = rowModel.getObject().getWebinar();


                    for (ELTCourseListener listener : courseListenerManager.getList(getModelObject(), true, false)) {

                        if (webinarUserPaymentManager.hasAlreadyRegistered(webinar, listener.getUserEmail()))
                            continue;

                        WebinarUserPayment userPayment = new WebinarUserPayment();
                        userPayment.setUserEmail(listener.getUserEmail());

                        String names[] = Strings.split(listener.getUserName(), ' ');
                        userPayment.setUserSurname(names[0]);
                        if (names.length > 1) {
                            userPayment.setUserName(names[1]);
                        }
                        if (names.length > 2) {
                            userPayment.setPatronymic(names[2]);
                        }

                        userPayment.setStatus(PaidStatus.CONFIRMED);
                        userPayment.setWebinar(webinar);
                        userPayment.setRole(WebinarUserPayment.Role.MEMBER);
                        userPayment.setPrice(BigDecimal.ZERO);
                        userPayment.setRegistrationDate(DateTime.now().toDate());

                        try {
                            webinarUserPaymentManager.createUser(userPayment);
                        } catch (EltilandManagerException | EmailException | WebinarException e) {
                            ELTAlerts.renderErrorPopup(e.getMessage(), target);
                        }
                    }
                    ELTAlerts.renderOKPopup(getString("sync.created"), target);
                }
            }

            @Override
            protected String getNotFoundedMessage() {
                return CourseWebinarPanel.this.getString("no.webinars");
            }
        };

        add(grid.setOutputMarkupPlaceholderTag(true));
        add(webinarItemPanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(ResourcesUtils.CSS_NEW_TABLE_STYLE);
    }
}
