package com.eltiland.ui.common.components.dialog;

import com.eltiland.ui.common.components.UIConstants;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Utility class encapsulating work with Growl-like notifications (popup showing for some time on error or successfull
 * action and fading out.
 * <p/>
 * If a user clicks on the popup during its fadeout it will stop disappearing.
 */
public final class ELTAlerts {
    private ELTAlerts() {
    }

    /**
     * Renders an error popup message in the top or bottom right corner and fading slowly to the nothing.
     *
     * @param message message to show. May contain HTML.
     * @param target  ajax request target
     */
    public static void renderErrorPopup(String message, AjaxRequestTarget target) {
        target.appendJavaScript(getErrorPopupJS(message));
    }

    /**
     * Renders an error popup message in the top or bottom right corner and fading slowly to the nothing.
     *
     * @param message  message to show. May contain HTML.
     * @param response header response
     */
    public static void renderErrorPopup(String message, IHeaderResponse response) {
        response.renderOnDomReadyJavaScript(getErrorPopupJS(message));
    }


    /**
     * Renders an modal 'OK' popup for the confirmation of some action happened (green 'OK' sign).
     *
     * @param message OK message to show. May contain HTML.
     * @param target  ajax request target
     */
    public static void renderOKPopupModal(String message, AjaxRequestTarget target) {
        target.appendJavaScript(getOKPopupModalJS(message));
    }

    /**
     * Renders an 'OK' popup for the confirmation of some action happened (green 'OK' sign).
     *
     * @param message OK message to show. May contain HTML.
     * @param target  ajax request target
     */
    public static void renderOKPopup(String message, AjaxRequestTarget target) {
        target.appendJavaScript(getOKPopupJS(message));
    }

    /**
     * Renders an 'Warning' popup for the warning if something happens (yellow 'Warning' triangle).
     *
     * @param message Warning message to show. May contain HTML.
     * @param target  ajax request target
     */
    public static void renderWarningPopup(String message, AjaxRequestTarget target) {
        target.appendJavaScript(getWarningPopupJS(message));
    }

    static String getWarningPopupJS(String message) {
        return String.format("alertWarning('%s', %d, %d)", message,
                UIConstants.ALERT_SHOW_TIME_SHORT, UIConstants.ALERT_FADEOUT_TIME);
    }

    static String getErrorPopupJS(String message) {
        return String.format("alertError('%s', %d, %d)", message,
                UIConstants.ALERT_SHOW_TIME_SHORT, UIConstants.ALERT_FADEOUT_TIME);
    }

    static String getOKPopupJS(String message) {
        return String.format("alertInfo('%s', %d, %d)", message,
                UIConstants.ALERT_SHOW_TIME_SHORT, UIConstants.ALERT_FADEOUT_TIME);
    }

    static String getOKPopupModalJS(String message) {
        return String.format("alertInfo('%s', %d, %d, 'center',true)", message,
                UIConstants.ALERT_SHOW_TIME_LONG, UIConstants.ALERT_FADEOUT_TIME);
    }

    static String getErrorPopupModalJS(String message) {
        return String.format("alertError('%s', %d, %d, 'center',true)", message,
                UIConstants.ALERT_SHOW_TIME_LONG, UIConstants.ALERT_FADEOUT_TIME);
    }
}
