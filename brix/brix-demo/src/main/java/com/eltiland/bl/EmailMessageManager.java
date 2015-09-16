package com.eltiland.bl;

import com.eltiland.exceptions.EmailException;
import com.eltiland.model.Pei;
import com.eltiland.model.course.Course;
import com.eltiland.model.course.CourseInvoice;
import com.eltiland.model.course.CourseListener;
import com.eltiland.model.course.CourseSession;
import com.eltiland.model.course2.ELTCourse;
import com.eltiland.model.course2.listeners.ELTCourseListener;
import com.eltiland.model.file.File;
import com.eltiland.model.magazine.Client;
import com.eltiland.model.subscribe.Email;
import com.eltiland.model.user.User;
import com.eltiland.model.webinar.Webinar;
import com.eltiland.model.webinar.WebinarMultiplyPayment;
import com.eltiland.model.webinar.WebinarRecordPayment;
import com.eltiland.model.webinar.WebinarUserPayment;

import javax.mail.internet.AddressException;
import java.io.InputStream;

/**
 * @author knorr
 * @version 1.0
 * @since 7/25/12
 */
public interface EmailMessageManager {

    /**
     * Send user email about registration on the portal.
     *
     * @param user user-receiver
     * @throws EmailException if cannot send email
     */
    void sendEmailToUserRegistered(User user) throws EmailException;

    /**
     * Send confirmation letter to user.
     *
     * @param user new user
     */
    void sendEmailConfirmationToUser(User user) throws EmailException;

    /**
     * Send letter to user, about it's confirmation by admin.
     *
     * @param email email of the new user.
     * @param pass  not crypted password for new user.
     */
    void sendConfirmationMessage(String email, String pass) throws EmailException;

    /**
     * Send email to PEI administration that their PEI has approved.
     *
     * @param pei PEI
     * @throws AddressException if the PEI address parse failed
     * @throws EmailException   if cannot send email
     */
    void sendEmailToPeiRegistrationApproved(Pei pei) throws AddressException, EmailException;

    /**
     * Send email request to reset password
     *
     * @param user - user to reset password
     */
    void sendEmailToUserResetPasswordRequest(User user) throws AddressException, EmailException;

    /******************************************************************************************************************/
    /* Files stuff
    */

    /**
     * Sending message about uploading file to user by current user.
     *
     * @param author   user-author of file.
     * @param receiver user-receiver of file.
     * @param file     uploaded file.
     */
    void sendFileUploadMessage(User author, User receiver, File file) throws EmailException;

    /**
     * Sending message to course author about uploading file by listener.
     *
     * @param author user-author of file.
     * @param course course item.
     * @param file   uploaded file.
     */
    void sendFileCourseUploadMessage(User author, ELTCourse course, File file) throws EmailException;

    /******************************************************************************************************************/
    /* Webinars stuff
    */

    /**
     * Sending pay invitation to user.
     *
     * @param payment webinar user payment data.
     */
    void sendWebinarInvitationToUser(WebinarUserPayment payment) throws EmailException;

    /**
     * Sending pay invitation to user (about couple of users).
     *
     * @param payment webinar couple user payment data.
     */
    void sendWebinarMultiplyInvitationToUser(WebinarMultiplyPayment payment) throws EmailException;

    /**
     * Sending pay invitation to user.
     *
     * @param payment webinar record payment data.
     */
    void sendRecordInvitationToUser(WebinarRecordPayment payment) throws EmailException;

    /**
     * Sending record link to user
     *
     * @param payment    webinar record payment data.
     * @param fileStream stream to certificate.
     */
    void sendRecordLinkToUser(WebinarRecordPayment payment, InputStream fileStream) throws EmailException;

    /**
     * Sending webinar certificate to user.
     *
     * @param user       webinar user data.
     * @param fileStream stream to certificate
     */
    void sendWebinarCertificate(WebinarUserPayment user, InputStream fileStream) throws EmailException;

    /**
     * Sending message to webinar manager bout it's appointment.
     *
     * @param webinar      created webinar.
     * @param managerEmail email of manager.
     */
    void sendWebinarApplyManager(Webinar webinar, String managerEmail) throws EmailException;

    /**
     * Sending message about reject of taking part in the webinar.
     *
     * @param payment webinar user payment data.
     */
    void sendWebinarDenyToUser(WebinarUserPayment payment) throws EmailException;

    /**
     * Sending message to site admin about creating webinar.
     *
     * @param webinar webinar to create.
     */
    void sendWebinarCreateToAdmin(Webinar webinar) throws EmailException;

