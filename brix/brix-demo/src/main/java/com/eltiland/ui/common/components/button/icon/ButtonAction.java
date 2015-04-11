package com.eltiland.ui.common.components.button.icon;

/**
 * Enum of the icon button actions.
 *
 * @author Aleksey Plotnikov.
 */
public enum ButtonAction {
    PREVIEW, EDIT, ADD, TAG, COLLECTION, DOWNLOAD, REMOVE, SEND, SETTINGS, SUPPORT, ENTER, UPLOAD, PAY, PAYMENT, BACK;

    @Override
    public String toString() {
        switch (this) {
            case PREVIEW:
                return "previewAction";
            case EDIT:
                return "editAction";
            case ADD:
                return "addAction";
            case SETTINGS:
                return "settingsAction";
            case TAG:
                return "tagAction";
            case COLLECTION:
                return "collectionAction";
            case DOWNLOAD:
                return "downloadAction";
            case REMOVE:
                return "removeAction";
            case SEND:
                return "sendAction";
            case SUPPORT:
                return "supportAction";
            case ENTER:
                return "enterAction";
            case UPLOAD:
                return "uploadAction";
            case PAY:
                return "payAction";
            case PAYMENT:
                return "paymentAction";
            case BACK:
                return "backAction";
            default:
                return "";
        }
    }
}
