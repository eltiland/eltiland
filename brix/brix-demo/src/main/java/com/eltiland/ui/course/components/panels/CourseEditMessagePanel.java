package com.eltiland.ui.course.components.panels;

import com.eltiland.bl.EmailMessageManager;
import com.eltiland.exceptions.EmailException;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogSimpleNewCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for editing email message.
 *
 * @author Aleksey Plotnikov.
 */
public class CourseEditMessagePanel extends ELTDialogPanel implements IDialogSimpleNewCallback<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseEditMessagePanel.class);

    @SpringBean
    private EmailMessageManager emailMessageManager;

    private IDialogActionProcessor<String> callback;
    private String courseName, recepientEmail;
    private boolean isAuthor = false;

    private ELTTextArea mailText = new ELTTextArea(
            "mailText", new ResourceModel("emailLabel"), new Model<String>(), true) {
        @Override
        protected int getInitialHeight() {
            return 200;
        }

        @Override
        protected boolean isFillToWidth() {
            return true;
        }
    };

    /**
     * Panel constructor.
     *
     * @param id markup id.
     */
    public CourseEditMessagePanel(String id) {
        super(id);
        form.add(mailText);
    }

    /**
     * Panel initialization.
     *
     * @param courseName     name of the course.
     * @param recepientEmail email of the recepient.
     * @param isAuthor       if true - it will be message to the author of the course.
     */
    public void initData(String courseName, String recepientEmail, boolean isAuthor) {
        mailText.setModelObject(null);
        this.courseName = courseName;
        this.recepientEmail = recepientEmail;
        this.isAuthor = isAuthor;
    }

    @Override
    protected String getHeader() {
        return getString(isAuthor ? "headerAuthor" : "headerSupport");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Send));
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Send)) {
            try {
                emailMessageManager.sendCourseMessage(courseName, mailText.getModelObject(), recepientEmail);
            } catch (EmailException e) {
                LOGGER.error("Cannot send email", e);
                throw new WicketRuntimeException("Cannot send email", e);
            }
            callback.process(mailText.getModel(), target);
        }
    }

    @Override
    protected boolean showButtonDecorator() {
        return true;
    }

    @Override
    public void setSimpleNewCallback(IDialogActionProcessor<String> callback) {
        this.callback = callback;
    }
}
