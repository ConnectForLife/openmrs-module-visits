package org.openmrs.module.visits.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.module.visits.util.VisitsUiConstants;
import org.openmrs.module.visits.web.model.Notification;
import org.openmrs.module.visits.web.model.builder.NotificationBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Exposes the endpoints related to system notifications
 */
@Controller
@RequestMapping("/visits")
public class NotificationController extends BaseRestController {

    /**
     * Fetch the all system notifications.
     * Information about notification is taken from HTTP request session attribute.
     * Clear related session attributes after fetching information.
     *
     * @return a list of system notification
     */
    @RequestMapping(value = "/notifications", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getNotifications(HttpServletRequest request) {
        final List<Notification> notifications = new ArrayList<>();
        final HttpSession session = request.getSession();
        final String toastMessage = toStringWithEscape(session.getAttribute(VisitsUiConstants.EMR_TOAST_MESSAGE));
        if (StringUtils.isNotBlank(toastMessage) && "true".equals(toastMessage)) {
            notifications.add(new NotificationBuilder()
                    .withErrorMessage(toStringWithEscape(session.getAttribute(VisitsUiConstants.EMR_ERROR_MESSAGE)))
                    .withInfoMessage(toStringWithEscape(session.getAttribute(VisitsUiConstants.EMR_INFO_MESSAGE)))
                    .build());

            clearToastSessionAttributes(session);
        }
        return notifications;
    }

    private void clearToastSessionAttributes(HttpSession session) {
        session.setAttribute(VisitsUiConstants.EMR_ERROR_MESSAGE, null);
        session.setAttribute(VisitsUiConstants.EMR_INFO_MESSAGE, null);
        session.setAttribute(VisitsUiConstants.EMR_TOAST_MESSAGE, null);
    }
}
