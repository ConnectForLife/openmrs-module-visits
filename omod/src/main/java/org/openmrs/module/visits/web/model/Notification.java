package org.openmrs.module.visits.web.model;

/**
 * System notification DTO object.
 */
public class Notification extends AbstractDTO {

    private static final long serialVersionUID = 5615857731418557904L;

    /**
     * Indicates the notification type.
     */
    private boolean errorMessage;

    /**
     * Provides the notification message.
     */
    private String message;

    public boolean isErrorMessage() {
        return errorMessage;
    }

    public Notification setErrorMessage(boolean errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Notification setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "errorMessage=" + errorMessage +
                ", message='" + message + '\'' +
                '}';
    }
}
