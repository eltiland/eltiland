package com.eltiland.bl.impl;

import com.eltiland.bl.*;
import com.eltiland.bl.course.ELTCourseListenerManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.pdf.WebinarCertificateGenerator;
import com.eltiland.bl.user.ConfirmationManager;
import com.eltiland.bl.user.ResetPassManager;
import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.exceptions.EmailException;
import com.eltiland.exceptions.VelocityCommonException;
import com.eltiland.model.EmailMessage;
import com.eltiland.model.FileContent;
import com.eltiland.model.Pei;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.TrainingCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.file.File;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.magazine.Magazine;
import com.eltiland.model.magazine.MagazineData;
import com.eltiland.model.payment.PaidStatus;
import com.eltiland.model.subscribe.Email;
import com.eltiland.model.subscribe.Subscriber;
import com.eltiland.model.user.Confirmation;
import com.eltiland.model.user.ResetCode;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;
import com.eltiland.session.EltilandSession;
import com.eltiland.ui.paymentnew.PaymentPage;
import com.eltiland.utils.DateUtils;
import com.eltiland.utils.MimeType;
import com.eltiland.utils.UrlUtils;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author knorr
 * @version 1.0
 * @since 7/25/12
 */
@Component
public class EmailMessageManagerImpl implements EmailMessageManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailMessageManagerImpl.class);

    @Autowired
    private VelocityMergeTool velocityMergeTool;
    @Autowired
    private GenericManager genericManager;
    @Autowired
    private ConfirmationManager confirmationManager;
    @Autowired
    private ResetPassManager resetPassManager;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private EmailManager emailManager;
    @Autowired
    private SubscriberManager subscriberManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private FileUtility fileUtility;
    @Autowired
    private CourseListenerManager courseListenerManager;
    @Autowired
    private CourseDocumentManager courseDocumentManager;
    @Autowired
    private WebinarUserPaymentManager webinarUserPaymentManager;
    @Autowired
    private WebinarCertificateGenerator webinarCertificateGenerator;
    @Autowired
    private ELTCourseListenerManager eltCourseListenerManager;


    @Autowired
    @Qualifier("eltilandProperties")
    private Properties eltilandProps;

    @Autowired
    @Qualifier("mailMessageHeadings")
    Properties mailHeadings;

    // Keys in email
    private final static String PEI_NAME = "pei_name";
    private final static String PEI_ADDRESS = "pei_address";
    private final static String PORTAL_URL = "portal_url";
    private final static String USER_NAME = "user_name";
    private final static String USER_NAMES = "user_names";
    private final static String USER_EMAIL = "user_email";
    private final static String SECRET_LINK = "secret_link";
    private final static String RESET_LINK = "reset_link";
    private final static String ENDING_DATE = "ending_date";
    private final static String ADMIN_EMAIL = "admin_email";
    private final static String PASSWORD = "password";
    private final static String FILE_NAME = "file_name";
    private final static String TEMPLATE_REG_SIMPLE_USER = "templates/regSimpleUser.fo";
    private final static String TEMPLATE_CONFIRM_USER = "templates/confirmationMessage.fo";

    private final static String COURSE_INVOICE_TO_ADMIN = "templates/course/courseInvoiceToAdmin.fo";
    private final static String COURSE_APPLY_TO_USER = "templates/course/courseApplyToUser.fo";
    private final static String COURSE_DENY_TO_USER = "templates/course/courseDenyToUser.fo";
    private final static String COURSE_ACCESS_INVOICE = "templates/course/courseAccessInvoiceToAdmin.fo";
    private final static String COURSE_INVOICE_DENIED = "templates/course/courseInvoiceDeniedToUser.fo";
    private final static String COURSE_INVOICE_GRANTED = "templates/course/courseInvoiceGrantedToUserFree.fo";
    private final static String COURSE_INVOICE_GRANTED_PAID = "templates/course/courseInvoiceGrantedToUserPaid.fo";
    private final static String COURSE_MESSAGE = "templates/course/courseMessage.fo";
    private final static String COURSE_INVOICE_MESSAGE = "templates/course/courseInvoiceMessage.fo";
    private final static String COURSE_CONFIRMED_MESSAGE = "templates/course/courseConfirmedMessage.fo";
    private final static String COURSE_AUTHOR_PAYMENT = "templates/course/coursePayMessageToAdmin.fo";

    private final static String COURSE_TRAINING_INVOICE_USER = "templates/course/training/courseInvoiceToUser.fo";
    private final static String COURSE_TRAINING_INVOICE_ADMIN = "templates/course/training/courseAccessInvoiceToAdmin.fo";
    private final static String COURSE_TRAINING_ACCEPTED_INVOICE = "templates/course/training/courseInvoiceAcceptedToUser.fo";
    private final static String COURSE_TRAINING_DECLINED_INVOICE = "templates/course/training/courseInvoiceDeclinedToUser.fo";
    private final static String COURSE_TRAINING_PAYACCEPTED_INVOICE = "templates/course/training/courseInvoicePayAcceptedToUser.fo";
    private final static String COURSE_TRAINING_PAID = "templates/course/training/courseListenerToUser.fo";

    private final static String SUBSCRIBE_TEMPLATE = "templates/subscribe.fo";

    private final static String RECORD_INVITATION_TO_USER = "templates/webinars/records/userInvitationToRecord.fo";
    private final static String RECORD_LINK_TO_USER = "templates/webinars/records/userLinkToRecord.fo";
    private final static String RECORD_FREE_INVITATION_TO_USER =
            "templates/webinars/records/userFreeInvitationToRecord.fo";

    private final static String WEBINAR_INVITATION_TO_USER = "templates/webinars/userInvitationToWebinar.fo";
    private final static String WEBINAR_MULTIPLY_INVITATION_TO_USER =
            "templates/webinars/userMultiplyInvitationToWebinar.fo";
    private final static String WEBINAR_FREE_INVITATION_TO_USER = "templates/webinars/userFreeInvitationToWebinar.fo";
    private final static String WEBINAR_MANAGER_APPLY = "templates/webinars/webinarManagerApply.fo";
    private final static String WEBINAR_DENY_TO_USER = "templates/webinars/webinarUserDeny.fo";
    private final static String WEBINAR_CREATE_TO_ADMIN = "templates/webinars/webinarCreateToAdmin.fo";
    private final static String WEBINAR_CHANGEPRICE_TO_USER = "templates/webinars/userChangePriceWebinar.fo";
    private final static String WEBINAR_CHANGEPRICE_TO_FREE_TO_USER =
            "templates/webinars/userChangePriceToFreeWebinar.fo";
    private final static String WEBINAR_CHANGEROLE_TO_USER = "templates/webinars/webinarChangeRole.fo";
    private final static String WEBINAR_CHANGEROLEADMIN_TO_USER = "templates/webinars/webinarChangeRoleToModerator.fo";
    private final static String WEBINAR_CERTIFICATE_TO_USER = "templates/webinars/webinarCertificateToUser.fo";
    private final static String WEBINAR_MESSAGE_TO_USER = "templates/webinars/webinarMessage.fo";
    private final static String WEBINAR_MESSAGE_WITH_RECORDS_TO_USER = "templates/webinars/webinarMessageWithRecords.fo";

    private final static String MAGAZINE_DOWNLOAD = "templates/magazine/downloadMagazine.fo";
    private final static String MAGAZINE_INFO_TO_ADMIN = "templates/magazine/adminNotifyMagazineBuy.fo";

    private final static String FILE_UPLOAD = "templates/file/fileUpload.fo";
    private final static String FILE_COURSE_UPLOAD = "templates/file/fileUploadCourse.fo";

    private final static String COURSE_NAME = "course_name";
    private final static String COURSE_PAY_LINK = "course_pay_link";

    private final static String REQUISITES = "requisites";
    private final static String START_DATE = "start_date";

    private final static String WEBINAR_NAME = "webinar_name";
    private final static String WEBINAR_DESCRIPTION = "webinar_description";
    private final static String WEBINAR_STARTDATE = "webinar_startdate";
    private final static String WEBINAR_PAYLINK = "pay_link";
    private final static String WEBINAR_PRICE = "webinar_price";
    private final static String WEBINAR_ROLE = "webinar_role";

    private final static String RECORD_LINKS = "record_links";

    private final static String SUBSCRIBE_TEXT = "mail_text";
    private final static String SUBSCRIBE_UNSUBSCRIBE = "unsubscribe_link";

    private final static String DOWNLOAD_LINK = "download_link";
    private final static String MAGAZINE_LIST = "magazines";
    private final static String FULL_PRICE = "fullprice";

    private final static String MESSAGE_TEXT = "message_text";

    @Override
    public void sendSubscribeToUser(Email email, String userMail) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        model.put(SUBSCRIBE_TEXT, email.getContent().replace("resource/",
                eltilandProps.getProperty("application.base.url") + "/resource/"));

        EmailMessage message = new EmailMessage();
        try {
            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }
        message.setSubject(email.getHeader());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(userMail);
        } catch (AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }
        message.setRecipients(Arrays.asList(recipient));

        String messageBody;
        try {
            messageBody = velocityMergeTool.mergeTemplate(model, SUBSCRIBE_TEMPLATE);
        } catch (VelocityCommonException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }

        message.setText(messageBody);
        mailSender.sendMessage(message);
    }

    @Override
    public void sendEmailToUserRegistered(User user) throws EmailException {
        Map<String, Object> model = new HashMap<String, Object>();

        model.put(PORTAL_URL, createPath(null, null));

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(user.getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, TEMPLATE_REG_SIMPLE_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("parentConfirmationEmail"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }

    }

    @Override
    public void sendEmailConfirmationToUser(User user) throws EmailException {
        genericManager.initialize(user, user.getConfirmation());
        Confirmation confirmation = user.getConfirmation();

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(SECRET_LINK, createPath(confirmation, UrlUtils.LOGIN_PAGE_MOUNT_PATH));
        try {
            sendRegistrationConfirmationEmail(model,
                    new InternetAddress(user.getEmail()),
                    Confirmation.SIMPLE_USER_TEMPLATE_LETTER,
                    mailHeadings.getProperty("userConfirmation"), null);
        } catch (EmailException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendConfirmationMessage(String email, String pass) throws EmailException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(PASSWORD, pass);
        try {
            InternetAddress recipient = new InternetAddress(email);

            String messageBody = velocityMergeTool.mergeTemplate(model, TEMPLATE_CONFIRM_USER);
            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("toUserAutoConfirm"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (EmailException | AddressException | UnsupportedEncodingException | VelocityCommonException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendEmailToPeiRegistrationApproved(Pei pei) throws AddressException, EmailException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(PEI_NAME, pei.getName());
        model.put(PEI_ADDRESS, pei.getAddress());
        model.put(PORTAL_URL, createPath(null, null));

        sendRegistrationConfirmationEmail(model, new InternetAddress(pei.getEmail()), Confirmation.PEI_TEMPLATE_LETTER,
                mailHeadings.getProperty("peiRegistrationConfirmed"), null);
    }

    @Override
    public void sendEmailToUserResetPasswordRequest(User user) throws AddressException, EmailException {
        ResetCode resetCode = getResetCode(user);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(RESET_LINK, createResetPath(resetCode, UrlUtils.RESET_PAGE_MOUNT_PATH));
        model.put(ENDING_DATE, resetCode.getEndingDate());

        sendRegistrationConfirmationEmail(model, new InternetAddress(user.getEmail()), ResetCode.RESET_PASS_LETTER,
                mailHeadings.getProperty("resetPasswordRequest"), null);
    }

    @Override
    public void sendFileUploadMessage(User author, User receiver, File file) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(USER_NAME, author.getName());
        model.put(FILE_NAME, file.getName());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(receiver.getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, FILE_UPLOAD);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("fileUpload"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendFileCourseUploadMessage(User author, ELTCourse course, File file) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(USER_NAME, author.getName());
        model.put(FILE_NAME, file.getName());
        model.put(COURSE_NAME, course.getName());

        genericManager.initialize(course, course.getAdmins());
        List<InternetAddress> recipients = new ArrayList<>();
        try {
            genericManager.initialize(course, course.getAuthor());
            recipients.add(new InternetAddress(course.getAuthor().getEmail()));

            for (User admin : course.getAdmins()) {
                recipients.add(new InternetAddress(admin.getEmail()));
            }

            String messageBody = velocityMergeTool.mergeTemplate(model, FILE_COURSE_UPLOAD);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("fileUploadCourse"));
            message.setRecipients(recipients);
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendInvoiceCreateCourseToAdmin(User author, Course course) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(USER_NAME, author.getName());
        model.put(COURSE_NAME, course.getName());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(eltilandProps.getProperty("administrator.email"));

            String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_INVOICE_TO_ADMIN);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseInvoiceToCreate"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendEmailApplyCourseToUser(ELTCourse course) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        model.put(COURSE_NAME, course.getName());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(course.getAuthor().getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_APPLY_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseApplyToUser"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendEmailDenyCourseToUser(ELTCourse course) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        model.put(COURSE_NAME, course.getName());
        model.put(ADMIN_EMAIL, eltilandProps.getProperty("administrator.email"));

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(course.getAuthor().getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_DENY_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseDenyToUser"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendSubscribeToCurrentUser(Email email) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        model.put(SUBSCRIBE_TEXT, email.getContent().replace("resource/",
                eltilandProps.getProperty("application.base.url") + "/resource/"));

        EmailMessage message = new EmailMessage();
        try {
            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
        message.setSubject(email.getHeader());

        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser == null) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR);
        }

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(currentUser.getEmail());
        } catch (AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
        message.setRecipients(Arrays.asList(recipient));

        String messageBody;
        try {
            messageBody = velocityMergeTool.mergeTemplate(model, SUBSCRIBE_TEMPLATE);
        } catch (VelocityCommonException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }

        message.setText(messageBody);
        mailSender.sendMessage(message);
    }

    @Override
    public void sendSubscribe(Email email) throws EmailException {

        Map<String, Object> model = new HashMap<>();
        model.put(SUBSCRIBE_TEXT, email.getContent().replace("resource/",
                eltilandProps.getProperty("application.base.url") + "/resource/"));

        EmailMessage message = new EmailMessage();
        try {
            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
        message.setSubject(email.getHeader());

        for (Subscriber subscriber : subscriberManager.getActiveSubscriberList()) {
            model.put(SUBSCRIBE_UNSUBSCRIBE, createUnsubscribePath(subscriber, UrlUtils.UNSUBSCRIBE_PAGE_MOUNT_PATH));

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(subscriber.getEmail());
            } catch (AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
            message.setRecipients(Arrays.asList(recipient));

            String messageBody;
            try {
                messageBody = velocityMergeTool.mergeTemplate(model, SUBSCRIBE_TEMPLATE);
            } catch (VelocityCommonException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }

            message.setText(messageBody);
            mailSender.sendMessage(message);
        }
        email.setStatus(true);
        email.setSendDate(DateUtils.getCurrentDate());
        emailManager.updateEmail(email);
    }

    @Override
    public void sendDownloadMagazineLink(Client client) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        model.put(DOWNLOAD_LINK, createDownloadMagazineLink(client, UrlUtils.MAGAZINE_DOWNLOAD_LINK));

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(client.getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, MAGAZINE_DOWNLOAD);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("magazineDownload"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendMagazineActionToAdmin(Client client) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        List<MagazineData> magazineDataList = new ArrayList<>();
        genericManager.initialize(client, client.getMagazines());

        BigDecimal fullPrice = BigDecimal.valueOf(0);
        for (Magazine magazine : client.getMagazines()) {
            MagazineData data = new MagazineData();
            data.setName(magazine.getName());
            data.setPrice(magazine.getPrice());
            fullPrice = fullPrice.add(magazine.getPrice());
            magazineDataList.add(data);
        }

        model.put(MAGAZINE_LIST, magazineDataList);
        model.put(FULL_PRICE, fullPrice.toString());
        model.put(USER_NAME, client.getName());
        model.put(USER_EMAIL, client.getEmail());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(eltilandProps.getProperty("magazine.administrator.email"));

            String messageBody = velocityMergeTool.mergeTemplate(model, MAGAZINE_INFO_TO_ADMIN);
            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("magazineBuy"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);
            mailSender.sendMessage(message);
        } catch (AddressException | VelocityCommonException | UnsupportedEncodingException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendCourseAccessInvoiceToAdmin(CourseInvoice invoice) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        genericManager.initialize(invoice, invoice.getCourse());
        genericManager.initialize(invoice, invoice.getListener());
        model.put(COURSE_NAME, invoice.getCourse().getName());
        model.put(USER_NAME, invoice.getListener().getName());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(eltilandProps.getProperty("administrator.email"));

            String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_ACCESS_INVOICE);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseAccessInvoice"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendCourseAccessGrantedToUser(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        genericManager.initialize(listener, listener.getCourse());
        boolean isFree = listener.getCourse().getPrice() == null ||
                listener.getCourse().getPrice().equals(BigDecimal.ZERO);
        model.put(COURSE_NAME, listener.getCourse().getName());
        if (!isFree) {
            model.put(COURSE_PAY_LINK, createCoursePayPath(listener, UrlUtils.PAYMENT_LINK));
        }

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(listener.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(
                    model, isFree ? COURSE_INVOICE_GRANTED : COURSE_INVOICE_GRANTED_PAID);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseInvoiceGranted"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendCourseAccessDeniedToUser(CourseInvoice invoice) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        genericManager.initialize(invoice, invoice.getCourse());
        genericManager.initialize(invoice, invoice.getListener());
        model.put(COURSE_NAME, invoice.getCourse().getName());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(invoice.getListener().getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_INVOICE_DENIED);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("courseInvoiceDenied"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendCourseMessage(String courseName, String message, String recepientEmail) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser != null) {
            model.put(COURSE_NAME, courseName);
            model.put(USER_NAME, currentUser.getName());
            model.put(USER_EMAIL, currentUser.getEmail());
            model.put(MESSAGE_TEXT, message);

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(recepientEmail);

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_MESSAGE);

                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("courseMessage"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendCourseListenerMessage(
            ELTCourse course, String messageText, boolean isConfirmed) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        if (course != null) {
            List<ELTCourseListener> listeners = eltCourseListenerManager.getList(course, isConfirmed, true);
            if (listeners != null && !(listeners.isEmpty())) {
                model.put(MESSAGE_TEXT, messageText);
                model.put(COURSE_NAME, course.getName());
                for (ELTCourseListener listener : listeners) {
                    InternetAddress recipient;
                    try {
                        genericManager.initialize(listener, listener.getListener());
                        // sending message to listener
                        recipient = new InternetAddress(listener.getListener().getEmail());

                        String messageBody = velocityMergeTool.mergeTemplate(model,
                                isConfirmed ? COURSE_CONFIRMED_MESSAGE : COURSE_INVOICE_MESSAGE);
                        EmailMessage email = new EmailMessage();

                        email.setSender(new InternetAddress(
                                mailHeadings.getProperty("robotFromEmail"),
                                mailHeadings.getProperty("robotFromName"),
                                "UTF-8"));

                        email.setSubject(mailHeadings.getProperty("courseListenerMessage"));
                        email.setRecipients(Arrays.asList(recipient));
                        email.setText(messageBody);

                        mailSender.sendMessage(email);
                    } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                        throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
                    }
                }
            }
        }
    }

    @Override
    public void sendCourseListenerMessage(ELTCourseListener listener, String messageText) throws EmailException {
        if (listener != null) {
            genericManager.initialize(listener, listener.getCourse());
            genericManager.initialize(listener, listener.getListener());

            Map<String, Object> model = new HashMap<>();
            model.put(MESSAGE_TEXT, messageText);
            model.put(COURSE_NAME, listener.getCourse().getName());
            InternetAddress recipient;
            try {
                // sending message to listener
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model,
                        listener.getStatus().equals(PaidStatus.CONFIRMED)
                                ? COURSE_CONFIRMED_MESSAGE : COURSE_INVOICE_MESSAGE);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("courseListenerMessage"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendCoursePayMessage(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getCourse());
            genericManager.initialize(listener.getCourse(), listener.getCourse().getAuthor());

            model.put(USER_NAME, listener.getName());
            model.put(COURSE_NAME, listener.getCourse().getName());

            InternetAddress recipient;
            try {
                // sending message to author
                recipient = new InternetAddress(listener.getCourse().getAuthor().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_AUTHOR_PAYMENT);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("coursePayMessage"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserInvoicePhysical(CourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getSession());
            genericManager.initialize(listener.getSession(), listener.getSession().getCourse());
            genericManager.initialize(listener, listener.getListener());
            model.put(COURSE_NAME, listener.getSession().getCourse().getName());

            InternetAddress recipient;
            try {

                // sending message to listener
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_INVOICE_USER);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingCourseInvoice"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserInvoiceToAdminPhysical(CourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getSession());
            genericManager.initialize(listener.getSession(), listener.getSession().getCourse());
            genericManager.initialize(listener.getSession().getCourse(), listener.getSession().getCourse().getAuthor());
            genericManager.initialize(listener, listener.getListener());
            model.put(COURSE_NAME, listener.getSession().getCourse().getName());
            model.put(USER_NAME, listener.getListener().getName());

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(listener.getSession().getCourse().getAuthor().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_INVOICE_ADMIN);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingCourseInvoice"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserAccepted(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getCourse());
            genericManager.initialize(listener, listener.getListener());
            model.put(COURSE_NAME, listener.getCourse().getName());

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_ACCEPTED_INVOICE);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingInvoiceAccepted"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserDeclined(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getCourse());
            model.put(COURSE_NAME, listener.getCourse().getName());

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_DECLINED_INVOICE);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingInvoiceDeclined"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserPayAccepted(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            genericManager.initialize(listener, listener.getCourse());
            genericManager.initialize(listener, listener.getListener());
            model.put(COURSE_NAME, listener.getCourse().getName());
            model.put(REQUISITES, listener.getRequisites());

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_PAYACCEPTED_INVOICE);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingInvoicePayAccepted"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendTCUserPaid(ELTCourseListener listener) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        if (listener != null) {
            listener = eltCourseListenerManager.getById(listener.getId());
            model.put(COURSE_NAME, listener.getCourse().getName());
            model.put(START_DATE, DateUtils.formatRussianDate(((TrainingCourse) listener.getCourse()).getStartDate()));

            InternetAddress recipient;
            try {
                recipient = new InternetAddress(listener.getListener().getEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model, COURSE_TRAINING_PAID);
                EmailMessage email = new EmailMessage();

                email.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                email.setSubject(mailHeadings.getProperty("trainingListenerAccepted"));
                email.setRecipients(Arrays.asList(recipient));
                email.setText(messageBody);

                mailSender.sendMessage(email);
            } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendWebinarInvitationToUser(WebinarUserPayment payment) throws EmailException {
        boolean isFree = ((payment.getPrice() == null) || (payment.getPrice().floatValue() == 0));
        Map<String, Object> model = new HashMap<>();
        Webinar webinar = payment.getWebinar();

        model.put(WEBINAR_NAME, webinar.getName());
        model.put(WEBINAR_DESCRIPTION, webinar.getDescription());
        model.put(WEBINAR_STARTDATE, DateUtils.formatFullDate(webinar.getStartDate()));
        if (!isFree) {
            model.put(WEBINAR_PAYLINK, createWebinarPath(payment, UrlUtils.PAYMENT_LINK));
        }

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(payment.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model,
                    isFree ? WEBINAR_FREE_INVITATION_TO_USER : WEBINAR_INVITATION_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("webinarInvitation"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarMultiplyInvitationToUser(WebinarMultiplyPayment payment) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        genericManager.initialize(payment, payment.getUsers());
        genericManager.initialize(payment, payment.getWebinar());

        Webinar webinar = payment.getWebinar();
        model.put(WEBINAR_NAME, webinar.getName());
        model.put(WEBINAR_DESCRIPTION, webinar.getDescription());
        model.put(WEBINAR_STARTDATE, DateUtils.formatFullDate(webinar.getStartDate()));
        model.put(WEBINAR_PAYLINK, createMWebinarPath(payment, UrlUtils.WEBINAR_MPAYMENT_PATH));

        List<String> names = new ArrayList<>();
        for (User user : payment.getUsers()) {
            names.add(user.getName());
        }
        model.put(USER_NAMES, names);

        InternetAddress recipient;
        User currentUser = EltilandSession.get().getCurrentUser();
        if (currentUser != null) {
            try {
                recipient = new InternetAddress(currentUser.getEmail());
                String messageBody = velocityMergeTool.mergeTemplate(model, WEBINAR_MULTIPLY_INVITATION_TO_USER);
                EmailMessage message = new EmailMessage();

                message.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                message.setSubject(mailHeadings.getProperty("webinarInvitation"));
                message.setRecipients(Arrays.asList(recipient));
                message.setText(messageBody);

                mailSender.sendMessage(message);
            } catch (AddressException | UnsupportedEncodingException | VelocityCommonException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    @Override
    public void sendRecordInvitationToUser(WebinarRecordPayment payment) throws EmailException {
        boolean isFree = ((payment.getPrice() == null) || (payment.getPrice().floatValue() == 0));
        Map<String, Object> model = new HashMap<>();

        genericManager.initialize(payment, payment.getRecord());
        genericManager.initialize(payment.getRecord(), payment.getRecord().getWebinar());
        genericManager.initialize(payment, payment.getUserProfile());
        model.put(WEBINAR_NAME, payment.getRecord().getWebinar().getName());
        if (!isFree) {
            model.put(WEBINAR_PAYLINK, createRecordPath(payment, UrlUtils.PAYMENT_LINK));
        }

        try {
            InternetAddress recipient = new InternetAddress(payment.getUserProfile().getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model,
                    isFree ? RECORD_FREE_INVITATION_TO_USER : RECORD_INVITATION_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("recordInvitation"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);

        } catch (UnsupportedEncodingException | AddressException | VelocityCommonException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendRecordLinkToUser(WebinarRecordPayment payment, InputStream fileStream) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        genericManager.initialize(payment, payment.getRecord());
        genericManager.initialize(payment, payment.getUserProfile());
        genericManager.initialize(payment.getRecord(), payment.getRecord().getWebinar());

        model.put(WEBINAR_NAME, payment.getRecord().getWebinar().getName());
        model.put(RECORD_LINKS, payment.getRecord().getLink().split(";"));
        model.put(PASSWORD, payment.getRecord().getPassword());

        try {
            InternetAddress recipient = new InternetAddress(payment.getUserProfile().getEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, RECORD_LINK_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("recordLink"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            FileContent fileContent = new FileContent();
            fileContent.setContent(IOUtils.toByteArray(fileStream));
            fileContent.setPath("Certificate.pdf");
            fileContent.setType(MimeType.PDF_TYPE);
            message.getFileContentList().add(fileContent);

            mailSender.sendMessage(message);
        } catch (AddressException | VelocityCommonException | IOException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarCertificate(WebinarUserPayment user, InputStream fileStream) throws EmailException {
        genericManager.initialize(user, user.getWebinar());
        genericManager.initialize(user.getWebinar(), user.getWebinar().getRecord());

        Map<String, Object> model = new HashMap<>();
        model.put(WEBINAR_NAME, user.getWebinar().getName());
        model.put(RECORD_LINKS, user.getWebinar().getRecord().getLink().split(";"));
        model.put(PASSWORD, user.getWebinar().getRecord().getPassword());

        try {
            InternetAddress recipient = new InternetAddress(user.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, WEBINAR_CERTIFICATE_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("webinarCertificate"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            FileContent fileContent = new FileContent();
            fileContent.setContent(IOUtils.toByteArray(fileStream));
            fileContent.setPath("Certificate.pdf");
            fileContent.setType(MimeType.PDF_TYPE);
            message.getFileContentList().add(fileContent);

            for (File file : fileManager.getFilesOfWebinar(user.getWebinar())) {
                genericManager.initialize(file, file.getBody());

                FileContent content = new FileContent();
                content.setPath(file.getName());
                content.setType(file.getType());
                content.setContent(IOUtils.toByteArray(
                        fileUtility.getFileResource(file.getBody().getHash()).getInputStream()));
                message.getFileContentList().add(content);
            }

            mailSender.sendMessage(message);
        } catch (AddressException | VelocityCommonException | IOException | ResourceStreamNotFoundException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarApplyManager(Webinar webinar, String managerEmail) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(WEBINAR_NAME, webinar.getName());
        model.put(PASSWORD, webinar.getPassword());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(managerEmail);

            String messageBody = velocityMergeTool.mergeTemplate(model, WEBINAR_MANAGER_APPLY);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(String.format(mailHeadings.getProperty("webinarManagerApply"), webinar.getName()));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarDenyToUser(WebinarUserPayment payment) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        Webinar webinar = payment.getWebinar();

        model.put(WEBINAR_NAME, webinar.getName());
        model.put(ADMIN_EMAIL, eltilandProps.getProperty("administrator.email"));

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(payment.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model, WEBINAR_DENY_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("webinarDeny"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarCreateToAdmin(Webinar webinar) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(WEBINAR_NAME, webinar.getName());
        model.put(PASSWORD, webinar.getPassword());

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(eltilandProps.getProperty("administrator.email"));

            String messageBody = velocityMergeTool.mergeTemplate(model, WEBINAR_CREATE_TO_ADMIN);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(String.format(mailHeadings.getProperty("webinarCreateToAdmin"), webinar.getName()));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }

    }

    @Override
    public void sendWebinarChangePriceToUser(WebinarUserPayment payment) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        BigDecimal price = payment.getPrice();
        boolean isFree = !((price != null) && (price.floatValue() != 0));

        model.put(WEBINAR_NAME, payment.getWebinar().getName());
        if (!isFree) {
            model.put(WEBINAR_PRICE, price);
        }

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(payment.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model,
                    isFree ? WEBINAR_CHANGEPRICE_TO_FREE_TO_USER : WEBINAR_CHANGEPRICE_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("webinarChangePrice"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarChangeRoleToUser(WebinarUserPayment payment) throws EmailException {
        Map<String, Object> model = new HashMap<>();
        boolean isModerator = payment.getRole().equals(WebinarUserPayment.Role.MODERATOR);

        model.put(WEBINAR_NAME, payment.getWebinar().getName());
        if (isModerator) {
            model.put(PASSWORD, payment.getWebinar().getPassword());
        }
        if (!isModerator) {
            model.put(WEBINAR_ROLE, payment.getRole().equals(
                    WebinarUserPayment.Role.MEMBER) ? "Участник" : "Обозреватель");
        }

        InternetAddress recipient;
        try {
            recipient = new InternetAddress(payment.getUserEmail());

            String messageBody = velocityMergeTool.mergeTemplate(model,
                    isModerator ? WEBINAR_CHANGEROLEADMIN_TO_USER : WEBINAR_CHANGEROLE_TO_USER);

            EmailMessage message = new EmailMessage();

            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));

            message.setSubject(mailHeadings.getProperty("webinarChangeRole"));
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);

            mailSender.sendMessage(message);
        } catch (VelocityCommonException | UnsupportedEncodingException | AddressException e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    @Override
    public void sendWebinarMessageToListeners(Webinar webinar, String text, boolean sentCertificate,
                                              boolean sentFiles, boolean sentRecords, String header) throws EmailException {
        Map<String, Object> model = new HashMap<>();

        model.put(WEBINAR_NAME, webinar.getName());

        if (sentRecords) {
            genericManager.initialize(webinar, webinar.getRecord());
            model.put(RECORD_LINKS, webinar.getRecord().getLink().split(";"));
            model.put(PASSWORD, webinar.getRecord().getPassword());
        }
        model.put(MESSAGE_TEXT, text);


        List<WebinarUserPayment> webinarUserPayments = webinarUserPaymentManager.getWebinarRealListeners(webinar);
        for (WebinarUserPayment payment : webinarUserPayments) {
            InternetAddress recipient;
            try {
                recipient = new InternetAddress(payment.getUserEmail());

                String messageBody = velocityMergeTool.mergeTemplate(model,
                        sentRecords ? WEBINAR_MESSAGE_WITH_RECORDS_TO_USER : WEBINAR_MESSAGE_TO_USER);

                EmailMessage message = new EmailMessage();

                message.setSender(new InternetAddress(
                        mailHeadings.getProperty("robotFromEmail"),
                        mailHeadings.getProperty("robotFromName"),
                        "UTF-8"));

                if (sentCertificate && payment.isCert()) {
                    FileContent fileContent = new FileContent();
                    fileContent.setContent(IOUtils.toByteArray(
                            webinarCertificateGenerator.generateCertificate(payment)));
                    fileContent.setPath("Certificate.pdf");
                    fileContent.setType(MimeType.PDF_TYPE);
                    message.getFileContentList().add(fileContent);
                }

                if (sentFiles) {
                    for (File file : fileManager.getFilesOfWebinar(webinar)) {
                        genericManager.initialize(file, file.getBody());

                        FileContent content = new FileContent();
                        content.setPath(file.getName());
                        content.setType(file.getType());
                        content.setContent(IOUtils.toByteArray(
                                fileUtility.getFileResource(file.getBody().getHash()).getInputStream()));
                        message.getFileContentList().add(content);
                    }
                }


                message.setSubject(header == null ? mailHeadings.getProperty("webinarListenersMessage") : header);
                message.setRecipients(Arrays.asList(recipient));
                message.setText(messageBody);

                mailSender.sendMessage(message);
                LOGGER.info(String.format("message send to %s", recipient));
            } catch (VelocityCommonException | UnsupportedEncodingException |
                    AddressException | ResourceStreamNotFoundException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            } catch (EltilandManagerException | IOException e) {
                throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
            }
        }
    }

    private void sendRegistrationConfirmationEmail(Map<String, Object> model, InternetAddress recipient,
                                                   String template, String subject, String footer)
            throws EmailException {
        try {
            String messageBody = velocityMergeTool.mergeTemplate(model, template);

            if (footer != null) {
                messageBody += footer;
            }

            EmailMessage message = new EmailMessage();
            message.setSender(new InternetAddress(
                    mailHeadings.getProperty("robotFromEmail"),
                    mailHeadings.getProperty("robotFromName"),
                    "UTF-8"));
            message.setSubject(subject);
            message.setRecipients(Arrays.asList(recipient));
            message.setText(messageBody);
            mailSender.sendMessage(message);
        } catch (Exception e) {
            throw new EmailException(EmailException.SEND_MAIL_ERROR, e);
        }
    }

    private Confirmation getConfirmation(User user) {
        return confirmationManager.getConfirmationByUser(user);
    }

    private ResetCode getResetCode(User user) {
        return resetPassManager.createResetCode(user, DateTime.now().plusDays(getConfirmationDays()).toDate());
    }

    private String createPath(Confirmation confirmation, String basePath) {
        if (confirmation != null) {
            basePath += "?" + UrlUtils.SECRET_CODE_PARAMETER_NAME + "=" + confirmation.getCode();
        }

        if (basePath == null) {
            return eltilandProps.getProperty("application.base.url");
        } else {
            return eltilandProps.getProperty("application.base.url") + basePath;
        }
    }

    private String createResetPath(ResetCode resetCode, String basePath) {
        if (resetCode != null) {
            basePath += "?" + UrlUtils.RESET_CODE_PARAMETER_NAME + "=" + resetCode.getCode();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createDownloadMagazineLink(Client client, String basePath) {
        if (client != null) {
            basePath += "?" + UrlUtils.DLINK_PARAMETER_NAME + "=" + client.getCode();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createUnsubscribePath(Subscriber subscriber, String basePath) {
        if (subscriber != null) {
            basePath += "?" + UrlUtils.UNSUBSCRIBE_CODE_PARAMETER_NAME + "=" + subscriber.getUnsubscribe();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createWebinarPath(WebinarUserPayment payment, String basePath) {
        if (payment != null) {
            basePath += "?" + PaymentPage.PARAM_ID + "=" + payment.getId();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createMWebinarPath(WebinarMultiplyPayment payment, String basePath) {
        if (payment != null) {
            basePath += "?" + UrlUtils.PAYMENT_CODE_PARAMETER_NAME + "=" + payment.getPayLink();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createRecordPath(WebinarRecordPayment payment, String basePath) {
        if (payment != null) {
            basePath += "?" + PaymentPage.PARAM_ID + "=" + payment.getId();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private String createCoursePayPath(ELTCourseListener listener, String basePath) {
        if (listener != null) {
            basePath += "?" + PaymentPage.PARAM_ID + "=" + listener.getId();
        }
        return eltilandProps.getProperty("application.base.url") + basePath;
    }

    private int getConfirmationDays() {
        return Integer.decode(eltilandProps.getProperty("confirmation.days")).intValue();
    }
}
