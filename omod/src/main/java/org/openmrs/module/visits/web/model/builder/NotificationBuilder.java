package org.openmrs.module.visits.web.model.builder;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.visits.web.model.Notification;

/**
 * Builds the notification DTO based on provided information.
 */
public class NotificationBuilder {

    private String errorMessage;
    private String infoMessage;

    public Notification build() {
        boolean isErrorMessage = StringUtils.isNotBlank(this.errorMessage);
        return new Notification()
                .setErrorMessage(isErrorMessage)
                .setMessage(isErrorMessage ? this.errorMessage : this.infoMessage);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public NotificationBuilder withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public NotificationBuilder withInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
        return this;
    }
}
