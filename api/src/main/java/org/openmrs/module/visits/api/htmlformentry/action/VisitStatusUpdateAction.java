package org.openmrs.module.visits.api.htmlformentry.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.CustomFormSubmissionAction;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.visits.api.decorator.VisitDecorator;
import org.openmrs.module.visits.api.service.ConfigService;
import org.openmrs.module.visits.api.service.VisitService;

/**
 * The VisitStatusUpdateAction Class.
 * <p>
 * The implementation of Post Submission Action which sets the Visits Status (Visit attribute) to correct status after
 * the Visit occurred.
 * </p>
 * <p>
 * Example: <br />
 * my-form.xml: <br />
 * {@code <postSubmissionAction class="org.openmrs.module.visits.api.htmlformentry.action.VisitStatusUpdateAction"/>}
 * </p>
 */
public class VisitStatusUpdateAction implements CustomFormSubmissionAction {
    private static final String VISIT_SERVICE = "visits.visitService";
    private static final String CONFIG_SERVICE = "visits.configService";

    private static final Log LOGGER = LogFactory.getLog(VisitStatusUpdateAction.class);

    @Override
    public void applyAction(FormEntrySession formEntrySession) {
        if (formEntrySession.getContext().getVisit() != null) {
            final VisitDecorator visitDecorator = new VisitDecorator((Visit) formEntrySession.getContext().getVisit());
            visitDecorator.setStatus(getConfigService().getStatusOfOccurredVisit());
            visitDecorator.setChanged();
            getVisitService().saveOrUpdate(visitDecorator.getObject());
            LOGGER.info(String.format("Visit with uuid: %s has successfully changed the status.", visitDecorator.getUuid()));
        }
    }

    private ConfigService getConfigService() {
        return Context.getRegisteredComponent(CONFIG_SERVICE, ConfigService.class);
    }

    private VisitService getVisitService() {
        return Context.getRegisteredComponent(VISIT_SERVICE, VisitService.class);
    }
}
