package com.eltiland.ui.course.components;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.bl.GenericManager;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.impl.integration.IndexCreator;
import com.eltiland.model.course2.AuthorCourse;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.file.File;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.dialog.Dialog;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.course.CourseNewContentPage;
import com.eltiland.ui.course.CourseNewPage;
import com.eltiland.ui.course.components.panels.CourseInvoicePanel;
import com.eltiland.ui.worktop.simple.ProfileViewPage;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Course icon panel.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseIconPanel extends BaseEltilandPanel<ELTCourse> {

    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private EmailMessageManager emailMessageManager;
    @SpringBean
    private IndexCreator indexCreator;
    @SpringBean
    private ELTCourseListenerManager courseListenerManager;

    private final String CSS = "static/css/panels/course_iconpanel.css";

    private Dialog<CourseInvoicePanel> invoicePanelDialog = new Dialog<CourseInvoicePanel>("invoicePanel", 445) {
        @Override
        public CourseInvoicePanel createDialogPanel(String id) {
            return new CourseInvoicePanel(id, getModel());
        }

        @Override
        public void registerCallback(CourseInvoicePanel panel) {
            super.registerCallback(panel);
            panel.setNewCallback(new IDialogNewCallback.IDialogActionProcessor<ELTCourseListener>() {
                @Override
                public void process(IModel<ELTCourseListener> model, AjaxRequestTarget target) {
                    close(target);
                }
            });
        }
    };

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private IModel<Boolean> isListenerModel = new LoadableDetachableModel<Boolean>() {
        @Override
        protected Boolean load() {
            if (currentUserModel.getObject() == null) {
                return false;
            } else {
                ELTCourseListener listener = courseListenerManager.getItem(currentUserModel.getObject(),
                        CourseIconPanel.this.getModelObject());
                return listener != null && listener.getStatus().equals(PaidStatus.CONFIRMED);
            }
        }
    };


    /**
     * Panel constructor.
     *
     * @param id           markup id
     * @param courseIModel course model.
     */
    public CourseIconPanel(String id, final IModel<ELTCourse> courseIModel) {
        super(id, courseIModel);

        ELTCourse course = courseIModel.getObject();
        genericManager.initialize(course, course.getIcon());
        genericManager.initialize(course, course.getAuthor());

        add(new CourseFileIconPanel("imagePanel", new GenericDBModel<>(
                File.class, course.getIcon()), new Model<>(courseIModel.getObject().getName()))
                .add(new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        throw new RestartResponseException(CourseNewPage.class, new PageParameters().add(
                                CourseNewPage.PARAM_ID, CourseIconPanel.this.getModelObject().getId()));
                    }
                }));

        final boolean isTraining = course instanceof TrainingCourse;
        final boolean isModule = isTraining ? false : ((AuthorCourse) course).isModule();
        Label label = new Label("label", new Model<String>());
        label.setDefaultModelObject(
                getString(isTraining ? "trainingCourse" : (isModule ? "moduleCourse" : "authorCourse")));
        if (isTraining) {
            label.add(new AttributeAppender("class", new Model<>("training_label"), " "));
        }
        add(label);

        Label name = new Label("name", getModelObject().getName());
        add(name);

        Label author = new Label("author", getModelObject().getAuthor().getName());
        add(author);
        author.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RestartResponseException(ProfileViewPage.class,
                        new PageParameters().add(ProfileViewPage.PARAM_ID,
                                CourseIconPanel.this.getModelObject().getAuthor().getId()));
            }
        });

        WebMarkupContainer aboutContainer = new WebMarkupContainer("about");
        aboutContainer.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                throw new RestartResponseException(CourseNewPage.class, new PageParameters().add(
                        CourseNewPage.PARAM_ID, CourseIconPanel.this.getModelObject().getId()));
            }
        });
        add(aboutContainer);

        WebMarkupContainer registerButton = new WebMarkupContainer("register") {
            @Override
            public boolean isVisible() {
                if (currentUserModel.getObject() == null) {
                    return false;
                } else {
                    if (isListenerModel.getObject()) {
                        return true;
                    } else {
                        ELTCourse tCourse = CourseIconPanel.this.getModelObject();
                        if (tCourse instanceof TrainingCourse) {
                            return ((TrainingCourse) tCourse).isOpen();
                        } else {
                            return tCourse.isNeedConfirm();
                        }
                    }
                }
            }
        };

        registerButton.add(new Label("caption", getString(isListenerModel.getObject() ? "already" : "register")));
        registerButton.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (isListenerModel.getObject()) {
                    throw new RestartResponseException(CourseNewContentPage.class, new PageParameters()
                            .add(CourseNewContentPage.PARAM_ID, getModelObject().getId())
                            .add(CourseNewContentPage.PARAM_VERSION, CourseNewContentPage.FULL_VERSION));
                } else {
                    invoicePanelDialog.show(target);
                }
            }
        });

        WebMarkupContainer trainingContainer = new WebMarkupContainer("trainingInfo");
        trainingContainer.setVisible(isTraining);

        if (isTraining) {
            String labelText = String.format(getString("teach"),
                    DateUtils.formatDate(((TrainingCourse) getModelObject()).getStartDate()),
                    DateUtils.formatDate(((TrainingCourse) getModelObject()).getFinishDate()));

            boolean isOpen = ((TrainingCourse) getModelObject()).isOpen();

            labelText += ", " + (isOpen ? String.format(getString("join.on"),
                    DateUtils.formatDate(((TrainingCourse) getModelObject()).getJoinDate())) :
                    getString("join.off"));
            Label join = new Label("join",labelText);
            join.add(new AttributeAppender("class", new Model<>(isOpen ? "opened" : "closed"), " "));
            trainingContainer.add(join);
        }

        add(trainingContainer);
        add(registerButton);
        add(invoicePanelDialog);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }
}
