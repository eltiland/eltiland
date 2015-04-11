package com.eltiland.exceptions;

/**
 * Used by Generic manager. Should be used where the creation/update/deletion of entities can not be
 * more specific.
 */
public class ConstraintException extends Exception {
    public ConstraintException() {
    }

    public ConstraintException(String message) {
        super(message);
    }

    public ConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintException(Throwable cause) {
        super(cause);
    }
}
