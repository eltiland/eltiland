package com.eltiland.ui.common.components.grid;

/**
 * Enum for grid actions.
 *
 * @author Aleksey Plotnikov.
 */
public enum GridAction {
    EDIT, REMOVE, ADD, DOWNLOAD, PLAY, APPLY, CHILDREN, INFO,
    PREVIEW, UP, DOWN, PAGE_PREVIEW, EXPORT_EXCEL,
    CONTROL_SET, CONTROL_RESET, PAY, FULL_APPLY, EDIT_PAYMENT,
    UPLOAD, SEND, USERS, CERTIFICATE, PROFILE, ON, OFF, SETTINGS, LOCK, UNLOCK, TIME, COURSE;

    @Override
    public String toString() {
        switch (this) {
            case APPLY:
                return "apply16";
            case EDIT:
                return "edit16";
            case REMOVE:
                return "cancel16";
            case ADD:
                return "add16";
            case DOWNLOAD:
                return "download16";
            case PLAY:
                return "play16";
            case CHILDREN:
                return "children16";
            case INFO:
                return "info16";
            case PREVIEW:
                return "preview16";
            case UP:
                return "up16";
            case DOWN:
                return "down16";
            case PAGE_PREVIEW:
                return "pagePreview16";
            case EXPORT_EXCEL:
                return "exportExcel16";
            case CONTROL_SET:
                return "controlSet16";
            case CONTROL_RESET:
                return "controlReset16";
            case PAY:
                return "pay16";
            case FULL_APPLY:
                return "full_apply16";
            case EDIT_PAYMENT:
                return "edit_payment16";
            case UPLOAD:
                return "upload16";
            case SEND:
                return "send16";
            case USERS:
                return "users16";
            case CERTIFICATE:
                return "certificate16";
            case PROFILE:
                return "profile16";
            case ON:
                return "on16";
            case OFF:
                return "off16";
            case SETTINGS:
                return "settings16";
            case LOCK:
                return "lock16";
            case UNLOCK:
                return "unlock16";
            case TIME:
                return "time16";
            case COURSE:
                return "course16";
            default:
                return "";
        }
    }
}
