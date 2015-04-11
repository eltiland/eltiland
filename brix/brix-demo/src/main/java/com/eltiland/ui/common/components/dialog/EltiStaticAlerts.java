package com.eltiland.ui.common.components.dialog;

import org.apache.wicket.Session;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.IHeaderResponse;

import java.io.Serializable;

/**
 * Utility class encapsulating work with Growl-like notifications (popup showing for some time on error or successfull
 * action and fading out.
 * <p/>
 * If a user clicks on the popup during its fadeout it will stop disappearing.
 * <p/>
 * <b>Use if you need to show alert after page redirect.</b>
 * To deal with with this class invoke {@link EltiStaticAlerts#registerOKPopup(String)}.
 */
public final class EltiStaticAlerts {
    /**
     * Filter  returns success messages.
     */
    public static final IFeedbackMessageFilter SUCCESS_MESSAGES = new IFeedbackMessageFilter() {
        @Override
        public boolean accept(FeedbackMessage message) {
            return message.isSuccess();
        }
    };
    /**
     * Filter  returns success messages.
     */
    public static final IFeedbackMessageFilter ERROR_MESSAGE = new IFeedbackMessageFilter() {
        @Override
        public boolean accept(FeedbackMessage message) {
            return message.isError();
        }
    };

    private EltiStaticAlerts() {
    }

    /**
     * Registers message that will be shown on next page.
     *
     * @param message message OK message to show. May contain HTML.
     */
    public static void registerOKPopup(String message) {
        Session.get().success(message);
    }

    /**
     * Registers message that will be shown on next page.
     *
     * @param message message OK message to show. May contain HTML.
     */
    public static void registerErrorPopup(String message) {
        Session.get().error(message);
    }

    /**
     * Registers message that will be shown on next page in modal mode.
     *
     * @param message message OK message to show. May contain HTML.
     */
    public static void registerOKPopupModal(String message) {
        Session.get().getFeedbackMessages().add(new ModalFeedbackMessage(message, FeedbackMessage.SUCCESS));
        Session.get().dirty();
    }

    /**
     * Renders an 'OK' popup for the confirmation of some action happened (green 'OK' sign).
     * Call in BasePage to show registered messages.
     *
     * @param response ajax request target
     */
    public static void renderOKPopups(IHeaderResponse response) {

        for (FeedbackMessage message : Session.get().getFeedbackMessages().messages(SUCCESS_MESSAGES)) {
            // for now function alertInfo() can render last message
            if (message instanceof ModalFeedbackMessage) {
                response.renderOnDomReadyJavaScript(ELTAlerts.getOKPopupModalJS(message.getMessage().toString()));
            } else {
                response.renderOnDomReadyJavaScript(ELTAlerts.getOKPopupJS(message.getMessage().toString()));
            }
            message.markRendered();
        }
    }

    /**
     * Renders an 'OK' popup for the confirmation of some action happened (green 'OK' sign).
     * Call in BasePage to show registered messages.
     *
     * @param response ajax request target
     */
    public static void renderErrorPopups(IHeaderResponse response) {

        for (FeedbackMessage message : Session.get().getFeedbackMessages().messages(ERROR_MESSAGE)) {
            // for now function alertInfo() can render last message
            if (message instanceof ModalFeedbackMessage) {
                response.renderOnDomReadyJavaScript(ELTAlerts.getErrorPopupModalJS(message.getMessage().toString()));
            } else {
                response.renderOnDomReadyJavaScript(ELTAlerts.getErrorPopupJS(message.getMessage().toString()));
            }
            message.markRendered();
        }
    }

    private static class ModalFeedbackMessage extends FeedbackMessage {
        /**
         * Construct using fields.
         *
         * @param message The actual message. Must not be <code>null</code>.
         * @param level   The level of the message
         */
        public ModalFeedbackMessage(Serializable message, int level) {
            super(null, message, level);
        }
    }

}
