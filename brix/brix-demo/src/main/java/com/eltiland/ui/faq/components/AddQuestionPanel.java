package com.eltiland.ui.faq.components;

import com.eltiland.bl.FaqApprovalManager;
import com.eltiland.bl.MailSender;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.EmailMessage;
import com.eltiland.model.faq.FaqApproval;
import com.eltiland.model.user.User;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogCloseCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.components.textfield.ELTTextEmailField;
import com.eltiland.utils.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Panel for creating new question by user.
 *
 * @author Aleksey PLotnikov.
 */
public class AddQuestionPanel extends ELTDialogPanel implements IDialogCloseCallback {
    private IDialogCloseCallback.IDialogActionProcessor callback;

    private IModel<User> currentUserModel = new LoadableDetachableModel<User>() {
        @Override
        protected User load() {
            return EltilandSession.get().getCurrentUser();
        }
    };

    private ELTTextEmailField emailField = new ELTTextEmailField(
            "emailField", new ResourceModel("emailLabel"), new Model<String>(), true);
    private ELTTextArea qEditor = new ELTTextArea("qEditor", new ResourceModel("qLabel"), new Model<String>(), true);

    @SpringBean(name = "eltilandProperties")
    private Properties eltilandProps;

    @SpringBean(name = "mailMessageHeadings")
    Properties mailHeadings;

    @SpringBean
    private MailSender mailSender;

    @SpringBean
    private FaqApprovalManager faqApprovalManager;

    private boolean isLogined() {
        return currentUserModel.getObject() != null;
    }

    /**
     * Panel constructor.
     *
     * @param id panel's ID.
     */
    public AddQuestionPanel(String id) {
        super(id);

        form.add(emailField);
        emailField.setVisible(!isLogined());

        form.add(qEditor);
        qEditor.addMaxLengthValidator(2048);
    }

    @Override
    protected String getHeader() {
        return getString("header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Send));
    }

    private void sendQuestionToAdmin(AjaxRequestTarget target) {
        String email = isLogined() ? currentUserModel.getObject().getEmail() : emailField.getModelObject();

        FaqApproval approval = new FaqApproval();
        approval.setQuestion(qEditor.getModelObject());
        approval.setCreationDate(DateUtils.getCurrentDate());
        approval.setUserEMail(email);
        try {
            faqApprovalManager.create(approval);
        } catch (FaqException e) {
            ELTAlerts.renderErrorPopup(e.getMessage(), target);
            return;
        }

        try {
            EmailMessage message = new EmailMessage();
            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("questionCreated"));
            message.setRecipients(Arrays.asList(new InternetAddress(eltilandProps.getProperty("administrator.email"))));
            message.setText(String.format(getString("emailText"), approval.getUserEMail()));
            mailSender.sendMessage(message);
        } catch (UnsupportedEncodingException | AddressException | EmailException e) {
            ELTAlerts.renderErrorPopup(getString("emailSendError"), target);
        }
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Send)) {
            callback.process(target);
            sendQuestionToAdmin(target);
        }
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        qEditor.setModelObject(null);
    }

    @Override
    protected boolean showCaptcha() {
        return true;
    }

    @Override
    public void setCloseCallback(IDialogActionProcessor callback) {
        this.callback = callback;
    }
}
