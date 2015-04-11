package com.eltiland.ui.faq.plugin.components;

import com.eltiland.bl.FaqApprovalManager;
import com.eltiland.bl.FaqManager;
import com.eltiland.bl.MailSender;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.FaqException;
import com.eltiland.model.EmailMessage;
import com.eltiland.model.faq.FaqApproval;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogConfirmCallback;
import com.eltiland.ui.common.components.textfield.ELTTextArea;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Panel for answering the question by user.
 *
 * @author Aleksey PLotnikov.
 */
public class AnswerQuestionPanel extends ELTDialogPanel implements IDialogConfirmCallback {
    @SpringBean
    private MailSender mailSender;
    @SpringBean(name = "mailMessageHeadings")
    Properties mailHeadings;
    @SpringBean
    private FaqManager faqManager;
    @SpringBean
    private FaqApprovalManager faqApprovalManager;

    private IModel<FaqApproval> faqApprovalModel = new GenericDBModel<>(FaqApproval.class);


    private IDialogActionProcessor callback;

    private ELTTextArea aEditor = new ELTTextArea("aEditor", new ResourceModel("aLabel"), new Model<String>(), true);
    private ELTTextArea qEditor = new ELTTextArea("qEditor", new ResourceModel("qLabel"), new Model<String>(), true);

    /**
     * Panel constructor.
     *
     * @param id panel's ID.
     */
    public AnswerQuestionPanel(String id) {
        super(id, new GenericDBModel<>(FaqApproval.class));

        aEditor.addMaxLengthValidator(2048);
        qEditor.addMaxLengthValidator(2048);

        form.add(aEditor);
        form.add(qEditor);
    }

    @Override
    protected String getHeader() {
        return getString("answerHeader");
    }

    @Override
    protected List<EVENT> getActionList() {
        return Arrays.asList(EVENT.Save);
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        switch (event) {
            case Save:
                FaqApproval obj = faqApprovalModel.getObject();
                obj.setQuestion(qEditor.getModelObject());
                obj.setAnswer(aEditor.getModelObject());
                obj.setAnswered(!obj.getAnswer().isEmpty());
                try {
                    faqApprovalManager.update(obj);
                } catch (FaqException e) {
                    ELTAlerts.renderErrorPopup(e.getMessage(), target);
                    return;
                }

                // send email to user, that his question answered
                if (!obj.getUserEMail().isEmpty()) {
                    try {
                        EmailMessage message = new EmailMessage();
                        message.setSender(new InternetAddress(
                                mailHeadings.getProperty("robotFromEmail"),
                                mailHeadings.getProperty("robotFromName"),
                                "UTF-8"));

                        message.setSubject(mailHeadings.getProperty("questionAnswered"));
                        message.setRecipients(Arrays.asList(new InternetAddress(obj.getUserEMail())));
                        message.setText(obj.getAnswer());
                        mailSender.sendMessage(message);
                    } catch (UnsupportedEncodingException | AddressException | EmailException e) {
                        ELTAlerts.renderErrorPopup(getString("emailSendError"), target);
                    }
                }

                callback.process(target);

                break;
        }
    }

    public void initQuestion(FaqApproval faqApproval) {
        setModelObject(faqApproval);
        faqApprovalModel.setObject(faqApproval);
        aEditor.setModelObject(null);
        aEditor.setConvertedInput(null);
        qEditor.setModelObject(faqApproval.getQuestion());
    }

    @Override
    public void setConfirmCallback(IDialogActionProcessor callback) {
        this.callback = callback;
    }
}
