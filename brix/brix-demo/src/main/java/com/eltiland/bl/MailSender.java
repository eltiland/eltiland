package com.eltiland.bl;

import com.eltiland.exceptions.EmailException;
import com.eltiland.model.EmailMessage;

/**
 * @author knorr
 * @version 1.0
 * @since 7/24/12
 */
public interface MailSender {

    /**
     * Perform actual send message (connect via SMTP and push message to its addressees).
     * <p/>
     * The message should be filled in correctly.
     * <p/>
     * senderName, senderEmailAddress, recipientName, recipientEmailAddress, ccAddresses, actual getContent(), attachments -
     * everything should be filled in prior to performing this method.
     * @param message message object  to send.
     * @throws com.eltiland.exceptions.EmailException
     */
    void sendMessage(EmailMessage message) throws EmailException;

    void sendMessages(EmailMessage messages[]) throws EmailException;

}