    /**
     * Sending message about changing price for user.
     *
     * @param payment webinar user payment data.
     */
    void sendWebinarChangePriceToUser(WebinarUserPayment payment) throws EmailException;

    /**
     * Sending message about changing role in webinar for user.
     *
     * @param payment webinar user payment data.
     */
    void sendWebinarChangeRoleToUser(WebinarUserPayment payment) throws EmailException;

    /**
     * Sending message to all webinar users.
     *  @param webinar         webinar entity.
     * @param text            message text.
     * @param sentCertificate if TRUE - sent also certificate file
     * @param sentFiles       if TRUE - sent also attached files
     * @param sentRecords     if TRUE - sent also links to the webinar records.
     * @param header
     */
    void sendWebinarMessageToListeners(Webinar webinar, String text, boolean sentCertificate,
                                       boolean sentFiles, boolean sentRecords, String header) throws EmailException;


    /******************************************************************************************************************/
    /* Courses stuff
    */

    /**
     * Sending message to admin about invoice to create course by user.
     *
     * @param author author of the course.
     * @param course course to create.
     */
    void sendInvoiceCreateCourseToAdmin(User author, Course course) throws EmailException;

    /**
     * Sending message to course author about applying his course.
     *
     * @param course course to apply.
     */
    void sendEmailApplyCourseToUser(ELTCourse course) throws EmailException;

    /**
     * Sending message to course author about denying his course.
     *
     * @param course course to apply.
     */
    void sendEmailDenyCourseToUser(ELTCourse course) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending given message to all subscribers.
     *
     * @param email email to send.
     */
    void sendSubscribe(Email email) throws EmailException;

    /**
     * Sending message to current active user
     *
     * @param email email to send
     * @throws EmailException
     */
    void sendSubscribeToCurrentUser(Email email) throws EmailException;

    void sendSubscribeToUser(Email email, String userMail) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending link to doenloading of magazine to client.
     *
     * @param client client information.
     */
    void sendDownloadMagazineLink(Client client) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending information to admin about current magazine transaction.
     *
     * @param client client information.
     */
    void sendMagazineActionToAdmin(Client client) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to administrator about granting access to the course.
     *
     * @param invoice course access invoice.
     */
    void sendCourseAccessInvoiceToAdmin(CourseInvoice invoice) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to listener about submitting course invoice.
     *
     * @param listener course listener
     */
    void sendCourseAccessGrantedToUser(ELTCourseListener listener) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to listener about denied course invoice.
     *
     * @param invoice course access invoice.
     */
    void sendCourseAccessDeniedToUser(CourseInvoice invoice) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to specified .
     *
     * @param courseName     course name.
     * @param message        message text.
     * @param recepientEmail email of the message recepient.
     */
    void sendCourseMessage(String courseName, String message, String recepientEmail) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to all listeners of the specified course
     *
     * @param course      course item.
     * @param messageText message text.
     * @param isConfirmed if TRUE - only for confirmed users.
     */
    void sendCourseListenerMessage(ELTCourse course, String messageText, boolean isConfirmed) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message to course listener.
     *
     * @param listener    listener item.
     * @param messageText message text.
     */
    void sendCourseListenerMessage(ELTCourseListener listener, String messageText) throws EmailException;

    /******************************************************************************************************************/
    /**
     * Sending message about user payment to author of the course.
     *
     * @param listener listener information
     */
    void sendCoursePayMessage(ELTCourseListener listener) throws EmailException;

    /**
     * **************************************************************************************************************
     */
    /* Training courses stuff

     /*
     * Sending message to user, when he registered to the training course.
     *
     * @param listener listener information.
     */
    void sendTCUserInvoicePhysical(CourseListener listener) throws EmailException;

    /*
    * Sending message to admin, when he registered to the training course.
    *
    * @param listener listener information.
    */
    void sendTCUserInvoiceToAdminPhysical(CourseListener listener) throws EmailException;

    /*
    * Sending message to user, when his invoice to access to the training course is accepted.
    *
    * @param listener listener information.
    */
    void sendTCUserAccepted(ELTCourseListener listener) throws EmailException;

    /*
    * Sending message to user, when his invoice to access to the training course is declined.
    *
    * @param listener listener information.
    */
    void sendTCUserDeclined(ELTCourseListener listener) throws EmailException;

    /*
    * Sending message to user, when he accepted to paying.
    *
    * @param listener listener information.
    */
    void sendTCUserPayAccepted(ELTCourseListener listener) throws EmailException;

    /*
    * Sending message to user, when his paying is accepted.
    *
    * @param listener listener information.
    */
    void sendTCUserPaid(ELTCourseListener listener) throws EmailException;
}