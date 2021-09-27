package org.openmrs.module.visits.api.scheduler.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.visits.api.service.VisitService;

public class MissedVisitsStatusChangerJobDefinition extends JobDefinition {

    private static final Log LOGGER = LogFactory.getLog(MissedVisitsStatusChangerJobDefinition.class);
    private static final String TASK_NAME = "Missed Visits Status Changer";

    @Override
    public void execute() {
        LOGGER.info(getTaskName() + " started");
        getVisitService().changeStatusForMissedVisits();
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public boolean shouldStartAtFirstCreation() {
        return true;
    }

    @Override
    public Class getTaskClass() {
        return MissedVisitsStatusChangerJobDefinition.class;
    }

    private VisitService getVisitService() {
        return Context.getRegisteredComponent("visits.visitService", VisitService.class);
    }
}
