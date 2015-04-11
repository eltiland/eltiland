package com.eltiland.exceptions;

/**
 * @author knorr
 * @version 1.0
 * @since 8/3/12
 */
public class VelocityCommonException extends Exception {

    public VelocityCommonException(String message) {
        super(message);
    }

    public VelocityCommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public VelocityCommonException(Throwable cause) {
        super(cause);
    }

    public VelocityCommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
