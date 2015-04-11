package com.eltiland.bl.impl;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.MailSender;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.exceptions.EmailException;
import com.eltiland.model.EmailMessage;
import com.eltiland.model.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author knorr
 * @version 1.0
 * @since 7/24/12
 */
@Component
public class MailSenderImpl implements MailSender {

    private final static Logger LOGGER = LoggerFactory.getLogger(MailSenderImpl.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private FileUtility fileUtility;

    @Override
    public void sendMessage(final EmailMessage message) throws EmailException {
        MimeMessagePreparator preparator = getPreparator(message);
        try {
            mailSender.send(preparator);
        } catch (MailException ex) {
            LOGGER.error(ex.getMessage());
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }
    }

    @Override
    public void sendMessages(EmailMessage[] messages) throws EmailException {
        List<MimeMessagePreparator> preparatorList = new ArrayList<>();

        for (EmailMessage message : messages) {
            preparatorList.add(getPreparator(message));
        }

        MimeMessagePreparator[] preparators = preparatorList.toArray(new MimeMessagePreparator[preparatorList.size()]);
        try {
            mailSender.send(preparators);
        } catch (MailException ex) {
            LOGGER.error("Mail sending exception.", ex);
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }
    }

    private MimeMessagePreparator getPreparator(final EmailMessage message) {
        return new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                messageHelper.setSubject(message.getSubject());
                messageHelper.setFrom(message.getSender());

                //Set recipients
                for (InternetAddress to : message.getRecipients()) {
                    messageHelper.addTo(to);
                }

                // Set CCs
                if (!message.getCc().isEmpty()) {
                    for (InternetAddress cc : message.getCc()) {
                        messageHelper.addCc(cc);
                    }
                }

                // Set BCCs
                if (!message.getDcc().isEmpty()) {
                    for (InternetAddress dcc : message.getDcc()) {
                        messageHelper.addBcc(dcc);
                    }
                }

                //Set message text
                messageHelper.setText(message.getText(), true);

                /**
                 * Attachment file
                 */

                for (FileContent fileContent : message.getFileContentList()) {
                    messageHelper.addAttachment(
                            fileContent.getPath(),
                            new ByteArrayResource(fileContent.getContent(),
                                    fileContent.getType()
                            ));
                }
            }
        };
    }
}
