package org.openmrs.module.visits.api.exception;

public class VisitsRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5L;

    public VisitsRuntimeException(String message) {
        super(message);
    }

    public VisitsRuntimeException(String message, Throwable exception) {
        super(message, exception);
    }

    public VisitsRuntimeException(Throwable throwable) {
        super(throwable);
    }
}
